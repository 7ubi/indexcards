package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponses;
import com.x7ubi.indexcards.service.indexcard.CreateIndexCardService;
import com.x7ubi.indexcards.service.indexcard.DeleteIndexCardService;
import com.x7ubi.indexcards.service.indexcard.IndexCardAssessmentService;
import com.x7ubi.indexcards.service.indexcard.IndexCardQuizService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/indexCard")
public class IndexCardsRestController {

    private final Logger logger = LoggerFactory.getLogger(IndexCardsRestController.class);

    private final CreateIndexCardService createIndexCardService;

    private final IndexCardAssessmentService indexCardAssessmentService;

    private final IndexCardQuizService indexCardQuizService;

    private final DeleteIndexCardService deleteIndexCardService;

    public IndexCardsRestController(
            CreateIndexCardService createIndexCardService,
            IndexCardAssessmentService indexCardAssessmentService,
            IndexCardQuizService indexCardQuizService,
            DeleteIndexCardService deleteIndexCardService) {
        this.createIndexCardService = createIndexCardService;
        this.indexCardAssessmentService = indexCardAssessmentService;
        this.indexCardQuizService = indexCardQuizService;
        this.deleteIndexCardService = deleteIndexCardService;
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

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteIndexCard(
            @RequestBody DeleteIndexCardRequest deleteIndexCardRequest
    ) {
        logger.info("Deleting index cards");

        ResultResponse response = this.deleteIndexCardService.deleteIndexCard(deleteIndexCardRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok().body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/quizIndexCards")
    public ResponseEntity<?> getIndexCardsForQuiz(
            @RequestParam Long id
    ) {
        logger.info("Getting index cards for quiz");

        IndexCardResponses response = this.indexCardQuizService.getIndexCardResponsesForQuiz(id);

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
