package tech.cloverfield.kdgplanner.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import tech.cloverfield.kdgplanner.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetAvailableClassroomsConfigureActivity WidgetAvailableClassroomsConfigureActivity}
 */
public class WidgetAvailableClassrooms extends AppWidgetProvider {

    private static final String PREF_PREFIX_KEY = "kdg-planner-widget";
    private static final String PREFS_NAME = "tech.cloverfield.kdgplanner.Widgets.WidgetAvailableClassrooms";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String campus = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_available_classrooms);
        remoteViews.setTextViewText(R.id.widgetCampusTitle, campus);

        Intent intent = new Intent(context, WidgetRemoteViewsService.class);
        intent.putExtra("kdgPlannerCampus", campus);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        remoteViews.setRemoteAdapter(R.id.lvClassroomsWidget, intent);

        //ConfigureSettingsButton
        Intent configIntent = new Intent(context, WidgetAvailableClassroomsConfigureActivity.class);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.settingsBtn, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId: appWidgetIds) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            String campus = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);

            RemoteViews views = new RemoteViews(
                    context.getPackageName(),
                    R.layout.widget_available_classrooms
            );

            Intent intent = new Intent(context, WidgetRemoteViewsService.class);
            views.setTextViewText(R.id.widgetCampusTitle, campus);
            intent.putExtra("kdgPlannerCampus", campus);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            views.setRemoteAdapter(R.id.lvClassroomsWidget, intent);

            //ConfigureSettingsButton
            Intent configIntent = new Intent(context, WidgetAvailableClassroomsConfigureActivity.class);
            configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, configIntent, 0);
            views.setOnClickPendingIntent(R.id.settingsBtn, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetAvailableClassroomsConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }
}

