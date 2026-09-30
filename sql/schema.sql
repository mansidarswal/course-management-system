CREATE DATABASE IF NOT EXISTS course_management;
USE course_management;

CREATE TABLE IF NOT EXISTS students (
    student_id  INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    department  VARCHAR(80),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS courses (
    course_id    INT AUTO_INCREMENT PRIMARY KEY,
    course_code  VARCHAR(20)  NOT NULL UNIQUE,
    title        VARCHAR(150) NOT NULL,
    credits      INT NOT NULL CHECK (credits > 0),
    instructor   VARCHAR(100),
    max_seats    INT NOT NULL DEFAULT 60 CHECK (max_seats > 0)
);

CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    enrolled_on   DATE NOT NULL DEFAULT (CURRENT_DATE),
    grade         VARCHAR(2),
    CONSTRAINT fk_enr_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_enr_course  FOREIGN KEY (course_id)  REFERENCES courses(course_id)  ON DELETE CASCADE,
    CONSTRAINT uq_student_course UNIQUE (student_id, course_id)
);

CREATE INDEX idx_student_name ON students(name);
CREATE INDEX idx_course_title ON courses(title);

-- Sample data
INSERT IGNORE INTO students (name, email, department) VALUES
 ('Mansi Rawat','mansi@example.com','CSE'),
 ('Ravi Kumar','ravi@example.com','ECE'),
 ('Anita Sharma','anita@example.com','CSE');
INSERT IGNORE INTO courses (course_code, title, credits, instructor, max_seats) VALUES
 ('CS101','Data Structures',4,'Dr. Mehta',60),
 ('CS102','Database Systems',3,'Dr. Rao',50),
 ('CS103','Java Programming',3,'Prof. Iyer',2);