package tech.cloverfield.kdgplanner.business.settings;

import android.os.AsyncTask;

import java.net.URL;
import java.net.URLConnection;

public class ConnectionTester extends AsyncTask<Void, Void ,Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        try{
            URL myUrl = new URL("http://www.google.be");
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(30);
            connection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
