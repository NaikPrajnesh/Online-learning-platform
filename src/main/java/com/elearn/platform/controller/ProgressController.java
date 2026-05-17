package com.elearn.platform.controller;

import com.elearn.platform.dto.ProgressDto;
import com.elearn.platform.service.ProgressService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @GetMapping("/{courseId}")
    public ProgressDto get(@PathVariable Long courseId, HttpSession session) {
        Long userId = requireUser(session);
        return progressService.getProgress(userId, courseId);
    }

    @PutMapping("/{courseId}/lessons/{lessonId}")
    public ProgressDto updateLesson(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody Map<String, Boolean> body,
            HttpSession session) {
        Long userId = requireUser(session);
        boolean completed = body.getOrDefault("completed", true);
        return progressService.updateLesson(userId, courseId, lessonId, completed);
    }

    @PutMapping("/{courseId}/sync")
    public ProgressDto sync(
            @PathVariable Long courseId,
            @RequestBody List<Long> completedLessonIds,
            HttpSession session) {
        Long userId = requireUser(session);
        return progressService.syncProgress(userId, courseId, completedLessonIds);
    }

    private Long requireUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in");
        }
        return userId;
    }
}
