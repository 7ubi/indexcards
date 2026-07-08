package com.x7ubi.indexcards.image;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.service.image.ImageStorageService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Path;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
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
    public void storeRejectsNonImageContentTypeTest() {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "not-an-image".getBytes());

        // when
        EntityCreationException exception = Assert.assertThrows(EntityCreationException.class, () ->
                this.imageStorageService.store(file));

        // then
        Assertions.assertEquals(ErrorMessage.Images.INVALID_IMAGE, exception.getMessage());
    }

    @Test
    public void loadMissingImageThrowsNotFoundTest() {
        // when
        EntityNotFoundException exception = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.imageStorageService.load(UUID.randomUUID()));

        // then
        Assertions.assertEquals(ErrorMessage.Images.IMAGE_NOT_FOUND, exception.getMessage());
    }
}
