package tech.cloverfield.kdgplanner.persistence.students;

import android.content.Context;

import java.util.List;

import tech.cloverfield.kdgplanner.business.domain.Student;

public class StudentRepoSQLite implements IStudentRepo {

    private Context context;

    public StudentRepoSQLite(Context context) {
        this.context = context;
    }


    @Override
    public Student createStudent(Student student) {
        return null;
    }

    @Override
    public Student readStudent(int studentId) {
        return null;
    }

    @Override
    public List<Student> readAllStudent() {
        return null;
    }

    @Override
    public Student updateStudent(Student student) {
        return null;
    }

    @Override
    public void deleteStudent(Student student) {

    }

    @Override
    public void deleteStudent(int studentId) {

    }


}
