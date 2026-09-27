package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.user.DeleteAccountRequest;
import com.x7ubi.indexcards.service.image.ImageStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deletes a user account together with all of its data: projects, index cards, their assessment history and the
 * uploaded images referenced by the user's index cards.
 */
@Service
public class DeleteUserService {

    private final Logger logger = LoggerFactory.getLogger(DeleteUserService.class);

    /**
     * Images are not linked to users in the database; cards reference them in their Markdown as
     * {@code /api/images/<uuid>} (see the frontend's paste-image directive).
     */
    private static final Pattern IMAGE_REFERENCE = Pattern.compile(
            "/api/images/([0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12})");

    private static final int SCAN_PAGE_SIZE = 500;

    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    private final ImageStorageService imageStorageService;

    private final EntityManager entityManager;

    public DeleteUserService(
            UserRepo userRepo, PasswordEncoder passwordEncoder, ImageStorageService imageStorageService,
            EntityManager entityManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.imageStorageService = imageStorageService;
        this.entityManager = entityManager;
    }

    @Transactional
    public void deleteUser(String username, DeleteAccountRequest deleteAccountRequest)
            throws EntityNotFoundException, UnauthorizedException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            return new EntityNotFoundException(ErrorMessage.Project.USERNAME_NOT_FOUND);
        });

        String password = deleteAccountRequest == null ? null : deleteAccountRequest.getPassword();
        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            logger.error(ErrorMessage.User.WRONG_PASSWORD);
            throw new UnauthorizedException(ErrorMessage.User.WRONG_PASSWORD);
        }

        Set<UUID> imageIds = collectImageIds(user);
        // Never delete an image that another user's card still references (e.g. copied Markdown).
        imageIds.removeAll(findImageIdsReferencedByOtherUsers(user));

        // Cascades: User -> Project -> IndexCard -> IndexCardAssessment (CascadeType.REMOVE).
        userRepo.delete(user);
        userRepo.flush();

        deleteImagesAfterCommit(imageIds);

        logger.info("User account was deleted");
    }

    private Set<UUID> collectImageIds(User user) {
        Set<UUID> imageIds = new HashSet<>();
        if (user.getProjects() == null) {
            return imageIds;
        }

        for (Project project : user.getProjects()) {
            if (project.getIndexCards() == null) {
                continue;
            }
            for (IndexCard indexCard : project.getIndexCards()) {
                addImageIds(imageIds, indexCard.getQuestion());
                addImageIds(imageIds, indexCard.getAnswer());
            }
        }

        return imageIds;
    }

    private Set<UUID> findImageIdsReferencedByOtherUsers(User user) {
        Set<UUID> imageIds = new HashSet<>();

        int page = 0;
        List<Object[]> rows;
        do {
            // Scalar projection: does not load the other users' entities into the persistence context.
            rows = entityManager.createQuery(
                            "select c.question, c.answer from IndexCard c where c.project.user <> :user "
                                    + "order by c.indexcardId", Object[].class)
                    .setParameter("user", user)
                    .setFirstResult(page * SCAN_PAGE_SIZE)
                    .setMaxResults(SCAN_PAGE_SIZE)
                    .getResultList();

            for (Object[] row : rows) {
                addImageIds(imageIds, (byte[]) row[0]);
                addImageIds(imageIds, (byte[]) row[1]);
            }
            page++;
        } while (rows.size() == SCAN_PAGE_SIZE);

        return imageIds;
    }

    private static void addImageIds(Set<UUID> imageIds, byte[] text) {
        if (text == null) {
            return;
        }

        Matcher matcher = IMAGE_REFERENCE.matcher(new String(text, StandardCharsets.UTF_8));
        while (matcher.find()) {
            imageIds.add(UUID.fromString(matcher.group(1)));
        }
    }

    /**
     * Image files are not transactional; only remove them once the database deletion was committed.
     */
    private void deleteImagesAfterCommit(Set<UUID> imageIds) {
        if (imageIds.isEmpty()) {
            return;
        }

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteImages(imageIds);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                deleteImages(imageIds);
            }
        });
    }

    private void deleteImages(Set<UUID> imageIds) {
        imageIds.forEach(imageStorageService::delete);
        logger.info("Deleted {} image(s) of deleted user", imageIds.size());
    }
}
