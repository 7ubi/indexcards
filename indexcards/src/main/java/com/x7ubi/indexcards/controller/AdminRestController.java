package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.response.admin.AnalyticsResponse;
import com.x7ubi.indexcards.service.admin.AdminAnalyticsService;
import com.x7ubi.indexcards.service.admin.GrantAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Only reachable with the ADMIN role, enforced for /api/admin/** in SecurityConfig.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminRestController {
    Logger logger = LoggerFactory.getLogger(AdminRestController.class);

    private final AdminAnalyticsService adminAnalyticsService;

    private final GrantAdminService grantAdminService;

    public AdminRestController(AdminAnalyticsService adminAnalyticsService, GrantAdminService grantAdminService) {
        this.adminAnalyticsService = adminAnalyticsService;
        this.grantAdminService = grantAdminService;
    }

    @GetMapping("/analytics")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        logger.info("Getting admin analytics");

        return ResponseEntity.ok(adminAnalyticsService.getAnalytics());
    }

    @PutMapping("/users/{username}/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> grantAdmin(@PathVariable String username) throws EntityNotFoundException {
        logger.info("Granting admin role");

        grantAdminService.grantAdmin(username);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
