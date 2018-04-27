package tech.cloverfield.kdgplanner.WebRequest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    private static String url;
    private static HashMap<String, String> getValues = new HashMap<>();
    private static RequestQueue requestQueue;

    public Manager(Context context, String requestURL, HashMap<String, String> values) {
        for (String s : values.keySet()) {
            getValues.put(s, values.get(s));
        }
        url = requestURL;
        requestQueue = Volley.newRequestQueue(context);
    }

    public static File sendRequest() {
        UrlRequest urlRequest = new UrlRequest(getValues, Request.Method.GET)
    }

    public static File download(String downloadUrl) {

    }

}
