package tech.cloverfield.kdgplanner.business.domain.settings;

import android.content.Context;

import java.util.Collection;

import tech.cloverfield.kdgplanner.persistence.settings.ISettingsRepo;
import tech.cloverfield.kdgplanner.persistence.settings.SettingsRepoKeyValue;

public class SettingsManager implements ISettingsManager {

    private ISettingsRepo repo;
    private Context context;

    public SettingsManager(Context context) {
        this.context = context;
        repo = new SettingsRepoKeyValue(context);
    }



    @Override
    public boolean testConnection() {
        return new ConnectionTester().doInBackground();
    }

    @Override
    public void addSetting(String setting, String value) {
        repo.addSetting(setting, value);
    }

    @Override
    public void addSetting(String setting, int value) {
        repo.addSetting(setting, value);
    }

    @Override
    public void addSetting(String setting, boolean value) {
        repo.addSetting(setting, value);
    }

    @Override
    public String getSettingString(String setting) {
        return null;
    }

    @Override
    public int getSettingInt(String setting) {
        return 0;
    }

    @Override
    public boolean getSettingBool(String setting) {
        return false;
    }

    @Override
    public Collection<?> getAllSettingValues() {
        return null;
    }

    @Override
    public Collection<String> getAllSettingKeys() {
        return null;
    }
}
