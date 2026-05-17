package com.elearn.platform.service;

import com.elearn.platform.dto.EnrollmentDto;
import com.elearn.platform.model.Course;
import com.elearn.platform.repository.CourseRepository;
import com.elearn.platform.repository.EnrollmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final ProgressService progressService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             ProgressService progressService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.progressService = progressService;
    }

    public void enroll(Long userId, Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
        enrollmentRepository.enroll(userId, courseId);
    }

    public List<EnrollmentDto> getMyCourses(Long userId) {
        List<EnrollmentDto> result = new ArrayList<>();
        for (Long courseId : enrollmentRepository.findCourseIdsByUser(userId)) {
            courseRepository.findById(courseId).ifPresent(course -> {
                int percent = progressService.getProgress(userId, courseId).getPercentComplete();
                result.add(new EnrollmentDto(courseId, course, percent));
            });
        }
        return result;
    }

    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.isEnrolled(userId, courseId);
    }
}
