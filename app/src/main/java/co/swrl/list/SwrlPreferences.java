package co.swrl.list;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class SwrlPreferences {
    private final Activity activity;

    public SwrlPreferences(Activity activity) {
        this.activity = activity;
    }


    public boolean isPackageNewVersion() {
        int currentVersionNumber = getCurrentVersionNumber();
        int savedVersion = getSavedVersion();
        return savedVersion < currentVersionNumber;
    }

    public void savePackageVersionAsCurrentVersion() {
        savePackageVersion(getCurrentVersionNumber());
    }

    private int getCurrentVersionNumber() {
        int currentVersionNumber = 0;
        try {
            PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionNumber;
    }

    private int getSavedVersion() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);

        return preferences.getInt(String.valueOf(R.string.pkey_version_number), 0);
    }

    private void savePackageVersion(int version) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        Editor editor = preferences.edit();
        editor.putInt(String.valueOf(R.string.pkey_version_number), version);
        editor.apply();
    }
}
