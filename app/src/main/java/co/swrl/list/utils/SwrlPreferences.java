package co.swrl.list.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import co.swrl.list.R;

public class SwrlPreferences {
    private static final String LOG_TAG = "SWRL_PREFERENCES";
    private final Context context;
    public static final String KEY_VERSION_NUMBER = "VERSION_NUMBER";
    private static final String KEY_AUTH_TOKEN = "AUTH_TOKEN";
    private static final String KEY_USER_ID = "USER_ID";

    public SwrlPreferences(Context context) {
        this.context = context;
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
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionNumber = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return currentVersionNumber;
    }

    private int getSavedVersion() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(KEY_VERSION_NUMBER, 0);
    }

    public String getAuthToken() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String authToken = preferences.getString(KEY_AUTH_TOKEN, null);
        //This next bit of code can be removed once all users have upgraded
        if (authToken == null) {
            try {
                authToken = preferences.getString(String.valueOf(R.string.pkey_auth_token), null);
            } catch (Exception e) {
                Log.i(LOG_TAG, "Failed to get auth token from old key - never mind.");
            }
        }
        saveAuthToken(authToken);
        // End of code to be removed.
        return authToken;
    }

    public int getUserID() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int userID = preferences.getInt(KEY_USER_ID, 0);
        //This next bit of code can be removed once all users have upgraded
        if (userID == 0) {
            try {
                userID = preferences.getInt(String.valueOf(R.string.pkey_user_id), 0);
            } catch (Exception e) {
                Log.i(LOG_TAG, "Failed to get user ID from old key - never mind.");
            }
        }
        saveUserID(userID);
        // End of code to be removed.
        return userID;
    }

    public boolean loggedIn() {
        return getUserID() != 0 && getAuthToken() != null;
    }

    private void savePackageVersion(int version) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putInt(KEY_VERSION_NUMBER, version);
        editor.apply();
    }

    public void saveAuthToken(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public void saveUserID(int id) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor = preferences.edit();
        editor.putInt(KEY_USER_ID, id);
        editor.apply();
    }
}
