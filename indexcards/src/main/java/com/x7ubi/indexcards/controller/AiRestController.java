package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.ai.SaveApiKeyRequest;
import com.x7ubi.indexcards.response.ai.AiStatusResponse;
import com.x7ubi.indexcards.response.ai.GeneratedCardsResponse;
import com.x7ubi.indexcards.service.indexcard.GenerateIndexCardsService;
import com.x7ubi.indexcards.service.user.DeleteApiKeyService;
import com.x7ubi.indexcards.service.user.SaveApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ai")
public class AiRestController {

    private final Logger logger = LoggerFactory.getLogger(AiRestController.class);

    private final JwtUtils jwtUtils;

    private final GenerateIndexCardsService generateIndexCardsService;

    private final SaveApiKeyService saveApiKeyService;

    private final DeleteApiKeyService deleteApiKeyService;

    public AiRestController(
            JwtUtils jwtUtils, GenerateIndexCardsService generateIndexCardsService,
            SaveApiKeyService saveApiKeyService, DeleteApiKeyService deleteApiKeyService) {
        this.jwtUtils = jwtUtils;
        this.generateIndexCardsService = generateIndexCardsService;
        this.saveApiKeyService = saveApiKeyService;
        this.deleteApiKeyService = deleteApiKeyService;
    }

    @GetMapping("/status")
    public ResponseEntity<AiStatusResponse> getStatus(
            @RequestHeader("Authorization") String authorization
    ) throws EntityNotFoundException {
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        return ResponseEntity.ok(generateIndexCardsService.getStatus(username));
    }

    @PutMapping("/apiKey")
    public ResponseEntity<AiStatusResponse> saveApiKey(
            @RequestHeader("Authorization") String authorization,
            @RequestBody SaveApiKeyRequest saveApiKeyRequest
    ) throws EntityNotFoundException, AiGenerationException {
        // Never log the request body: it contains the API key.
        logger.info("Saving Gemini API key");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        saveApiKeyService.saveApiKey(username, saveApiKeyRequest);

        return ResponseEntity.ok(generateIndexCardsService.getStatus(username));
    }

    @DeleteMapping("/apiKey")
    public ResponseEntity<AiStatusResponse> deleteApiKey(
            @RequestHeader("Authorization") String authorization
    ) throws EntityNotFoundException {
        logger.info("Deleting Gemini API key");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        deleteApiKeyService.deleteApiKey(username);

        return ResponseEntity.ok(generateIndexCardsService.getStatus(username));
    }

    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneratedCardsResponse> generateIndexCards(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "") String notes,
            @RequestParam(defaultValue = "10") int cardCount,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws EntityNotFoundException, UnauthorizedException, EntityCreationException, AiGenerationException {
        // Never log the notes or file content.
        logger.info("Generating index card suggestions for project {}", projectId);
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        return ResponseEntity.status(HttpStatus.OK).body(
                generateIndexCardsService.generateIndexCards(username, projectId, notes, cardCount, file));
    }
}
