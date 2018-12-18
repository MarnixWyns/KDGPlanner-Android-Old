package tech.cloverfield.kdgplanner.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
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
import java.util.Collections;
import java.util.Date;

import tech.cloverfield.kdgplanner.application.MainActivity;
import tech.cloverfield.kdgplanner.business.domain.Campus;
import tech.cloverfield.kdgplanner.foundation.DateFormatter;
import tech.cloverfield.kdgplanner.business.domain.Classroom;
import tech.cloverfield.kdgplanner.business.domain.DateType;
import tech.cloverfield.kdgplanner.R;

public class SQLiteClassroomRepo extends SQLiteOpenHelper {

    private boolean internet = true;
    private boolean loaded = false;
    private boolean isUpdating = false;
    private String loadedPercentage = "0%";

    private static final String DATABASE_NAME = "KDGPLANNER.db";
    private MainActivity context;
    private RequestQueue requestQueue;

    public SQLiteClassroomRepo(Context context) {
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



    public void update(Campus campus) {
        if (isUpdating) return;
        isUpdating = true;

        Cursor res = this.getReadableDatabase().rawQuery("SELECT * FROM Lokalen WHERE Date('now') = Date AND Campus = '" + campus.getLongName() + "'", null);

        loaded = res.getCount() >= 1;
        if (!loaded)
            context.swipeRefreshLayout.setRefreshing(true);
        res.close();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, "https://www.devvix.com:2087/api/partial/free", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                jsonHandler(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                internet = false;
                isUpdating = false;
                if ((!loaded && !context.swipeRefreshLayout.isRefreshing()) || context.swipeRefreshLayout.isRefreshing())
                    context.displayWarning("Error while connecting to the server,\nplease check your connection or try again later");
                context.swipeRefreshLayout.setRefreshing(false);
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    private void insertClassroom(String Campus, String Classroom, String Start_Time, String End_Time, String Date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Campus", Campus);
        contentValues.put("Classroom", Classroom);
        contentValues.put("Start_Time", Start_Time);
        contentValues.put("End_Time", End_Time);
        contentValues.put("Date", Date);
        db.insert("Lokalen", null, contentValues);
    }

    public ArrayList<Classroom> getRooms(String campus, Date time) {
        ArrayList<Classroom> rooms = new ArrayList<>();
        boolean checked = false;
        Cursor cursor = null;

        do {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = String.format("SELECT Classroom, Date, End_Time FROM Lokalen WHERE Campus = '%s' AND Date('%s') = Date AND Time('%s') BETWEEN Start_Time AND Time(End_Time, '-%d MINUTES')", campus, DateFormatter.decode(time, DateType.DATE_US), DateFormatter.decode(time, DateType.TIME_FULL), 15);
            Cursor newCursor = db.rawQuery(query, null);
            if (cursor != null && newCursor.getCount() <= cursor.getCount()) checked = true;
            cursor = newCursor;
        } while (!checked && cursor != null);

        cursor.moveToFirst();
        cursor.getCount();
        while (cursor.moveToNext()) {
            Date endDate = DateFormatter.toDate(cursor.getString(2) + ".000 " + cursor.getString(1), DateType.FULL_DATE_US);
                rooms.add(new Classroom(cursor.getString(0), endDate, time, context));
        }
        try {
            Collections.sort(rooms);
        } catch (NullPointerException e) {
            System.out.println("kdgPlanner: " + e.getMessage());
        }
        return rooms;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean hasInternet() {
        return internet;
    }

    public String getLoadedPercentage() {
        return loadedPercentage;
    }
}
