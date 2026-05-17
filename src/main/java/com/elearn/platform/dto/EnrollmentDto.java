package com.elearn.platform.dto;

import com.elearn.platform.model.Course;

public class EnrollmentDto {
    private Long courseId;
    private Course course;
    private int percentComplete;

    public EnrollmentDto() {}

    public EnrollmentDto(Long courseId, Course course, int percentComplete) {
        this.courseId = courseId;
        this.course = course;
        this.percentComplete = percentComplete;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public int getPercentComplete() { return percentComplete; }
    public void setPercentComplete(int percentComplete) { this.percentComplete = percentComplete; }
}
