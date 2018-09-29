package tech.cloverfield.kdgplanner.Main;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

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
import java.util.Calendar;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.Objects.Campus;
import tech.cloverfield.kdgplanner.Objects.Classroom;

public class Lokalen_DB extends SQLiteOpenHelper {

    private HashMap<String, Campus> campussen = new HashMap<>();
    private boolean internet = true;
    private boolean loaded = false;
    private boolean force = false;
    private boolean refresing = false;
    private boolean isUpdating;

    private static final String DATABASE_NAME = "KDGPLANNER.db";
    private MainActivity context;
    private RequestQueue requestQueue;

    public Lokalen_DB(MainActivity context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Lokalen(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Campus VARCHAR(2),Classroom VARCHAR(255), Start_Time TIME, End_Time TIME, Date DATE)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Lokalen");
        onCreate(db);
    }

    private void load() {
        Cursor res = this.getReadableDatabase().rawQuery("SELECT * FROM Lokalen", null);
        /*contentValues.put("ID", ID);
        contentValues.put("Campus", Campus);
        contentValues.put("Classroom", Classroom.replace(" ", ""));
        contentValues.put("Start_Time", Start_Time);
        contentValues.put("End_Time", End_Time);
        contentValues.put("Date", Date);*/


    }

    @SuppressLint("StaticFieldLeak")
    private void jsonHandler(JSONArray jsonArray) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                JSONArray jsonArray = (JSONArray) objects[0];
                if (jsonArray == null) {
                    internet = false;
                    loaded = false;
                    isUpdating = false;
                } else {
                    getWritableDatabase().execSQL("DELETE FROM Lokalen");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            insertClassroom(i, jsonObject.getString("Campus"), jsonObject.getString("Classroom"), jsonObject.getString("Start_Time"), jsonObject.getString("End_Time"), jsonObject.getString("Date"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (force) {
                                Calendar calendar = Calendar.getInstance();
                                String time = DateFormatter.fixTimeString(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
                                context.displayAvailable(getRooms(context.convertCampus(context.getSelectedCampus()), time), false);
                            }

                            if (refresing) {
                                context.swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });

                    isUpdating = false;
                    loaded = true;
                    internet = true;
                }
                return null;
            }
        };

        task.execute(jsonArray);
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
                internet = false;
                isUpdating = false;
                //Log.e("Volley Error", error.toString());
            }
        });

        /*jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                200,
                7,
                2));*/

        requestQueue.add(jsonArrayRequest);
    }

    public void update(String campus) {
        if (isUpdating) return;

        isUpdating = true;
        campus = campus.toUpperCase();

        Cursor res = this.getReadableDatabase().rawQuery("SELECT * FROM Lokalen WHERE DATE(Date) = date('now') AND TIME(Start_Time) > time('now') AND Campus = '" + campus + "'", null);
        if (res.getCount() < 1) {
            loaded = false;
        } else {
            loaded = true;
        }

        requestJSON("http://server.devvix.com:8000/available_classrooms");
    }

    public boolean insertClassroom(int ID, String Campus, String Classroom, String Start_Time, String End_Time, String Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", ID);
        contentValues.put("Campus", Campus);
        contentValues.put("Classroom", Classroom.replace(" ", ""));
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

    public void setRefreshing(boolean value) {
        refresing = value;
    }

    public void setForced(boolean value) {
        force = value;
    }
}
