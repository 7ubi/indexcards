package com.x7ubi.indexcards.image;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.service.image.ImageStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
// Close the context after this class so its H2 "testdb" connection pool does not keep the in-memory
// database (and its rows) alive for the other test classes.
@DirtiesContext
public class ImageStorageServiceTest extends TestConfig {

    @TempDir
    static Path storageDir;

    @DynamicPropertySource
    static void registerStorageDir(DynamicPropertyRegistry registry) {
        registry.add("app.images.storage-path", () -> storageDir.toString());
    }

    @Autowired
    private ImageStorageService imageStorageService;

    @Test
    public void storeAndLoadImageTest() throws EntityCreationException, EntityNotFoundException {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "image-bytes".getBytes());

        // when
        UUID imageId = this.imageStorageService.store(file);
        byte[] data = this.imageStorageService.load(imageId);

        // then
        Assertions.assertArrayEquals("image-bytes".getBytes(), data);
    }

    @Test
    public void storeRejectsTooLargeImageTest() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "big.jpg", "image/jpeg", new byte[5 * 1024 * 1024 + 1]);

        // when
        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.imageStorageService.store(file));

        // then
        Assertions.assertEquals(ErrorMessage.Images.IMAGE_TOO_LARGE, exception.getMessage());
    }

    @Test
    public void storeRejectsNonImageContentTypeTest() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "not-an-image".getBytes());

        // when
        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.imageStorageService.store(file));

        // then
        Assertions.assertEquals(ErrorMessage.Images.INVALID_IMAGE, exception.getMessage());
    }

    @Test
    public void loadMissingImageThrowsNotFoundTest() {
        // when
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.imageStorageService.load(UUID.randomUUID()));

        // then
        Assertions.assertEquals(ErrorMessage.Images.IMAGE_NOT_FOUND, exception.getMessage());
    }
}
