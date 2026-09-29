package com.x7ubi.indexcards.service.admin;

import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.response.admin.AnalyticsResponse;
import com.x7ubi.indexcards.response.admin.AnalyticsTotalsResponse;
import com.x7ubi.indexcards.response.admin.AssessmentDistributionResponse;
import com.x7ubi.indexcards.response.admin.DailyActivityResponse;
import com.x7ubi.indexcards.response.admin.DailyCountResponse;
import com.x7ubi.indexcards.response.admin.UserAnalyticsResponse;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * App-wide usage statistics for admins. All queries are scalar projections, so none of the EAGER entity graphs
 * (user -> projects -> cards -> assessment history) are loaded.
 */
@Service
public class AdminAnalyticsService {

    public static final int DAYS = 30;

    private final EntityManager entityManager;

    public AdminAnalyticsService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public AnalyticsResponse getAnalytics() {
        LocalDate firstDay = LocalDate.now().minusDays(DAYS - 1);

        return new AnalyticsResponse(
                getTotals(), getAssessmentDistribution(), getDailyActivity(firstDay), getDailySignups(firstDay),
                getUsers());
    }

    private AnalyticsTotalsResponse getTotals() {
        long archivedProjects = count("select count(p) from Project p where p.archived = true");

        return new AnalyticsTotalsResponse(
                count("select count(u) from User u"),
                count("select count(p) from Project p") - archivedProjects,
                archivedProjects,
                count("select count(c) from IndexCard c"),
                count("select count(a) from IndexCardAssessment a"));
    }

    private AssessmentDistributionResponse getAssessmentDistribution() {
        Map<Assessment, Long> counts = new EnumMap<>(Assessment.class);
        entityManager.createQuery(
                        "select c.assessment, count(c) from IndexCard c group by c.assessment", Object[].class)
                .getResultList()
                .forEach(row -> counts.put((Assessment) row[0], (Long) row[1]));

        return new AssessmentDistributionResponse(
                counts.getOrDefault(Assessment.UNRATED, 0L),
                counts.getOrDefault(Assessment.BAD, 0L),
                counts.getOrDefault(Assessment.OK, 0L),
                counts.getOrDefault(Assessment.GOOD, 0L));
    }

    private List<DailyActivityResponse> getDailyActivity(LocalDate firstDay) {
        Map<LocalDate, Object[]> rowsByDay = new HashMap<>();
        entityManager.createQuery(
                        "select cast(a.date as LocalDate), count(a), count(distinct p.user.userId) "
                                + "from IndexCard c join c.assessmentHistory a join c.project p "
                                + "where a.date >= :since group by cast(a.date as LocalDate)", Object[].class)
                .setParameter("since", firstDay.atStartOfDay())
                .getResultList()
                .forEach(row -> rowsByDay.put(toLocalDate(row[0]), row));

        List<DailyActivityResponse> days = new ArrayList<>();
        for (LocalDate day = firstDay; day.isBefore(firstDay.plusDays(DAYS)); day = day.plusDays(1)) {
            Object[] row = rowsByDay.get(day);
            days.add(row == null
                    ? new DailyActivityResponse(day, 0, 0)
                    : new DailyActivityResponse(day, (Long) row[1], (Long) row[2]));
        }
        return days;
    }

    private List<DailyCountResponse> getDailySignups(LocalDate firstDay) {
        Map<LocalDate, Long> countsByDay = new HashMap<>();
        entityManager.createQuery(
                        "select cast(u.createdAt as LocalDate), count(u) from User u "
                                + "where u.createdAt >= :since group by cast(u.createdAt as LocalDate)", Object[].class)
                .setParameter("since", firstDay.atStartOfDay())
                .getResultList()
                .forEach(row -> countsByDay.put(toLocalDate(row[0]), (Long) row[1]));

        List<DailyCountResponse> days = new ArrayList<>();
        for (LocalDate day = firstDay; day.isBefore(firstDay.plusDays(DAYS)); day = day.plusDays(1)) {
            days.add(new DailyCountResponse(day, countsByDay.getOrDefault(day, 0L)));
        }
        return days;
    }

    private List<UserAnalyticsResponse> getUsers() {
        List<UserAnalyticsResponse> users = new ArrayList<>(entityManager.createQuery(
                        "select u.username, u.createdAt, count(distinct p), count(distinct c), count(a), max(a.date), u.role "
                                + "from User u left join u.projects p left join p.indexCards c "
                                + "left join c.assessmentHistory a "
                                + "group by u.userId, u.username, u.createdAt, u.role", Object[].class)
                .getResultList()
                .stream()
                .map(row -> new UserAnalyticsResponse(
                        (String) row[0], (LocalDateTime) row[1], (Long) row[2], (Long) row[3], (Long) row[4],
                        (LocalDateTime) row[5], row[6] == Role.ADMIN))
                .toList());

        // Most recently active first; users without any assessment last.
        users.sort(Comparator.comparing(
                UserAnalyticsResponse::getLastActivity, Comparator.nullsLast(Comparator.reverseOrder())));
        return users;
    }

    private long count(String query) {
        return entityManager.createQuery(query, Long.class).getSingleResult();
    }

    private static LocalDate toLocalDate(Object value) {
        // Depending on the dialect, casting to a date can come back as java.sql.Date.
        return value instanceof java.sql.Date sqlDate ? sqlDate.toLocalDate() : (LocalDate) value;
    }
}
