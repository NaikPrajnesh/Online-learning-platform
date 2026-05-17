package com.elearn.platform.repository;

import com.elearn.platform.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;
    private final ConcurrentHashMap<String, User> memoryUsers = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1000);
    private boolean seeded;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void seedFromDatabase() {
        if (seeded) return;
        List<User> users = jdbc.query(
                "SELECT id, email, password, full_name, role FROM users",
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")));
        users.forEach(u -> memoryUsers.put(u.getEmail().toLowerCase(), u));
        seeded = true;
    }

    public Optional<User> findByEmail(String email) {
        seedFromDatabase();
        User u = memoryUsers.get(email.toLowerCase());
        if (u != null) return Optional.of(u);
        List<User> list = jdbc.query(
                "SELECT id, email, password, full_name, role FROM users WHERE LOWER(email) = LOWER(?)",
                (rs, rowNum) -> new User(
                        rs.getLong("id"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")),
                email);
        list.forEach(user -> memoryUsers.put(user.getEmail().toLowerCase(), user));
        return list.stream().findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(con -> {
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (email, password, full_name, role) VALUES (?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getFullName());
                ps.setString(4, user.getRole() != null ? user.getRole() : "STUDENT");
                return ps;
            }, keyHolder);
            user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        }
        memoryUsers.put(user.getEmail().toLowerCase(), user);
        return user;
    }
}
