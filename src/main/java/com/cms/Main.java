package com.cms;

import com.cms.dao.CourseDAO;
import com.cms.dao.EnrollmentDAO;
import com.cms.dao.StudentDAO;
import com.cms.model.Course;
import com.cms.model.EnrollmentRow;
import com.cms.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final StudentDAO students = new StudentDAO();
    private static final CourseDAO courses = new CourseDAO();
    private static final EnrollmentDAO enrollments = new EnrollmentDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("""

                    ===== Student Course Management System =====
                     1. Add student            8. Add course
                     2. View all students      9. View all courses
                     3. Search students       10. Search courses
                     4. Update student        11. Update course
                     5. Delete student        12. Delete course
                     6. Enroll student        13. View enrollments (all)
                     7. Drop enrollment       14. Student's courses
                    15. Course roster         16. Assign grade
                     0. Exit
                    """);
            int choice = readInt("Choice: ");
            try {
                if (choice == 0) { System.out.println("Bye!"); return; }
                handle(choice);
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private static void handle(int choice) throws SQLException {
        switch (choice) {
            case 1 -> {
                int id = students.add(new Student(0, readLine("Name: "), readLine("Email: "), readLine("Department: ")));
                System.out.println("Student added with ID " + id);
            }
            case 2 -> print(students.findAll());
            case 3 -> print(students.search(readLine("Keyword: ")));
            case 4 -> {
                int id = readInt("Student ID: ");
                var existing = students.findById(id);
                if (existing.isEmpty()) { System.out.println("Not found."); break; }
                Student s = existing.get();
                String name = readOrKeep("Name", s.name());
                String email = readOrKeep("Email", s.email());
                String dept = readOrKeep("Department", s.department());
                System.out.println(students.update(new Student(id, name, email, dept)) ? "Updated." : "Nothing updated.");
            }
            case 5 -> System.out.println(students.delete(readInt("Student ID: ")) ? "Deleted." : "Not found.");
            case 6 -> {
                enrollments.enroll(readInt("Student ID: "), readInt("Course ID: "));
                System.out.println("Enrolled successfully.");
            }
            case 7 -> System.out.println(enrollments.drop(readInt("Student ID: "), readInt("Course ID: "))
                    ? "Enrollment dropped." : "No such enrollment.");
            case 8 -> {
                int id = courses.add(new Course(0, readLine("Course code: "), readLine("Title: "),
                        readInt("Credits: "), readLine("Instructor: "), readInt("Max seats: ")));
                System.out.println("Course added with ID " + id);
            }
            case 9 -> print(courses.findAll());
            case 10 -> print(courses.search(readLine("Keyword: ")));
            case 11 -> {
                int id = readInt("Course ID: ");
                var existing = courses.findById(id);
                if (existing.isEmpty()) { System.out.println("Not found."); break; }
                Course c = existing.get();
                String code = readOrKeep("Code", c.code());
                String title = readOrKeep("Title", c.title());
                int credits = Integer.parseInt(readOrKeep("Credits", String.valueOf(c.credits())));
                String instr = readOrKeep("Instructor", c.instructor());
                int seats = Integer.parseInt(readOrKeep("Max seats", String.valueOf(c.maxSeats())));
                System.out.println(courses.update(new Course(id, code, title, credits, instr, seats))
                        ? "Updated." : "Nothing updated.");
            }
            case 12 -> System.out.println(courses.delete(readInt("Course ID: ")) ? "Deleted." : "Not found.");
            case 13 -> printEnr(enrollments.findAll());
            case 14 -> printEnr(enrollments.findByStudent(readInt("Student ID: ")));
            case 15 -> {
                int id = readInt("Course ID: ");
                printEnr(enrollments.findByCourse(id));
                System.out.println("Enrolled: " + courses.countEnrolled(id));
            }
            case 16 -> System.out.println(enrollments.assignGrade(readInt("Student ID: "), readInt("Course ID: "),
                    readLine("Grade (e.g. A, B+): ")) ? "Grade saved." : "No such enrollment.");
            default -> System.out.println("Invalid choice.");
        }
    }

    private static <T> void print(List<T> rows) {
        if (rows.isEmpty()) System.out.println("(no records)");
        else rows.forEach(System.out::println);
    }

    private static void printEnr(List<EnrollmentRow> rows) { print(rows); }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    private static String readOrKeep(String label, String current) {
        String v = readLine(label + " [" + current + "]: ");
        return v.isEmpty() ? current : v;
    }

    private static int readInt(String prompt) {
        while (true) {
            try { return Integer.parseInt(readLine(prompt)); }
            catch (NumberFormatException e) { System.out.println("Please enter a number."); }
        }
    }
}
