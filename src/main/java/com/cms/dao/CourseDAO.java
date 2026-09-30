package com.cms.dao;

import com.cms.model.Course;
import com.cms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAO {

    public int add(Course co) throws SQLException {
        String sql = "INSERT INTO courses (course_code, title, credits, instructor, max_seats) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, co.code());
            ps.setString(2, co.title());
            ps.setInt(3, co.credits());
            ps.setString(4, co.instructor());
            ps.setInt(5, co.maxSeats());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    public Optional<Course> findById(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM courses WHERE course_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public List<Course> findAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM courses ORDER BY course_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public List<Course> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_code LIKE ? OR title LIKE ? OR instructor LIKE ? ORDER BY title";
        List<Course> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            for (int i = 1; i <= 3; i++) ps.setString(i, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public boolean update(Course co) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, title = ?, credits = ?, instructor = ?, max_seats = ? WHERE course_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, co.code());
            ps.setString(2, co.title());
            ps.setInt(3, co.credits());
            ps.setString(4, co.instructor());
            ps.setInt(5, co.maxSeats());
            ps.setInt(6, co.id());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM courses WHERE course_id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int countEnrolled(int courseId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM enrollments WHERE course_id = ?")) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private Course map(ResultSet rs) throws SQLException {
        return new Course(rs.getInt("course_id"), rs.getString("course_code"), rs.getString("title"),
                rs.getInt("credits"), rs.getString("instructor"), rs.getInt("max_seats"));
    }
}
