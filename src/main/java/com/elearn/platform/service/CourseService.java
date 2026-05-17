package com.elearn.platform.service;

import com.elearn.platform.model.Course;
import com.elearn.platform.repository.CourseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAll(String search, String category) {
        return courseRepository.findAll().stream()
                .filter(c -> search == null || search.isBlank()
                        || c.getTitle().toLowerCase().contains(search.toLowerCase())
                        || c.getShortDescription().toLowerCase().contains(search.toLowerCase()))
                .filter(c -> category == null || category.isBlank() || "all".equalsIgnoreCase(category)
                        || c.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Course> getFeatured() {
        return courseRepository.findFeatured();
    }

    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));
    }

    public Course create(Course course) {
        return courseRepository.save(course);
    }

    public Course update(Long id, Course updates) {
        Course existing = getById(id);
        if (updates.getTitle() != null) existing.setTitle(updates.getTitle());
        if (updates.getShortDescription() != null) existing.setShortDescription(updates.getShortDescription());
        if (updates.getFullDescription() != null) existing.setFullDescription(updates.getFullDescription());
        if (updates.getDuration() != null) existing.setDuration(updates.getDuration());
        if (updates.getImageUrl() != null) existing.setImageUrl(updates.getImageUrl());
        if (updates.getInstructor() != null) existing.setInstructor(updates.getInstructor());
        existing.setFeatured(updates.isFeatured());
        if (updates.getCategory() != null) existing.setCategory(updates.getCategory());
        return courseRepository.save(existing);
    }

    public void delete(Long id) {
        getById(id);
        courseRepository.delete(id);
    }
}
