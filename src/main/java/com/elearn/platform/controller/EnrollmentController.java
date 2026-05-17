package com.elearn.platform.controller;

import com.elearn.platform.dto.EnrollmentDto;
import com.elearn.platform.service.EnrollmentService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public Map<String, String> enroll(@RequestBody Map<String, Long> body, HttpSession session) {
        Long userId = requireUser(session);
        Long courseId = body.get("courseId");
        if (courseId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "courseId is required");
        }
        enrollmentService.enroll(userId, courseId);
        return Map.of("message", "Enrolled successfully");
    }

    @GetMapping("/my")
    public List<EnrollmentDto> myCourses(HttpSession session) {
        Long userId = requireUser(session);
        return enrollmentService.getMyCourses(userId);
    }

    @GetMapping("/check/{courseId}")
    public Map<String, Boolean> check(@PathVariable Long courseId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return Map.of("enrolled", false);
        return Map.of("enrolled", enrollmentService.isEnrolled(userId, courseId));
    }

    private Long requireUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to enroll");
        }
        return userId;
    }
}
