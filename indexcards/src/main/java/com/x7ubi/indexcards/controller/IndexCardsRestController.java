package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import com.x7ubi.indexcards.service.indexcard.CreateIndexCardService;
import com.x7ubi.indexcards.service.indexcard.IndexCardAssessmentService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

@RestController
@RequestMapping("/api/indexCard")
public class IndexCardsRestController {

    private final Logger logger = LoggerFactory.getLogger(IndexCardsRestController.class);

    private final CreateIndexCardService createIndexCardService;

    private final IndexCardAssessmentService indexCardAssessmentService;

    public IndexCardsRestController(
        CreateIndexCardService createIndexCardService,
        IndexCardAssessmentService indexCardAssessmentService
    ) {
        this.createIndexCardService = createIndexCardService;
        this.indexCardAssessmentService = indexCardAssessmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createIndexCard(
        @RequestBody CreateIndexCardRequest createProjectRequest
    ) {
        logger.info("Creating Index Card");

        ResultResponse response = this.createIndexCardService.createIndexCard(createProjectRequest);

        if(response.isSuccess()) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/assess")
    public ResponseEntity<?> assessIndexCard(
        @RequestBody AssessmentRequest assessmentRequest
    ) {
        logger.info("Assessing Index Card");
        ResultResponse response = this.indexCardAssessmentService.assessIndexCard(assessmentRequest);

        if(response.isSuccess()) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }
}
