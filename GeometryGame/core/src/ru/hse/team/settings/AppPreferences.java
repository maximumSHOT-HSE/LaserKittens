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

    private static final String PREF_MUSIC_VOLUME = "music volume";
    private static final String PREF_SOUND_VOLUME = "sound volume";
    private static final String PREFS_NAME = "CATS ARE NICE";
    private static final String PREF_ACCELEROMETER = "ACCELEROMETER";
    private static final String PREF_SHOW_TIME = "SHOW_TIME";

    protected Preferences getPrefs() {
        if (preferences == null) {
            preferences = Gdx.app.getPreferences(PREFS_NAME);
        }
        return preferences;
    }

    public float getMusicVolume() {
        return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    public void setMusicVolume(float volume) {
        getPrefs().putFloat(PREF_MUSIC_VOLUME, volume);
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

    public boolean isShowTime() {
        return getPrefs().getBoolean(PREF_SHOW_TIME, true);
    }

    public void setShowTime(boolean value) {
        getPrefs().putBoolean(PREF_SHOW_TIME, value);
        getPrefs().flush();;
    }
}
