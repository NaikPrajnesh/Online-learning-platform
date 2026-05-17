package com.elearn.platform.dto;

import java.util.List;

public class ProgressDto {
    private Long courseId;
    private List<Long> completedLessonIds;
    private int totalLessons;
    private int completedCount;
    private int percentComplete;

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public List<Long> getCompletedLessonIds() { return completedLessonIds; }
    public void setCompletedLessonIds(List<Long> completedLessonIds) { this.completedLessonIds = completedLessonIds; }
    public int getTotalLessons() { return totalLessons; }
    public void setTotalLessons(int totalLessons) { this.totalLessons = totalLessons; }
    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }
    public int getPercentComplete() { return percentComplete; }
    public void setPercentComplete(int percentComplete) { this.percentComplete = percentComplete; }
}
