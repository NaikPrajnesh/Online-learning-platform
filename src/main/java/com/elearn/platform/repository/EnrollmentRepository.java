package com.elearn.platform.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EnrollmentRepository {

    private final JdbcTemplate jdbc;
    private final Map<Long, Set<Long>> memoryEnrollments = new ConcurrentHashMap<>();

    public EnrollmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void enroll(Long userId, Long courseId) {
        memoryEnrollments.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(courseId);
        try {
            jdbc.update("INSERT INTO enrollments (user_id, course_id) VALUES (?, ?)", userId, courseId);
        } catch (Exception ignored) {
            // duplicate enrollment
        }
    }

    public boolean isEnrolled(Long userId, Long courseId) {
        Set<Long> courses = memoryEnrollments.get(userId);
        if (courses != null && courses.contains(courseId)) return true;
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM enrollments WHERE user_id = ? AND course_id = ?",
                Integer.class, userId, courseId);
        if (count != null && count > 0) {
            memoryEnrollments.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(courseId);
            return true;
        }
        return false;
    }

    public List<Long> findCourseIdsByUser(Long userId) {
        Set<Long> mem = memoryEnrollments.getOrDefault(userId, Set.of());
        List<Long> dbIds = jdbc.query(
                "SELECT course_id FROM enrollments WHERE user_id = ?",
                (rs, rowNum) -> rs.getLong("course_id"), userId);
        Set<Long> merged = new LinkedHashSet<>(mem);
        merged.addAll(dbIds);
        return new ArrayList<>(merged);
    }
}
