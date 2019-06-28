package ru.hse.team.settings;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Preferences class based on libgdx preferences
 */
public class AppPreferences {
    // that pointer needs to be saved due to some strange behavior on android
    // (if it is not, saved settings get cleared)
    private Preferences preferences;

    private static final String PREF_PLAYER_NAME = "player name";
    private static final String PREF_SOUND_VOLUME = "sound volume";
    private static final String PREFS_SETTINGS_NAME = "CATS ARE NICE";
    private static final String PREF_ACCELEROMETER = "ACCELEROMETER";
    private static final String PREF_SHOW_TIME = "SHOW_TIME";
    private static final String PREF_FOG = "FOG";

    protected Preferences getPrefs() {
        if (preferences == null) {
            preferences = Gdx.app.getPreferences(PREFS_SETTINGS_NAME);
        }
        return preferences;
    }

    public String getPlayerName() {
        return getPrefs().getString(PREF_PLAYER_NAME, "Player");
    }

    public void setPlayerName(String playerName) {
        getPrefs().putString(PREF_PLAYER_NAME, playerName);
        getPrefs().flush();
    }

    public float getSoundVolume() {
        return getPrefs().getFloat(PREF_SOUND_VOLUME, 0.5f);
    }

    public void setSoundVolume(float volume) {
        getPrefs().putFloat(PREF_SOUND_VOLUME, volume);
        getPrefs().flush();
    }

    public boolean isEnabledAccelerometer() {
        return getPrefs().getBoolean(PREF_ACCELEROMETER, false);
    }

    public void setEnableAccelerometer(boolean enabled) {
        getPrefs().putBoolean(PREF_ACCELEROMETER, enabled);
        getPrefs().flush();
    }

    public boolean isEnabledFog() {
        return getPrefs().getBoolean(PREF_FOG, false);
    }

    public void setEnabledFog(boolean enabled) {
        getPrefs().putBoolean(PREF_FOG, enabled);
        getPrefs().flush();
    }

    public boolean isShowTime() {
        return getPrefs().getBoolean(PREF_SHOW_TIME, true);
    }

    public void setShowTime(boolean value) {
        getPrefs().putBoolean(PREF_SHOW_TIME, value);
        getPrefs().flush();
    }
}
