package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.service.indexcard.CreateIndexCardService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/indexCard")
public class IndexCardsRestController {

    private final Logger logger = LoggerFactory.getLogger(IndexCardsRestController.class);

    private final CreateIndexCardService createIndexCardService;

    public IndexCardsRestController(CreateIndexCardService createIndexCardService) {
        this.createIndexCardService = createIndexCardService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createIndexCard(
        @RequestBody CreateIndexCardRequest createProjectRequest
    ) {
        logger.info("Creating Index Card");
        return ResponseEntity
                .ok()
                .body(this.createIndexCardService.createIndexCard(createProjectRequest));
    }
}
