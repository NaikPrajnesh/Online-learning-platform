package com.elearn.platform.repository;

import com.elearn.platform.model.Course;
import com.elearn.platform.model.Lesson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CourseRepository {

    private final JdbcTemplate jdbc;
    private final boolean inMemory;
    private final Map<Long, Course> memoryStore = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(100);

    public CourseRepository(JdbcTemplate jdbc, @Value("${app.storage.in-memory:true}") boolean inMemory) {
        this.jdbc = jdbc;
        this.inMemory = inMemory;
    }

    public void loadFromDatabase() {
        if (inMemory && !memoryStore.isEmpty()) return;
        List<Course> courses = jdbc.query(
                "SELECT id, title, short_description, full_description, duration, image_url, instructor, featured, category FROM courses",
                (rs, rowNum) -> {
                    Course c = new Course();
                    c.setId(rs.getLong("id"));
                    c.setTitle(rs.getString("title"));
                    c.setShortDescription(rs.getString("short_description"));
                    c.setFullDescription(rs.getString("full_description"));
                    c.setDuration(rs.getString("duration"));
                    c.setImageUrl(rs.getString("image_url"));
                    c.setInstructor(rs.getString("instructor"));
                    c.setFeatured(rs.getBoolean("featured"));
                    c.setCategory(rs.getString("category"));
                    return c;
                });
        for (Course course : courses) {
            course.setLessons(findLessonsByCourseId(course.getId()));
            if (inMemory) memoryStore.put(course.getId(), copyCourse(course));
        }
    }

    public List<Course> findAll() {
        ensureLoaded();
        if (inMemory) return memoryStore.values().stream().map(this::copyCourse).collect(Collectors.toList());
        return jdbc.query(
                "SELECT id, title, short_description, full_description, duration, image_url, instructor, featured, category FROM courses ORDER BY id",
                (rs, rowNum) -> mapCourse(rs));
    }

    public List<Course> findFeatured() {
        return findAll().stream().filter(Course::isFeatured).collect(Collectors.toList());
    }

    public Optional<Course> findById(Long id) {
        ensureLoaded();
        if (inMemory) {
            Course c = memoryStore.get(id);
            return c == null ? Optional.empty() : Optional.of(copyCourse(c));
        }
        List<Course> list = jdbc.query(
                "SELECT id, title, short_description, full_description, duration, image_url, instructor, featured, category FROM courses WHERE id = ?",
                (rs, rowNum) -> mapCourse(rs), id);
        if (list.isEmpty()) return Optional.empty();
        Course c = list.get(0);
        c.setLessons(findLessonsByCourseId(id));
        return Optional.of(c);
    }

    public Course save(Course course) {
        ensureLoaded();
        if (course.getId() == null) {
            long newId = inMemory ? idGen.incrementAndGet() : insertCourse(course);
            course.setId(newId);
        } else if (!inMemory) {
            jdbc.update(
                    "UPDATE courses SET title=?, short_description=?, full_description=?, duration=?, image_url=?, instructor=?, featured=?, category=? WHERE id=?",
                    course.getTitle(), course.getShortDescription(), course.getFullDescription(),
                    course.getDuration(), course.getImageUrl(), course.getInstructor(),
                    course.isFeatured(), course.getCategory(), course.getId());
        }
        if (inMemory) memoryStore.put(course.getId(), copyCourse(course));
        return course;
    }

    public void delete(Long id) {
        if (inMemory) {
            memoryStore.remove(id);
        } else {
            jdbc.update("DELETE FROM courses WHERE id = ?", id);
        }
    }

    public List<Lesson> findLessonsByCourseId(Long courseId) {
        if (inMemory) {
            Course c = memoryStore.get(courseId);
            return c == null ? List.of() : new ArrayList<>(c.getLessons());
        }
        return jdbc.query(
                "SELECT id, course_id, title, duration_minutes, video_url, sort_order FROM lessons WHERE course_id = ? ORDER BY sort_order",
                (rs, rowNum) -> {
                    Lesson l = new Lesson();
                    l.setId(rs.getLong("id"));
                    l.setCourseId(rs.getLong("course_id"));
                    l.setTitle(rs.getString("title"));
                    l.setDurationMinutes(rs.getInt("duration_minutes"));
                    l.setVideoUrl(rs.getString("video_url"));
                    l.setSortOrder(rs.getInt("sort_order"));
                    return l;
                }, courseId);
    }

    private void ensureLoaded() {
        if (inMemory && memoryStore.isEmpty()) loadFromDatabase();
    }

    private long insertCourse(Course course) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO courses (title, short_description, full_description, duration, image_url, instructor, featured, category) VALUES (?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getShortDescription());
            ps.setString(3, course.getFullDescription());
            ps.setString(4, course.getDuration());
            ps.setString(5, course.getImageUrl());
            ps.setString(6, course.getInstructor());
            ps.setBoolean(7, course.isFeatured());
            ps.setString(8, course.getCategory());
            return ps;
        }, keyHolder);
        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    private Course mapCourse(java.sql.ResultSet rs) throws java.sql.SQLException {
        Course c = new Course();
        c.setId(rs.getLong("id"));
        c.setTitle(rs.getString("title"));
        c.setShortDescription(rs.getString("short_description"));
        c.setFullDescription(rs.getString("full_description"));
        c.setDuration(rs.getString("duration"));
        c.setImageUrl(rs.getString("image_url"));
        c.setInstructor(rs.getString("instructor"));
        c.setFeatured(rs.getBoolean("featured"));
        c.setCategory(rs.getString("category"));
        c.setLessons(findLessonsByCourseId(c.getId()));
        return c;
    }

    private Course copyCourse(Course source) {
        Course c = new Course();
        c.setId(source.getId());
        c.setTitle(source.getTitle());
        c.setShortDescription(source.getShortDescription());
        c.setFullDescription(source.getFullDescription());
        c.setDuration(source.getDuration());
        c.setImageUrl(source.getImageUrl());
        c.setInstructor(source.getInstructor());
        c.setFeatured(source.isFeatured());
        c.setCategory(source.getCategory());
        c.setLessons(new ArrayList<>(source.getLessons()));
        return c;
    }
}
