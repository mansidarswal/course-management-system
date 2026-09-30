package com.cms.dao;

import com.cms.model.EnrollmentRow;
import com.cms.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    private static final String BASE_SELECT = """
            SELECT e.enrollment_id, s.student_id, s.name, c.course_id, c.course_code, c.title,
                   e.enrolled_on, e.grade
            FROM enrollments e
            JOIN students s ON s.student_id = e.student_id
            JOIN courses  c ON c.course_id  = e.course_id
            """;

    /**
     * Enrolls a student in a course inside one transaction.
     * The course row is locked so two concurrent enrollments cannot exceed max_seats.
     */
    public void enroll(int studentId, int courseId) throws SQLException {
        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            try {
                int maxSeats;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT max_seats FROM courses WHERE course_id = ? FOR UPDATE")) {
                    ps.setInt(1, courseId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Course not found: " + courseId);
                        maxSeats = rs.getInt(1);
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM students WHERE student_id = ?")) {
                    ps.setInt(1, studentId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new SQLException("Student not found: " + studentId);
                    }
                }
                int taken;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT COUNT(*) FROM enrollments WHERE course_id = ?")) {
                    ps.setInt(1, courseId);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        taken = rs.getInt(1);
                    }
                }
                if (taken >= maxSeats) throw new SQLException("Course is full (" + maxSeats + " seats).");

                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)")) {
                    ps.setInt(1, studentId);
                    ps.setInt(2, courseId);
                    ps.executeUpdate();
                } catch (SQLIntegrityConstraintViolationException dup) {
                    throw new SQLException("Student is already enrolled in this course.");
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public boolean drop(int studentId, int courseId) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?")) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean assignGrade(int studentId, int courseId, String grade) throws SQLException {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE enrollments SET grade = ? WHERE student_id = ? AND course_id = ?")) {
            ps.setString(1, grade);
            ps.setInt(2, studentId);
            ps.setInt(3, courseId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<EnrollmentRow> findByStudent(int studentId) throws SQLException {
        return run(BASE_SELECT + " WHERE s.student_id = ? ORDER BY c.course_code", studentId);
    }

    public List<EnrollmentRow> findByCourse(int courseId) throws SQLException {
        return run(BASE_SELECT + " WHERE c.course_id = ? ORDER BY s.name", courseId);
    }

    public List<EnrollmentRow> findAll() throws SQLException {
        return run(BASE_SELECT + " ORDER BY e.enrollment_id", null);
    }

    private List<EnrollmentRow> run(String sql, Integer param) throws SQLException {
        List<EnrollmentRow> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param != null) ps.setInt(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new EnrollmentRow(rs.getInt(1), rs.getInt(2), rs.getString(3),
                            rs.getInt(4), rs.getString(5), rs.getString(6),
                            rs.getDate(7).toLocalDate(), rs.getString(8)));
                }
            }
        }
        return list;
    }
}