package co.swrl.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.view.WindowManager;

import java.net.MalformedURLException;
import java.net.URL;

import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.details.FilmDetails;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class Helpers {
    private Helpers() {}

    static final Swrl THE_MATRIX = new Swrl("The Matrix", Type.FILM);

    static final Swrl THE_MATRIX_RELOADED = new Swrl("The Matrix Reloaded", Type.FILM);
    static final Swrl THE_MATRIX_REVOLUTIONS = new Swrl("The Matrix Revolutions", Type.FILM);
    static final URL THE_MATRIX_POSTER_URL = makeUrl("http://www.posters.com/the_matrix.jpg");
    static final FilmDetails THE_MATRIX_DETAILS = new FilmDetails("The Matrix (1991)", "Overview", "603", THE_MATRIX_POSTER_URL);

    private static URL makeUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    static void clearAllSettings() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        settings.edit().clear().apply();
    }

    static Activity launchAndAvoidWhatsNewDialog(ActivityTestRule testRule) {
        setSavedVersionToHugeNumber();
        return launchAndWakeUpActivity(testRule);
    }

    static void setSavedVersionToHugeNumber() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        int currentVersion = Integer.MAX_VALUE;
        settings.edit().putInt(String.valueOf(R.string.pkey_version_number), currentVersion).apply();
    }

    static Activity restartActivity(Activity activity, ActivityTestRule testRule) {
        stopActivity(activity);
        return launchAndWakeUpActivity(testRule);
    }

    static Activity launchAndWakeUpActivity(ActivityTestRule testRule) {
        return launchAndWakeUpActivity(testRule, null);
    }

    static Activity launchAndWakeUpActivity(ActivityTestRule testRule, Intent intent) {
        testRule.launchActivity(intent);
        Activity activity = testRule.getActivity();
        wakeUpDevice(activity);
        return activity;
    }

    private static void wakeUpDevice(final Activity activity) {
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    private static void stopActivity(Activity activity) {
        activity.finish();
    }

    static void addSwrlsToList(Swrl[] swrls) {
        for (Swrl swrl : swrls) {
            onView(withId(R.id.addItemEditText)).perform(typeText(swrl.getTitle()));
            onView(withId(R.id.addItemButton)).perform(click());
            onView(withText(swrl.getType().toString())).perform(click());
            onData(allOf(is(instanceOf(Swrl.class)), equalTo(swrl))).check(matches(isDisplayed()));
        }
    }

    static void purgeDatabase() {
        SQLiteCollectionManager db = new SQLiteCollectionManager(InstrumentationRegistry.getTargetContext());
        db.permanentlyDeleteAll();
    }
}
