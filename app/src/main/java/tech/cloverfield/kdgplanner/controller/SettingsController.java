package tech.cloverfield.kdgplanner.controller;

import android.content.Context;

import tech.cloverfield.kdgplanner.business.settings.ISettingsManager;
import tech.cloverfield.kdgplanner.business.settings.SettingsManager;

public class SettingsController {
    private boolean hasInternet;
    private Context context;
    private ISettingsManager manager;

    public SettingsController(Context context) {
        this.context = context;
        manager = new SettingsManager(context);
        hasInternet = testConnection();
    }

    public boolean HasInternet() {
        return hasInternet;
    }

    private boolean testConnection(){
        return manager.testConnection();
    }
}
