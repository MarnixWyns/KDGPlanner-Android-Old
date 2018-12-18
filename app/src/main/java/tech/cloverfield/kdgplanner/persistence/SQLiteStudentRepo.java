package tech.cloverfield.kdgplanner.persistence;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import tech.cloverfield.kdgplanner.business.domain.Student;

public class SQLiteStudentRepo extends SQLiteOpenHelper implements IStudentRepo {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users.db";

    public SQLiteStudentRepo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
