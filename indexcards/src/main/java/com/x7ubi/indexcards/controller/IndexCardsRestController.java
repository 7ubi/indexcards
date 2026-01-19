package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.IndexCardCsvImportRequest;
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

    private final JwtUtils jwtUtils;

    private final IndexCardService indexCardService;

    private final CreateIndexCardService createIndexCardService;

    private final IndexCardAssessmentService indexCardAssessmentService;

    private final EditIndexCardService editIndexCardService;

    private final IndexCardQuizService indexCardQuizService;

    private final DeleteIndexCardService deleteIndexCardService;

    public IndexCardsRestController(
            JwtUtils jwtUtils, IndexCardService indexCardService, CreateIndexCardService createIndexCardService,
            IndexCardAssessmentService indexCardAssessmentService, EditIndexCardService editIndexCardService,
            IndexCardQuizService indexCardQuizService,
            DeleteIndexCardService deleteIndexCardService) {
        this.jwtUtils = jwtUtils;
        this.indexCardService = indexCardService;
        this.createIndexCardService = createIndexCardService;
        this.indexCardAssessmentService = indexCardAssessmentService;
        this.editIndexCardService = editIndexCardService;
        this.indexCardQuizService = indexCardQuizService;
        this.deleteIndexCardService = deleteIndexCardService;
    }

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<IndexCardResponse> getIndexCard(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long id
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("getIndexCard id: {}", id);

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        IndexCardResponse indexCardResponse = this.indexCardService.getIndexCard(username, id);
        return ResponseEntity.status(HttpStatus.OK).body(indexCardResponse);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createIndexCard(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreateIndexCardRequest createProjectRequest
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Creating Index Card");

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        this.createIndexCardService.createIndexCard(username, createProjectRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> editIndexCard(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long id,
            @RequestBody CreateIndexCardRequest createProjectRequest
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Editing Index Card");

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        this.editIndexCardService.editIndexCard(username, id, createProjectRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteIndexCard(
            @RequestHeader("Authorization") String authorization,
            @RequestBody DeleteIndexCardRequest deleteIndexCardRequest)
            throws EntityNotFoundException, UnauthorizedException {
        logger.info("Deleting index cards");

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        this.deleteIndexCardService.deleteIndexCard(username, deleteIndexCardRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/quizIndexCards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<IndexCardResponse>> getIndexCardsForQuiz(
            @RequestHeader("Authorization") String authorization,
            @RequestParam Long id
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Getting index cards for quiz");

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        List<IndexCardResponse> response = this.indexCardQuizService.getIndexCardResponsesForQuiz(username, id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/assess")
    public ResponseEntity<?> assessIndexCard(
            @RequestHeader("Authorization") String authorization,
            @RequestBody AssessmentRequest assessmentRequest
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Assessing Index Card");

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        this.indexCardAssessmentService.assessIndexCard(username, assessmentRequest);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/import")
    public ResponseEntity<?> importIndexCards(
            @RequestHeader("Authorization") String authorization,
            @RequestBody IndexCardCsvImportRequest indexCardCsvImportRequest
            ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Importing Index Cards from CSV");

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        this.createIndexCardService.importIndexCardsFromCsv(username, indexCardCsvImportRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
