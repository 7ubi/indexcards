package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.service.indexcard.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/indexCard")
public class IndexCardsRestController {

    private final Logger logger = LoggerFactory.getLogger(IndexCardsRestController.class);

    private final IndexCardService indexCardService;

    private final CreateIndexCardService createIndexCardService;

    private final IndexCardAssessmentService indexCardAssessmentService;

    private final EditIndexCardService editIndexCardService;

    private final IndexCardQuizService indexCardQuizService;

    private final DeleteIndexCardService deleteIndexCardService;

    public IndexCardsRestController(
            IndexCardService indexCardService, CreateIndexCardService createIndexCardService,
            IndexCardAssessmentService indexCardAssessmentService, EditIndexCardService editIndexCardService,
            IndexCardQuizService indexCardQuizService,
            DeleteIndexCardService deleteIndexCardService) {
        this.indexCardService = indexCardService;
        this.createIndexCardService = createIndexCardService;
        this.indexCardAssessmentService = indexCardAssessmentService;
        this.editIndexCardService = editIndexCardService;
        this.indexCardQuizService = indexCardQuizService;
        this.deleteIndexCardService = deleteIndexCardService;
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<IndexCardResponse> getIndexCard(@RequestParam Long id) throws EntityNotFoundException {
        logger.info("getIndexCard id: {}", id);

        IndexCardResponse indexCardResponse = this.indexCardService.getIndexCard(id);
        return ResponseEntity.status(HttpStatus.OK).body(indexCardResponse);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createIndexCard(
            @RequestBody CreateIndexCardRequest createProjectRequest
    ) throws EntityNotFoundException {
        logger.info("Creating Index Card");

        this.createIndexCardService.createIndexCard(createProjectRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> editIndexCard(@RequestParam Long id, @RequestBody CreateIndexCardRequest createProjectRequest) throws EntityNotFoundException {
        logger.info("Editing Index Card");

        this.editIndexCardService.editIndexCard(id, createProjectRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteIndexCard(@RequestBody DeleteIndexCardRequest deleteIndexCardRequest) throws EntityNotFoundException {
        logger.info("Deleting index cards");

        this.deleteIndexCardService.deleteIndexCard(deleteIndexCardRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/quizIndexCards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<IndexCardResponse>> getIndexCardsForQuiz(@RequestParam Long id) throws EntityNotFoundException {
        logger.info("Getting index cards for quiz");

        List<IndexCardResponse> response = this.indexCardQuizService.getIndexCardResponsesForQuiz(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/assess")
    public ResponseEntity<?> assessIndexCard(
            @RequestBody AssessmentRequest assessmentRequest
    ) throws EntityNotFoundException {
        logger.info("Assessing Index Card");
        this.indexCardAssessmentService.assessIndexCard(assessmentRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
