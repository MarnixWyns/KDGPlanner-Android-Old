package tech.cloverfield.kdgplanner.WebRequest;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

public class Manager {
/*
    private String url = "https://mijnrooster.kdg.be/export?";
    private HashMap<String, String> values = new HashMap<>();
    private RequestQueue requestQueue;
    private Context context;

    public Manager(Context context) {
        for (String s : values.keySet()) {
            this.values.put(s, values.get(s));
        }

        this.requestQueue = Volley.newRequestQueue(context);
        this.context = context;

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        int endWeek = end.get(Calendar.WEEK_OF_YEAR) + 2;
        if (endWeek > 52) endWeek = (endWeek - 52);
        end.set(Calendar.WEEK_OF_YEAR, endWeek);

        values.put("format", "CSV_ZONE");
        values.put("locale", "nl");
        values.put("group", "false");
        values.put("deduplicate", "true");
        values.put("addSubscriptions", "false");
        values.put("delimiter", "%2C");
        values.put("startDate", start.getTimeInMillis() + "");
        values.put("endDate", end.getTimeInMillis() + "");

    }

    public File sendRequest() {
        for (String key : values.keySet()) {
            String value = values.get(key);
            url += "&" + key + "=" + value;
        }

        url = url.replace("?&", "?");

        Log.d("Response", "Requesting: " + url);

        StringRequest GETRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error.Response", error.toString());
            }
        });

        requestQueue.add(GETRequest);

        //TODO: RETURN ACTUAL FILE
        return null;
    }

    public File download(String downloadUrl) {
        return null;
    }*/

}
