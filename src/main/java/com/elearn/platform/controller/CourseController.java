package com.elearn.platform.controller;

import com.elearn.platform.model.Course;
import com.elearn.platform.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        return courseService.getAll(search, category);
    }

    @GetMapping("/featured")
    public List<Course> featured() {
        return courseService.getFeatured();
    }

    @GetMapping("/{id}")
    public Course get(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    public Course create(@RequestBody Course course) {
        return courseService.create(course);
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @RequestBody Course course) {
        return courseService.update(id, course);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        courseService.delete(id);
    }
}
