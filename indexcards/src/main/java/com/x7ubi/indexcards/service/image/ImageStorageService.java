package com.x7ubi.indexcards.service.image;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Logger logger = LoggerFactory.getLogger(ImageStorageService.class);

    // The multipart limit is higher (AI PDF upload), so images get their own cap.
    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    private final Path storageDir;

    public ImageStorageService(@Value("${app.images.storage-path}") String storagePath) throws IOException {
        this.storageDir = Path.of(storagePath);
        Files.createDirectories(this.storageDir);
    }

    public UUID store(MultipartFile file) throws EntityCreationException {
        if (file == null || file.isEmpty() || file.getContentType() == null
                || !file.getContentType().startsWith("image/")) {
            throw new EntityCreationException(ErrorMessage.Images.INVALID_IMAGE);
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new EntityCreationException(ErrorMessage.Images.IMAGE_TOO_LARGE);
        }

        UUID imageId = UUID.randomUUID();

        try {
            Files.write(resolveImagePath(imageId), file.getBytes());
        } catch (IOException e) {
            logger.error("Failed to store image", e);
            throw new EntityCreationException(ErrorMessage.Images.INVALID_IMAGE);
        }

        return imageId;
    }

    public byte[] load(UUID imageId) throws EntityNotFoundException {
        Path imagePath = resolveImagePath(imageId);

        if (!Files.exists(imagePath)) {
            throw new EntityNotFoundException(ErrorMessage.Images.IMAGE_NOT_FOUND);
        }

        try {
            return Files.readAllBytes(imagePath);
        } catch (IOException e) {
            logger.error("Failed to load image", e);
            throw new EntityNotFoundException(ErrorMessage.Images.IMAGE_NOT_FOUND);
        }
    }

    /**
     * Deletes the stored image with the given id. Missing images are ignored.
     *
     * @return true if a file was deleted
     */
    public boolean delete(UUID imageId) {
        try {
            return Files.deleteIfExists(resolveImagePath(imageId));
        } catch (IOException e) {
            logger.error("Failed to delete image {}", imageId, e);
            return false;
        }
    }

    private Path resolveImagePath(UUID imageId) {
        return storageDir.resolve(imageId + ".jpg");
    }
}
