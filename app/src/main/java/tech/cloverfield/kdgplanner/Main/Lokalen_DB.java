package tech.cloverfield.kdgplanner.Main;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Lokalen_DB extends SQLiteOpenHelper {

    private boolean internet = true;
    private boolean loaded = false;
    private static final String DATABASE_NAME = "KDGPLANNER.db";
    private Context context;
    private RequestQueue requestQueue;

    public Lokalen_DB(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void drop() {
        this.getWritableDatabase().execSQL("DROP TABLE Lokalen");
    }

    public void create() {
        this.getWritableDatabase().execSQL("CREATE TABLE IF NOT EXISTS Lokalen(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Campus VARCHAR(2),Classroom VARCHAR(255), Start_Time TIME, End_Time TIME, Date DATE)");
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Lokalen(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Campus VARCHAR(2),Classroom VARCHAR(255), Start_Time TIME, End_Time TIME, Date DATE)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Lokalen");
        onCreate(db);
    }

    public void jsonHandler(JSONArray jsonArray) {
        if (jsonArray == null) {
            internet = false;
            loaded = false;
        } else {
            this.getWritableDatabase().execSQL("DELETE FROM Lokalen");
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    insertClassroom(i, jsonObject.getString("Campus"), jsonObject.getString("Classroom"), jsonObject.getString("Start_Time"), jsonObject.getString("End_Time"), jsonObject.getString("Date"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            loaded = true;
            internet = true;
        }
    }

    public void requestJSON(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                jsonHandler(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //ERROR NO INTERNET
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    public void update() {
        Cursor res = this.getReadableDatabase().rawQuery("SELECT * FROM Lokalen WHERE DATE(Date) = date('now')", null);
        if (res.getCount() < 1) {
            loaded = false;
            requestJSON("http://server.devvix.com:8000/available_classrooms");
        } else {
            loaded = true;
        }
    }

    public boolean insertClassroom(int ID, String Campus, String Classroom, String Start_Time, String End_Time, String Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", ID);
        contentValues.put("Campus", Campus);
        contentValues.put("Classroom", Classroom.replace(" ",""));
        contentValues.put("Start_Time", Start_Time);
        contentValues.put("End_Time", End_Time);
        contentValues.put("Date", Date);
        db.insert("Lokalen", null, contentValues);
        return true;
    }

    public ArrayList<Classroom> getRooms(String Campus, String time) {
        String addCampus = "";
        if (Campus != null) {
            addCampus = " Campus LIKE '%" + Campus.toUpperCase() + "%' AND";
        }

        ArrayList<Classroom> classroomList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT Classroom," +
                " Start_Time FROM" +
                " Lokalen WHERE" +
                addCampus +
                " DATE(Date) = date('now') AND" +
                " date('now') NOT BETWEEN" +
                " TIME(Start_Time, '-15 minutes') AND" +
                " TIME(End_Time, '+5 minutes') AND" +
                " TIME('" + time + "') < Start_Time GROUP BY" +
                " Classroom, Start_Time ORDER BY" +
                " Start_Time ASC", null);

        res.moveToFirst();
        while (!res.isAfterLast()) {
            classroomList.add(new Classroom(res.getString(0), res.getString(1)));
            res.moveToNext();
        }

        res.close();
        return classroomList;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean hasInternet() {
        return internet;
    }
}
