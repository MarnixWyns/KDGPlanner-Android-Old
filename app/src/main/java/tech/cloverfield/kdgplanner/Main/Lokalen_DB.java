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
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.Objects.Campus;
import tech.cloverfield.kdgplanner.Objects.Classroom;
import tech.cloverfield.kdgplanner.Objects.DateType;
import tech.cloverfield.kdgplanner.Objects.Uur;
import tech.cloverfield.kdgplanner.R;

public class Lokalen_DB extends SQLiteOpenHelper {

    private HashMap<String, Campus> campussen = new HashMap<>();
    private boolean internet = true;
    private boolean loaded = false;
    private boolean force = false;
    private boolean isUpdating = false;
    private String loadedPercentage = "0%";

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

    private void populateStorageClass(String campusInitials, String classroomID, String startTime, String endTime, String date) {
        Date startDate = DateFormatter.toDate(String.format("%s %s", startTime, date), DateType.FULL_DATE_US);
        Date endDate = DateFormatter.toDate(String.format("%s %s", endTime, date), DateType.FULL_DATE_US);

        Campus campus = campussen.get(campusInitials);
        if (campus == null) campus = new Campus(campusInitials);
        Uur uur = new Uur(startDate, endDate);
        campus.addUur(classroomID, startDate, uur);
        campussen.put(campusInitials, campus);
    }

    private void populateStorageClasses() {
        Cursor res = this.getWritableDatabase().rawQuery("SELECT * FROM Lokalen",null);
        res.moveToFirst();

        do {
            populateStorageClass(res.getString(1), res.getString(2), res.getString(3), res.getString(4), res.getString(5));
        } while (res.moveToNext());
    }

    @SuppressLint("StaticFieldLeak")
    private void jsonHandler(JSONArray jsonArray) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(final Object[] objects) {
                if (objects[0] == null) {
                    internet = false;
                    isUpdating = false;
                    if ((!loaded && !force) || force) context.displayWarning(context.getString(R.string.server_connect_error));
                } else {
                    campussen = new HashMap<>();
                    JSONArray jsonArray = (JSONArray) objects[0];
                    onUpgrade(getWritableDatabase(), 0, 0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            loadedPercentage = String.format("%.2f%%", ((double) 100 / jsonArray.length()) * i);
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String campus = jsonObject.getString("Campus");
                            String classroom = jsonObject.getString("Classroom");
                            String startTime = jsonObject.getString("Start_Time");
                            String endTime = jsonObject.getString("End_Time");
                            String date = jsonObject.getString("Date");
                            insertClassroom(i, campus, classroom, startTime, endTime, date);
                            populateStorageClass(campus, classroom, startTime, endTime, date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    isUpdating = false;
                    loaded = true;
                    internet = true;
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.swipeRefreshLayout.setRefreshing(false);

                        if (objects[0] != null && force) {
                            if (context.button.getText().toString().contains(":")) {
                                Calendar calendar = Calendar.getInstance();
                                context.displayAvailable(getRooms(context.convertCampus(context.getSelectedCampus()), DateFormatter.toDate(String.format("%s:00 %04d-%02d-%02d", context.button.getText().toString(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)), DateType.FULL_DATE_US)), false);
                            }
                            context.displayWarning("The classrooms are now up-to-date");
                        }
                    }
                });
                return null;
            }
        };

        task.execute(jsonArray);
    }

    public void update(String campus) {
        if (isUpdating) return;
        isUpdating = true;

        Cursor res = this.getReadableDatabase().rawQuery("SELECT * FROM Lokalen WHERE DATE(Date) = date('now') AND DATETIME(Start_Time) > time('now') AND Campus = '" + campus + "'", null);
        loaded = res.getCount() >= 1;
        if (!loaded) context.swipeRefreshLayout.setRefreshing(true);
        if (loaded) populateStorageClasses();
        res.close();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "http://server.devvix.com:8000/available_classrooms", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                jsonHandler(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                internet = false;
                isUpdating = false;
                if ((!loaded && !force) || force) context.displayWarning("Error while connecting to the server,\nplease check your connection or try again later");
                context.swipeRefreshLayout.setRefreshing(false);
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void insertClassroom(int ID, String Campus, String Classroom, String Start_Time, String End_Time, String Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", ID);
        contentValues.put("Campus", Campus);
        contentValues.put("Classroom", Classroom.replace(" ", ""));
        contentValues.put("Start_Time", Start_Time);
        contentValues.put("End_Time", End_Time);
        contentValues.put("Date", Date);
        db.insert("Lokalen", null, contentValues);
    }

    public ArrayList<Classroom> getRooms(String campus, Date time) {
        if (!campussen.containsKey(campus)) return new ArrayList<>();
        return campussen.get(campus).getAvailableClassrooms(time);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean hasInternet() {
        return internet;
    }

    public void setForced(boolean value) {
        force = value;
    }

    public String getLoadedPercentage() {
        return loadedPercentage;
    }
}
