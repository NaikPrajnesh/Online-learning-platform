INSERT INTO users (email, password, full_name, role) VALUES
('admin@elearn.com', 'admin123', 'Platform Admin', 'ADMIN'),
('student@elearn.com', 'student123', 'Alex Student', 'STUDENT');

INSERT INTO courses (title, short_description, full_description, duration, image_url, instructor, featured, category) VALUES
('Java Fundamentals', 'Master core Java syntax, OOP, and collections.', 'A comprehensive introduction to Java programming covering variables, control flow, object-oriented principles, exception handling, and the Java Collections Framework. Perfect for beginners moving into enterprise development.', '12 hours', 'https://images.unsplash.com/photo-1516116216624-53e697fedbea?w=800&q=80', 'Dr. Sarah Chen', TRUE, 'Programming'),
('Spring Boot in Practice', 'Build REST APIs and web apps with Spring Boot.', 'Learn to create production-ready Spring Boot applications: dependency injection, REST controllers, JPA integration, security basics, and deployment strategies with hands-on projects.', '18 hours', 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=800&q=80', 'James Rivera', TRUE, 'Programming'),
('Responsive Web Design', 'Create beautiful layouts with Flexbox, Grid, and media queries.', 'From mobile-first design to advanced CSS Grid layouts, this course teaches modern responsive techniques, accessibility, and performance optimization for real-world websites.', '10 hours', 'https://images.unsplash.com/photo-1504639725590-34d0984388bd?w=800&q=80', 'Mia Thompson', TRUE, 'Design'),
('Data Structures & Algorithms', 'Strengthen problem-solving with essential DSA topics.', 'Explore arrays, linked lists, trees, graphs, sorting, searching, and complexity analysis. Includes interview-style challenges and visual explanations.', '20 hours', 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&q=80', 'Prof. David Okonkwo', FALSE, 'Computer Science'),
('UI/UX for Developers', 'Design interfaces users love with practical UX methods.', 'Wireframing, prototyping, color theory, typography, and usability testing tailored for developers who want to ship polished products.', '8 hours', 'https://images.unsplash.com/photo-1561070791-2526d30994b5?w=800&q=80', 'Elena Vasquez', FALSE, 'Design'),
('MySQL & JDBC', 'Connect Java applications to relational databases.', 'Learn SQL fundamentals, schema design, JDBC connections, prepared statements, transactions, and integrating MySQL with Spring applications.', '14 hours', 'https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=800&q=80', 'Raj Patel', FALSE, 'Database');

INSERT INTO lessons (course_id, title, duration_minutes, video_url, sort_order) VALUES
(1, 'Introduction to Java', 25, 'https://www.youtube.com/embed/eIrMbAQSU34', 1),
(1, 'Variables and Data Types', 30, 'https://www.youtube.com/embed/grEKMHGYyns', 2),
(1, 'Control Flow', 35, 'https://www.youtube.com/embed/ldYLYRNaucM', 3),
(1, 'Object-Oriented Programming', 45, 'https://www.youtube.com/embed/7psH8i6bA4Y', 4),
(2, 'Spring Boot Setup', 20, 'https://www.youtube.com/embed/9SGDpanrc8U', 1),
(2, 'REST Controllers', 40, 'https://www.youtube.com/embed/35EQXmHKZYs', 2),
(2, 'Dependency Injection', 35, 'https://www.youtube.com/embed/8ZiJReil90E', 3),
(2, 'Data Access with JPA', 50, 'https://www.youtube.com/embed/8ug4A6cPfRg', 4),
(3, 'CSS Flexbox Basics', 30, 'https://www.youtube.com/embed/JJSoEo8JSnc', 1),
(3, 'CSS Grid Layout', 35, 'https://www.youtube.com/embed/EiNiSFIPIQE', 2),
(3, 'Media Queries', 25, 'https://www.youtube.com/embed/2KL-z9AiqPk', 3),
(4, 'Arrays and Linked Lists', 40, NULL, 1),
(4, 'Stacks and Queues', 35, NULL, 2),
(4, 'Trees and Graphs', 50, NULL, 3),
(5, 'Design Thinking', 25, NULL, 1),
(5, 'Wireframing', 30, NULL, 2),
(6, 'SQL Essentials', 35, NULL, 1),
(6, 'JDBC Connections', 40, NULL, 2);
