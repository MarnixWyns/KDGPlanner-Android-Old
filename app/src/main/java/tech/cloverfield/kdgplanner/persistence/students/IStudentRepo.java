package tech.cloverfield.kdgplanner.persistence.students;

import java.util.List;

import tech.cloverfield.kdgplanner.business.domain.Student;

public interface IStudentRepo {
    Student createStudent(Student student);
    Student readStudent(int studentId);
    List<Student> readAllStudent();
    Student updateStudent(Student student);
    void deleteStudent(Student student);
    void deleteStudent(int studentId);
}

