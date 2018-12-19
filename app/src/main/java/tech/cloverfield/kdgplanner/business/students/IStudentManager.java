package tech.cloverfield.kdgplanner.business.students;

import java.util.Collection;

import tech.cloverfield.kdgplanner.business.domain.Student;

public interface IStudentManager {
    Student addStudent(Student student);
    Student findStudentByName(String lastOrFirstName);
    Collection<Student> readAllStudents();
    void removeStudent(Student student);
}
