package tech.cloverfield.kdgplanner.business.students;

import android.content.Context;

import java.util.Collection;

import tech.cloverfield.kdgplanner.business.domain.Student;
import tech.cloverfield.kdgplanner.persistence.students.IStudentRepo;
import tech.cloverfield.kdgplanner.persistence.students.StudentRepoSQLite;

public class StudentManager implements IStudentManager {
    private IStudentRepo repo;

    public StudentManager(Context context) {
        repo = new StudentRepoSQLite(context);
    }

    @Override
    public Student addStudent(Student student) {
        return null;
    }

    @Override
    public Student findStudentByName(String lastOrFirstName) {
        return null;
    }

    @Override
    public Collection<Student> readAllStudents() {
        return null;
    }

    @Override
    public void removeStudent(Student student) {

    }
}
