package com.cms.dao;

import com.cms.model.Student;
import com.cms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    public int add(Student s) throws SQLException {
        String sql = "INSERT INTO students (name, email, department) VALUES (?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.name());
            ps.setString(2, s.email());
            ps.setString(3, s.department());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public Optional<Student> findById(int id) throws SQLException {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        return query("SELECT * FROM students ORDER BY student_id", null);
    }

    /** Partial, case-insensitive search on name, email or department. */
    public List<Student> search(String keyword) throws SQLException {
        return query("SELECT * FROM students WHERE name LIKE ? OR email LIKE ? OR department LIKE ? ORDER BY name",
                "%" + keyword + "%");
    }

    public boolean update(Student s) throws SQLException {
        String sql = "UPDATE students SET name = ?, email = ?, department = ? WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, s.name());
            ps.setString(2, s.email());
            ps.setString(3, s.department());
            ps.setInt(4, s.id());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private List<Student> query(String sql, String like) throws SQLException {
        List<Student> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (like != null) {
                for (int i = 1; i <= 3; i++) ps.setString(i, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private Student map(ResultSet rs) throws SQLException {
        return new Student(rs.getInt("student_id"), rs.getString("name"),
                rs.getString("email"), rs.getString("department"));
    }
}