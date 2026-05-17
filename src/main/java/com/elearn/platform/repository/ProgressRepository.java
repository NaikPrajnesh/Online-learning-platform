package com.elearn.platform.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProgressRepository {

    private final JdbcTemplate jdbc;
    private final Map<String, Set<Long>> memoryProgress = new ConcurrentHashMap<>();

    public ProgressRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private String key(Long userId, Long courseId) {
        return userId + ":" + courseId;
    }

    public Set<Long> getCompletedLessons(Long userId, Long courseId) {
        String k = key(userId, courseId);
        Set<Long> mem = memoryProgress.get(k);
        if (mem != null) return new HashSet<>(mem);
        List<Long> db = jdbc.query(
                "SELECT lesson_id FROM lesson_progress WHERE user_id = ? AND course_id = ? AND completed = TRUE",
                (rs, rowNum) -> rs.getLong("lesson_id"), userId, courseId);
        Set<Long> set = new HashSet<>(db);
        memoryProgress.put(k, ConcurrentHashMap.newKeySet());
        memoryProgress.get(k).addAll(set);
        return set;
    }

    public void setLessonComplete(Long userId, Long courseId, Long lessonId, boolean completed) {
        String k = key(userId, courseId);
        memoryProgress.computeIfAbsent(k, x -> ConcurrentHashMap.newKeySet());
        if (completed) {
            memoryProgress.get(k).add(lessonId);
            try {
                jdbc.update(
                        "INSERT INTO lesson_progress (user_id, course_id, lesson_id, completed) VALUES (?, ?, ?, TRUE)",
                        userId, courseId, lessonId);
            } catch (Exception ignored) {}
        } else {
            memoryProgress.get(k).remove(lessonId);
            jdbc.update(
                    "DELETE FROM lesson_progress WHERE user_id = ? AND course_id = ? AND lesson_id = ?",
                    userId, courseId, lessonId);
        }
    }

    public void syncProgress(Long userId, Long courseId, List<Long> completedLessonIds) {
        String k = key(userId, courseId);
        Set<Long> set = ConcurrentHashMap.newKeySet();
        set.addAll(completedLessonIds);
        memoryProgress.put(k, set);
        jdbc.update("DELETE FROM lesson_progress WHERE user_id = ? AND course_id = ?", userId, courseId);
        for (Long lessonId : completedLessonIds) {
            setLessonComplete(userId, courseId, lessonId, true);
        }
    }
}
