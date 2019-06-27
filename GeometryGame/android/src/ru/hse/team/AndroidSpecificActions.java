package ru.hse.team;

import android.app.Activity;
import android.widget.Toast;

/**
 * Class which contains general android specific actions interface,
 *  to make later use of them outside of android module
 */
public class AndroidSpecificActions implements AndroidActions {

    private final Activity activity;

    public AndroidSpecificActions(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void showToast(String message, boolean isLong) {
        if (isLong) {
            activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
        } else {
            activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_LONG).show());
        }
    }
}
