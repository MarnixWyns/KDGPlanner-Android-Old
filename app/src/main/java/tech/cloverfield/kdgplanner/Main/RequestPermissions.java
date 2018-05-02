package tech.cloverfield.kdgplanner.Main;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class RequestPermissions {

    private static int PERMISSION_KEY = 64556;
    private static boolean isPermissionGranted = false;
    private static boolean requestInProgress = false;

    public static void requestPermissions(Activity activity) {
        requestInProgress = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED || activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_KEY);
            } else {
                isPermissionGranted = true;
                requestInProgress = false;
            }
        } else {
            isPermissionGranted = true;
            requestInProgress = false;
        }
    }

    public static void setGranted(boolean value) {
        isPermissionGranted = value;
    }

    public static boolean isGranted() {
        return isPermissionGranted;
    }

    public static int getRequestCode() {
        return PERMISSION_KEY;
    }

    public static boolean getRequestInProgress() {
        return requestInProgress;
    }

    public static void setRequestInProgress(boolean value) {
        requestInProgress = value;
    }

}
