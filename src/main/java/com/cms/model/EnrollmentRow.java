package com.cms.model;
import java.time.LocalDate;
public record EnrollmentRow(int enrollmentId, int studentId, String studentName,
                            int courseId, String courseCode, String courseTitle,
                            LocalDate enrolledOn, String grade) {

    @Override
    public String toString() {
        return String.format("#%-4d %-20s %-8s %-24s %s  grade:%s",enrollmentId, studentName, courseCode, courseTitle, enrolledOn,
                grade == null ? "-" : grade);
    }
}
