package com.elearn.platform.service;

import com.elearn.platform.dto.ProgressDto;
import com.elearn.platform.model.Course;
import com.elearn.platform.repository.CourseRepository;
import com.elearn.platform.repository.ProgressRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final CourseRepository courseRepository;

    public ProgressService(ProgressRepository progressRepository, CourseRepository courseRepository) {
        this.progressRepository = progressRepository;
        this.courseRepository = courseRepository;
    }

    public ProgressDto getProgress(Long userId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        Set<Long> completed = progressRepository.getCompletedLessons(userId, courseId);
        int total = course.getLessons().size();
        int done = completed.size();
        int percent = total == 0 ? 0 : (int) Math.round((done * 100.0) / total);

        ProgressDto dto = new ProgressDto();
        dto.setCourseId(courseId);
        dto.setCompletedLessonIds(new ArrayList<>(completed));
        dto.setTotalLessons(total);
        dto.setCompletedCount(done);
        dto.setPercentComplete(percent);
        return dto;
    }

    public ProgressDto updateLesson(Long userId, Long courseId, Long lessonId, boolean completed) {
        progressRepository.setLessonComplete(userId, courseId, lessonId, completed);
        return getProgress(userId, courseId);
    }

    public ProgressDto syncProgress(Long userId, Long courseId, List<Long> completedLessonIds) {
        progressRepository.syncProgress(userId, courseId, completedLessonIds);
        return getProgress(userId, courseId);
    }
}
