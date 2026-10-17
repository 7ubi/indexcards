package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.IndexCardCsvImportRequest;
import com.x7ubi.indexcards.response.indexcard.DueIndexCardResponse;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.service.indexcard.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final DueIndexCardService dueIndexCardService;

    public IndexCardsRestController(
            IndexCardService indexCardService, CreateIndexCardService createIndexCardService,
            IndexCardAssessmentService indexCardAssessmentService, EditIndexCardService editIndexCardService,
            IndexCardQuizService indexCardQuizService,
            DeleteIndexCardService deleteIndexCardService, DueIndexCardService dueIndexCardService) {
        this.indexCardService = indexCardService;
        this.createIndexCardService = createIndexCardService;
        this.indexCardAssessmentService = indexCardAssessmentService;
        this.editIndexCardService = editIndexCardService;
        this.indexCardQuizService = indexCardQuizService;
        this.deleteIndexCardService = deleteIndexCardService;
        this.dueIndexCardService = dueIndexCardService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<IndexCardResponse> getIndexCard(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam Long id
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("getIndexCard id: {}", id);

        String username = securityUser.getUsername();

        IndexCardResponse indexCardResponse = this.indexCardService.getIndexCard(username, id);
        return ResponseEntity.status(HttpStatus.OK).body(indexCardResponse);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createIndexCard(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody CreateIndexCardRequest createProjectRequest
    ) throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        logger.info("Creating Index Card");

        String username = securityUser.getUsername();

        this.createIndexCardService.createIndexCard(username, createProjectRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> editIndexCard(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam Long id,
            @RequestBody CreateIndexCardRequest createProjectRequest
    ) throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        logger.info("Editing Index Card");

        String username = securityUser.getUsername();

        this.editIndexCardService.editIndexCard(username, id, createProjectRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteIndexCard(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody DeleteIndexCardRequest deleteIndexCardRequest)
            throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        logger.info("Deleting index cards");

        String username = securityUser.getUsername();

        this.deleteIndexCardService.deleteIndexCard(username, deleteIndexCardRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/quizIndexCards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<IndexCardResponse>> getIndexCardsForQuiz(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam Long id
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Getting index cards for quiz");

        String username = securityUser.getUsername();

        List<IndexCardResponse> response = this.indexCardQuizService.getIndexCardResponsesForQuiz(username, id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/due")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DueIndexCardResponse>> getDueIndexCards(
            @AuthenticationPrincipal SecurityUser securityUser
    ) throws EntityNotFoundException {
        logger.info("Getting due index cards across all projects");

        String username = securityUser.getUsername();

        List<DueIndexCardResponse> response = this.dueIndexCardService.getDueIndexCards(username);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/assess")
    public ResponseEntity<?> assessIndexCard(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody AssessmentRequest assessmentRequest
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Assessing Index Card");

        String username = securityUser.getUsername();

        this.indexCardAssessmentService.assessIndexCard(username, assessmentRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/import")
    public ResponseEntity<?> importIndexCards(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody IndexCardCsvImportRequest indexCardCsvImportRequest
    ) throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        logger.info("Importing Index Cards from CSV");

        String username = securityUser.getUsername();

        this.createIndexCardService.importIndexCardsFromCsv(username, indexCardCsvImportRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
