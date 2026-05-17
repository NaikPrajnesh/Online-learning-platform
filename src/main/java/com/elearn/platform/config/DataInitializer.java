package com.elearn.platform.config;

import com.elearn.platform.repository.CourseRepository;
import com.elearn.platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public DataInitializer(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        userRepository.seedFromDatabase();
        courseRepository.loadFromDatabase();
    }
}
