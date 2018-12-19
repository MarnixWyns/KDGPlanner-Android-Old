package tech.cloverfield.kdgplanner.business.settings;

import java.util.Collection;

public interface ISettingsManager {
    boolean testConnection();

    void addSetting(String setting, String value);
    void addSetting(String setting, int value);
    void addSetting(String setting, boolean value);
    String getSettingString(String setting);
    int getSettingInt(String setting);
    boolean getSettingBool(String setting);
    Collection<?> getAllSettingValues();
    Collection<String> getAllSettingKeys();
}
