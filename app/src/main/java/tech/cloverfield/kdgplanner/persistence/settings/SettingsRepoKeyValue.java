package tech.cloverfield.kdgplanner.persistence.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collection;

public class SettingsRepoKeyValue implements ISettingsRepo {

    private Context context;
    private SharedPreferences preferences;

    public SettingsRepoKeyValue(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("preferences", 0);

    }

    @Override
    public void addSetting(String setting, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(setting,value);
        editor.apply();
    }

    @Override
    public void addSetting(String setting, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(setting,value);
        editor.apply();
    }

    @Override
    public void addSetting(String setting, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(setting,value);
        editor.apply();
    }

    @Override
    public String getSettingString(String setting) {
        return preferences.getString(setting, "");
    }


    @Override
    public int getSettingInt(String setting) {
        return preferences.getInt(setting, 0);
    }

    @Override
    public boolean getSettingBool(String setting) {
        return preferences.getBoolean(setting, false);
    }

    @Override
    public Collection<?> getAllSettingValues() {
        return preferences.getAll().values();
    }

    @Override
    public Collection<String> getAllSettingKeys() {
        return preferences.getAll().keySet();
    }
}
