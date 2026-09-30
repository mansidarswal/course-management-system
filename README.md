# Student Course Management System

A Java console application for managing students, courses and enrollments, using JDBC with MySQL.

## Features
- Add, view, update, delete and search students
- Add, view, update, delete and search courses
- Enroll and drop students, with seat-limit and duplicate checks done in a transaction
- Assign grades and view a student's courses or a course roster (SQL JOIN queries)

## Tech Stack
Java 17+, JDBC, MySQL 8, Maven, DAO pattern

## Project Structure
```
src/main/java/com/cms
├── Main.java          console menu
├── dao/               StudentDAO, CourseDAO, EnrollmentDAO (all SQL lives here)
├── model/             Student, Course, EnrollmentRow (records)
└── util/              DBConnection
sql/schema.sql         tables, constraints and sample data
```

## Setup
1. Install JDK 17+, Maven and MySQL 8.
2. Run `sql/schema.sql` in MySQL to create the `course_management` database.
3. Set your database credentials as environment variables:
```
   CMS_DB_USER=root
   CMS_DB_PASSWORD=your_password
```
In IntelliJ: Run → Edit Configurations → Environment variables.
If `CMS_DB_USER` is not set, it defaults to `root`.
4. Run `com.cms.Main`.

## Database Design
- `students` (student_id, name, email, department)
- `courses` (course_id, course_code, title, credits, instructor, max_seats)
- `enrollments` (enrollment_id, student_id, course_id, enrolled_on, grade)
  with foreign keys and `ON DELETE CASCADE`