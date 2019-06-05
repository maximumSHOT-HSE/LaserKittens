package ru.hse.team;

import android.app.Activity;
import android.widget.Toast;

public class Actions implements AndroidActions {

    private final Activity activity;

    public Actions(Activity activity) {
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
