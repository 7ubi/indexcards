package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.response.admin.AnalyticsResponse;
import com.x7ubi.indexcards.response.admin.DailyActivityResponse;
import com.x7ubi.indexcards.response.admin.DailyCountResponse;
import com.x7ubi.indexcards.response.admin.UserAnalyticsResponse;
import com.x7ubi.indexcards.service.admin.AdminAnalyticsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AdminAnalyticsServiceTest extends AdminTestConfig {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @Test
    public void emptyAnalyticsTest() {
        // when
        AnalyticsResponse analytics = this.adminAnalyticsService.getAnalytics();

        // then
        Assertions.assertEquals(2, analytics.getTotals().getUsers());
        Assertions.assertEquals(0, analytics.getTotals().getActiveProjects());
        Assertions.assertEquals(0, analytics.getTotals().getIndexCards());
        Assertions.assertEquals(0, analytics.getTotals().getAssessments());
        Assertions.assertEquals(AdminAnalyticsService.DAYS, analytics.getDailyActivity().size());
        Assertions.assertEquals(LocalDate.now(), analytics.getDailyActivity().getLast().getDate());
        Assertions.assertTrue(analytics.getDailyActivity().stream().allMatch(day -> day.getAssessments() == 0));
        Assertions.assertEquals(2, analytics.getUsers().size());
    }

    @Test
    public void analyticsTest() {
        // given
        Project project = saveProject("Project", this.user, false);
        saveProject("Archived", this.user, true);
        Project adminProject = saveProject("AdminProject", this.admin, false);
        IndexCard card = saveIndexCard(project);
        IndexCard card2 = saveIndexCard(project);
        IndexCard adminCard = saveIndexCard(adminProject);
        saveIndexCard(adminProject);

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime yesterday = today.minusDays(1);
        assess(card, Assessment.BAD, yesterday);
        assess(card, Assessment.GOOD, today);
        assess(card2, Assessment.OK, today);
        assess(adminCard, Assessment.OK, today);
        // outside the 30 day window
        assess(adminCard, Assessment.GOOD, today.minusDays(AdminAnalyticsService.DAYS + 5));

        saveUser("newbie", Role.USER, yesterday);

        // when
        AnalyticsResponse analytics = this.adminAnalyticsService.getAnalytics();

        // then
        Assertions.assertEquals(3, analytics.getTotals().getUsers());
        Assertions.assertEquals(2, analytics.getTotals().getActiveProjects());
        Assertions.assertEquals(1, analytics.getTotals().getArchivedProjects());
        Assertions.assertEquals(4, analytics.getTotals().getIndexCards());
        Assertions.assertEquals(5, analytics.getTotals().getAssessments());

        Assertions.assertEquals(1, analytics.getAssessmentDistribution().getUnrated());
        Assertions.assertEquals(0, analytics.getAssessmentDistribution().getBad());
        Assertions.assertEquals(1, analytics.getAssessmentDistribution().getOk());
        Assertions.assertEquals(2, analytics.getAssessmentDistribution().getGood());

        List<DailyActivityResponse> activity = analytics.getDailyActivity();
        Assertions.assertEquals(AdminAnalyticsService.DAYS, activity.size());
        DailyActivityResponse todayActivity = activity.getLast();
        Assertions.assertEquals(3, todayActivity.getAssessments());
        Assertions.assertEquals(2, todayActivity.getActiveUsers());
        DailyActivityResponse yesterdayActivity = activity.get(activity.size() - 2);
        Assertions.assertEquals(1, yesterdayActivity.getAssessments());
        Assertions.assertEquals(1, yesterdayActivity.getActiveUsers());
        Assertions.assertEquals(4, activity.stream().mapToLong(DailyActivityResponse::getAssessments).sum());

        // "test" has no signup date and is not counted
        List<DailyCountResponse> signups = analytics.getDailySignups();
        Assertions.assertEquals(AdminAnalyticsService.DAYS, signups.size());
        Assertions.assertEquals(1, signups.getLast().getCount());
        Assertions.assertEquals(1, signups.get(signups.size() - 2).getCount());
        Assertions.assertEquals(2, signups.stream().mapToLong(DailyCountResponse::getCount).sum());

        List<UserAnalyticsResponse> users = analytics.getUsers();
        Assertions.assertEquals(3, users.size());
        UserAnalyticsResponse testUser = findUser(users, "test");
        Assertions.assertEquals(2, testUser.getProjects());
        Assertions.assertEquals(2, testUser.getIndexCards());
        Assertions.assertEquals(3, testUser.getAssessments());
        Assertions.assertNull(testUser.getCreatedAt());
        Assertions.assertFalse(testUser.isAdmin());
        UserAnalyticsResponse adminUser = findUser(users, "admin");
        Assertions.assertEquals(1, adminUser.getProjects());
        Assertions.assertEquals(2, adminUser.getIndexCards());
        Assertions.assertEquals(2, adminUser.getAssessments());
        Assertions.assertTrue(adminUser.isAdmin());
        UserAnalyticsResponse newbie = findUser(users, "newbie");
        Assertions.assertEquals(0, newbie.getProjects());
        Assertions.assertEquals(0, newbie.getIndexCards());
        Assertions.assertEquals(0, newbie.getAssessments());
        Assertions.assertNull(newbie.getLastActivity());
        // users without activity come last
        Assertions.assertEquals("newbie", users.getLast().getUsername());
    }

    private static UserAnalyticsResponse findUser(List<UserAnalyticsResponse> users, String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElseThrow();
    }
}
