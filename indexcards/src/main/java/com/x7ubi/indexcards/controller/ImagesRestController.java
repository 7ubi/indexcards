package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.response.image.ImageResponse;
import com.x7ubi.indexcards.service.image.ImageStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/images")
public class ImagesRestController {

    private final Logger logger = LoggerFactory.getLogger(ImagesRestController.class);

    private final ImageStorageService imageStorageService;

    public ImagesRestController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ImageResponse> createImage(
            @RequestParam("file") MultipartFile file
    ) throws EntityCreationException {
        logger.info("Uploading image");

        UUID imageId = this.imageStorageService.store(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ImageResponse(imageId));
    }

    @GetMapping("/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getImage(@PathVariable UUID imageId) throws EntityNotFoundException {
        byte[] data = this.imageStorageService.load(imageId);

        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(data);
    }
}
