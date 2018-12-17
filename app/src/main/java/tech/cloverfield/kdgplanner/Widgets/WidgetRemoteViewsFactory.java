package tech.cloverfield.kdgplanner.Widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import tech.cloverfield.kdgplanner.DateFormatter;
import tech.cloverfield.kdgplanner.Domain.Classroom;
import tech.cloverfield.kdgplanner.Domain.DateType;
import tech.cloverfield.kdgplanner.R;

public class WidgetRemoteViewsFactory extends SQLiteOpenHelper implements RemoteViewsService.RemoteViewsFactory {

    private static final String PREF_PREFIX_KEY = "kdg-planner-widget";
    private static final String PREFS_NAME = "tech.cloverfield.kdgplanner.Widgets.WidgetAvailableClassrooms";
    private int appWidgetId;
    private Context mContext;
    private Cursor mCursor;
    private static final String DATABASE_NAME = "KDGPLANNER.db";
    private String campus;
    private HashMap<String, String> campusFormatter = new HashMap<>();

    public WidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        super(applicationContext, DATABASE_NAME, null, 1);
        onCreate(this.getWritableDatabase());
        mContext = applicationContext;
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences prefs = applicationContext.getSharedPreferences(PREFS_NAME, 0);
        campus = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        //campus = intent.getStringExtra("kdgPlannerCampus");
        campusFormatter.put("groenplaats", "GR");
        campusFormatter.put("pothoek", "PH");
        campusFormatter.put("stadswaag", "SW");
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Lokalen(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Campus VARCHAR(2),Classroom VARCHAR(255), Start_Time Time, End_Time Time, Date DATE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Lokalen");
        onCreate(db);
    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        mCursor = getRooms(Calendar.getInstance().getTime());
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public int getCount() {
        return mCursor == null? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || mCursor == null || !mCursor.moveToPosition(position)) {
            return null;
        }

        Date endDate = DateFormatter.toDate(mCursor.getString(2) + ".000 " + mCursor.getString(1), DateType.FULL_DATE_US);
        Classroom classroom = new Classroom(mCursor.getString(0), endDate, Calendar.getInstance().getTime(), mContext);

        String displayString = String.format(mContext.getString(R.string.class_availability), classroom.getIdentifier(), classroom.getDuration());

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
        rv.setTextViewText(R.id.widgetListItem, displayString);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mCursor.moveToPosition(position) ? mCursor.getLong(0) : position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public Cursor getRooms(Date time) {
        String campus = campusFormatter.get(this.campus.toLowerCase());
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
        return cursor;
    }
}
