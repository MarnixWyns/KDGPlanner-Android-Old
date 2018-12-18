package tech.cloverfield.kdgplanner.business;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import tech.cloverfield.kdgplanner.R;
import tech.cloverfield.kdgplanner.business.domain.DateType;
import tech.cloverfield.kdgplanner.foundation.DateFormatter;

public class ClassroomManager implements IClassroomManager {
















    
    @SuppressLint("StaticFieldLeak")
    private void jsonHandler(JSONArray jsonArray) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(final Object[] objects) {
                if (objects[0] == null) {
                    internet = false;
                    isUpdating = false;
                    if ((!loaded && !context.swipeRefreshLayout.isRefreshing()) || context.swipeRefreshLayout.isRefreshing())
                        context.displayWarning(context.getString(R.string.server_connect_error));
                } else {
                    JSONArray jsonArray = (JSONArray) objects[0];
                    onUpgrade(getWritableDatabase(), 0, 0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            if (context.swipeRefreshLayout.isRefreshing())
                                loadedPercentage = String.format("%.2f%%", ((double) 100 / jsonArray.length()) * i);
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String campus = jsonObject.getString("Campus");
                            String classroom = jsonObject.getString("Classroom");
                            String startTime = jsonObject.getString("Start_Time");
                            String endTime = jsonObject.getString("End_Time");
                            String date = jsonObject.getString("Date");
                            insertClassroom(campus, classroom, startTime, endTime, date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (context.swipeRefreshLayout.isRefreshing())
                        loaded = true;
                    isUpdating = false;
                    internet = true;
                }

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (objects[0] != null && context.swipeRefreshLayout != null && context.swipeRefreshLayout.isRefreshing()) {
                            if (context.button.getText().toString().contains(":")) {
                                Calendar calendar = Calendar.getInstance();
                                context.displayAvailable(getRooms(context.getController().getActiveCampus().getLongName(), DateFormatter.toDate(String.format("%s:00.000 %04d-%02d-%02d", context.button.getText().toString(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)), DateType.FULL_DATE_US)));
                            }
                            context.displayWarning("The classrooms are now up-to-date");
                        }
                        if (context.swipeRefreshLayout != null)
                            context.swipeRefreshLayout.setRefreshing(false);
                    }
                });
                return null;
            }
        };

        task.execute(jsonArray);
    }
}
