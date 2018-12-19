package tech.cloverfield.kdgplanner.persistence.classrooms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.Collection;
import java.util.Date;

import tech.cloverfield.kdgplanner.application.MainActivity;
import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.business.domain.Lesson;
import tech.cloverfield.kdgplanner.business.domain.Classroom;

public class ClassroomRepoSQLite extends SQLiteOpenHelper implements IClassroomRepo {

    private boolean internet = true;
    private boolean loaded = false;
    private boolean isUpdating = false;
    private String loadedPercentage = "0%";

    private static final String DATABASE_NAME = "KDGPLANNER.db";
    private MainActivity context;
    private RequestQueue requestQueue;

    public ClassroomRepoSQLite(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = (MainActivity) context;
        requestQueue = Volley.newRequestQueue(context);
        onCreate(this.getWritableDatabase());
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Lokalen(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Campus VARCHAR(2),Classroom VARCHAR(255), Start_Time Time, End_Time Time, Date DATE)");
    }

    public void createIfNotExist() {
        onCreate(this.getWritableDatabase());
    }

    public void drop() {
        this.getWritableDatabase().execSQL("DROP TABLE Lokalen");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Lokalen");
        onCreate(db);
    }

    @Override
    public void updateClassrooms(Campus activeCampus) {

    }

    @Override
    public Collection<Classroom> getAvailableClassrooms(Campus campus, Date date) {
        return null;
    }

    @Override
    public Collection<Lesson> getLessonsByRoom(Campus campus, Classroom room) {
        return null;
    }

    @Override
    public int getLoadPercentage() {
        return 0;
    }
}
