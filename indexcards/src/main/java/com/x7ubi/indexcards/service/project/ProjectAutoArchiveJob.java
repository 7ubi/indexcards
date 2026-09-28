package com.x7ubi.indexcards.service.project;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ProjectAutoArchiveJob {

    private final ArchiveProjectService archiveProjectService;

    public ProjectAutoArchiveJob(ArchiveProjectService archiveProjectService) {
        this.archiveProjectService = archiveProjectService;
    }

    // Also runs on startup so exam dates that passed while the server was down are caught up.
    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 5 0 * * *")
    public void autoArchivePastExamProjects() {
        archiveProjectService.autoArchivePastExamProjects(LocalDate.now());
    }
}
