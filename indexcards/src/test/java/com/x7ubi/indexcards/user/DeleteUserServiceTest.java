package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.user.DeleteAccountRequest;
import com.x7ubi.indexcards.service.authentication.AuthentikUserDeletionService;
import com.x7ubi.indexcards.service.image.ImageStorageService;
import com.x7ubi.indexcards.service.user.DeleteUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:deleteusertestdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
// AFTER_EACH (not BEFORE_EACH): this class has its own context configuration (image storage path), so its last
// context must not stay cached and keep the shared in-memory H2 database (and other classes' rows) alive.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DeleteUserServiceTest extends TestConfig {

    @TempDir
    static Path storageDir;

    @DynamicPropertySource
    static void registerStorageDir(DynamicPropertyRegistry registry) {
        registry.add("app.images.storage-path", () -> storageDir.toString());
    }

    private static final String SUBJECT = "6a2f41a3-c54c-4fd4-9a2b-2f0b6c1e9a10";

    // The real service would call Authentik.
    @MockitoBean
    private AuthentikUserDeletionService authentikUserDeletionService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private IndexCardRepo indexCardRepo;

    @Autowired
    private IndexCardAssessmentRepo indexCardAssessmentRepo;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private DeleteUserService deleteUserService;

    private User user;

    private User user2;

    private Project project;

    private Project project2;

    private IndexCard indexCard;

    private IndexCard indexCard2;

    private UUID ownImageId;

    private UUID sharedImageId;

    private UUID otherUsersImageId;

    /**
     * The in-memory H2 "testdb" may still be kept open (with rows) by another test class's cached context, so start
     * and end with an empty database.
     */
    @AfterEach
    void clearDatabase() {
        this.indexCardRepo.deleteAll();
        this.projectRepo.deleteAll();
        this.userRepo.deleteAll();
        this.indexCardAssessmentRepo.deleteAll();
    }

    @BeforeEach
    void setup() throws EntityCreationException {
        clearDatabase();

        this.ownImageId = storeImage();
        this.sharedImageId = storeImage();
        this.otherUsersImageId = storeImage();

        this.user = saveUser("test");
        this.user.setOidcSubject(SUBJECT);
        this.user = this.userRepo.save(this.user);
        this.user2 = saveUser("test2");

        this.project = saveProject("TestProject", this.user);
        this.project2 = saveProject("OtherProject", this.user2);

        this.indexCard = saveIndexCard(this.project,
                "Question ![image](/api/images/" + this.ownImageId + ")",
                "Answer ![image](/api/images/" + this.sharedImageId + ")");
        saveIndexCard(this.project, "Second question", "Second answer");
        this.indexCard2 = saveIndexCard(this.project2,
                "Other question ![image](/api/images/" + this.otherUsersImageId + ")",
                "Other answer ![image](/api/images/" + this.sharedImageId + ")");

        assess(this.indexCard, Assessment.GOOD);
        assess(this.indexCard2, Assessment.BAD);
    }

    private UUID storeImage() throws EntityCreationException {
        return this.imageStorageService.store(
                new MockMultipartFile("file", "test.jpg", "image/jpeg", "image-bytes".getBytes()));
    }

    private User saveUser(String username) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setFirstname("Max");
        newUser.setSurname("Muster");
        newUser.setProjects(new ArrayList<>());
        return this.userRepo.save(newUser);
    }

    private Project saveProject(String name, User owner) {
        Project newProject = new Project(name, null);
        newProject.setUser(owner);
        return this.projectRepo.save(newProject);
    }

    private IndexCard saveIndexCard(Project owningProject, String question, String answer) {
        IndexCard newIndexCard = new IndexCard(
                question.getBytes(StandardCharsets.UTF_8), answer.getBytes(StandardCharsets.UTF_8));
        newIndexCard.setProject(owningProject);
        return this.indexCardRepo.save(newIndexCard);
    }

    private void assess(IndexCard card, Assessment assessment) {
        IndexCardAssessment indexCardAssessment = new IndexCardAssessment(assessment, LocalDateTime.now());
        this.indexCardAssessmentRepo.save(indexCardAssessment);
        IndexCard managedCard = this.indexCardRepo.findIndexCardByIndexcardId(card.getId());
        managedCard.setAssessment(assessment);
        managedCard.getAssessmentHistory().add(indexCardAssessment);
        this.indexCardRepo.save(managedCard);
    }

    private boolean imageExists(UUID imageId) {
        return Files.exists(storageDir.resolve(imageId + ".jpg"));
    }

    private DeleteAccountRequest request(String username) {
        DeleteAccountRequest deleteAccountRequest = new DeleteAccountRequest();
        deleteAccountRequest.setUsername(username);
        return deleteAccountRequest;
    }

    @Test
    public void deleteUserTest() throws EntityNotFoundException, UnauthorizedException, AuthentikDeletionException {
        // when
        this.deleteUserService.deleteUser(this.user.getUsername(), request(this.user.getUsername()));

        // then
        Assertions.assertTrue(this.userRepo.findByUsername(this.user.getUsername()).isEmpty());
        Assertions.assertNull(this.projectRepo.findProjectByProjectId(this.project.getId()));
        Assertions.assertNull(this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId()));
        Assertions.assertFalse(imageExists(this.ownImageId));

        // other user's data is untouched
        Assertions.assertTrue(this.userRepo.findByUsername(this.user2.getUsername()).isPresent());
        Assertions.assertNotNull(this.projectRepo.findProjectByProjectId(this.project2.getId()));
        Assertions.assertEquals(1, this.projectRepo.count());
        Assertions.assertEquals(1, this.indexCardRepo.count());
        IndexCard otherCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard2.getId());
        Assertions.assertNotNull(otherCard);
        Assertions.assertEquals(1, otherCard.getAssessmentHistory().size());
        Assertions.assertEquals(1, this.indexCardAssessmentRepo.count());
        Assertions.assertTrue(imageExists(this.otherUsersImageId));
        // still referenced by the other user's card, so it must not be removed
        Assertions.assertTrue(imageExists(this.sharedImageId));
    }

    @Test
    public void deleteUserWithWrongConfirmationTest() {
        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.deleteUserService.deleteUser(this.user.getUsername(), request(this.user2.getUsername())));

        // then
        Assertions.assertEquals(ErrorMessage.User.USERNAME_CONFIRMATION_MISMATCH, unauthorizedException.getMessage());
        assertNothingDeleted();
    }

    @Test
    public void deleteUserWithoutConfirmationTest() {
        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.deleteUserService.deleteUser(this.user.getUsername(), request(null)));

        // then
        Assertions.assertEquals(ErrorMessage.User.USERNAME_CONFIRMATION_MISMATCH, unauthorizedException.getMessage());
        assertNothingDeleted();
    }

    @Test
    public void deleteUserTrimsConfirmationTest() throws EntityNotFoundException, UnauthorizedException, AuthentikDeletionException {
        // when
        this.deleteUserService.deleteUser(this.user.getUsername(), request("  " + this.user.getUsername() + " "));

        // then
        Assertions.assertTrue(this.userRepo.findByUsername(this.user.getUsername()).isEmpty());
    }

    @Test
    public void deleteUnknownUserTest() {
        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.deleteUserService.deleteUser("unknown", request("unknown")));

        // then
        Assertions.assertEquals(ErrorMessage.Project.USERNAME_NOT_FOUND, entityNotFoundException.getMessage());
        assertNothingDeleted();
    }

    @Test
    public void deleteUserDeletesAuthentikUserTest()
            throws EntityNotFoundException, UnauthorizedException, AuthentikDeletionException {
        this.deleteUserService.deleteUser(this.user.getUsername(), request(this.user.getUsername()));

        verify(this.authentikUserDeletionService).deleteUser(SUBJECT);
        Assertions.assertTrue(this.userRepo.findByUsername(this.user.getUsername()).isEmpty());
    }

    // Not @Transactional on purpose: the service's own transaction must really roll back.
    @Test
    public void authentikFailureRollsBackTest() throws AuthentikDeletionException {
        doThrow(new AuthentikDeletionException("down")).when(this.authentikUserDeletionService)
                .deleteUser(anyString());

        Assertions.assertThrows(AuthentikDeletionException.class, () ->
                this.deleteUserService.deleteUser(this.user.getUsername(), request(this.user.getUsername())));

        assertNothingDeleted();
        Assertions.assertTrue(this.userRepo.findByUsername(this.user.getUsername()).isPresent());
    }

    @Test
    public void legacyUserSkipsAuthentikTest()
            throws EntityNotFoundException, UnauthorizedException, AuthentikDeletionException {
        this.deleteUserService.deleteUser(this.user2.getUsername(), request(this.user2.getUsername()));

        Assertions.assertTrue(this.userRepo.findByUsername(this.user2.getUsername()).isEmpty());
        verifyNoInteractions(this.authentikUserDeletionService);
    }

    private void assertNothingDeleted() {
        Assertions.assertEquals(2, this.userRepo.count());
        Assertions.assertEquals(2, this.projectRepo.count());
        Assertions.assertEquals(3, this.indexCardRepo.count());
        Assertions.assertEquals(2, this.indexCardAssessmentRepo.count());
        List<UUID> images = List.of(this.ownImageId, this.sharedImageId, this.otherUsersImageId);
        images.forEach(imageId -> Assertions.assertTrue(imageExists(imageId)));
    }
}
