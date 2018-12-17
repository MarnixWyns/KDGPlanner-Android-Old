package tech.cloverfield.kdgplanner.Widgets;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import tech.cloverfield.kdgplanner.R;

/**
 * The configuration screen for the {@link WidgetAvailableClassrooms WidgetAvailableClassrooms} AppWidget.
 */
public class WidgetAvailableClassroomsConfigureActivity extends Activity {

    private static final String PREFS_NAME = "tech.cloverfield.kdgplanner.Widgets.WidgetAvailableClassrooms";
    private static final String PREF_PREFIX_KEY = "kdg-planner-widget";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public WidgetAvailableClassroomsConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.widget_available_classrooms_configure);
        Button groenplaatsBtn = (Button) findViewById(R.id.groenplaatsBtn);
        Button pothoekBtn = (Button) findViewById(R.id.pothoekBtn);
        Button stadswaagBtn = (Button) findViewById(R.id.stadswaagBtn);
        groenplaatsBtn.setOnClickListener(buttonOnClickListener);
        pothoekBtn.setOnClickListener(buttonOnClickListener);
        stadswaagBtn.setOnClickListener(buttonOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //mAppWidgetText.setText(loadTitlePref(WidgetAvailableClassroomsConfigureActivity.this, mAppWidgetId));
    }

    View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = WidgetAvailableClassroomsConfigureActivity.this;

            // When the button is clicked, store the string locally
            String choice = ((Button) v).getText().toString();
            saveTitlePref(context, mAppWidgetId, choice);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            WidgetAvailableClassrooms.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
}

