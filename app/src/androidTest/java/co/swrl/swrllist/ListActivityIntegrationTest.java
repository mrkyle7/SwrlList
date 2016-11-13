package co.swrl.swrllist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.WindowManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsString;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.RootMatchers.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ListActivityIntegrationTest {
    private Activity activity = null;

    @Rule
    public ActivityTestRule listActivityActivityTestRule = new ActivityTestRule<>(ListActivity.class, false, false);

    @Before @After
    public void setUpAndTearDown() {
        clearAllSettings();
    }

    @Test
    public void whatsNewDialogIsOnlyShownOnFirstLaunchOfActivity() throws Exception {
        launchAndWakeUpActivity();

        onView(withId(R.id.whatsNew)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.whatsNewMessage)).check(matches(withText(containsString("Version"))));
        onView(withId(R.id.whatsNewMoreButton)).check(matches(isClickable()));

        onView(withId(R.id.dismissWhatsNewButton)).perform(click());
        onView(withId(R.id.whatsNew)).check(doesNotExist());
        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));

        stopActivity();
        launchAndWakeUpActivity();

        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void whatsNewDialogIsNotShownWhenNotNewVersion() throws Exception {
        setSavedVersionToHugeNumber();
        launchAndWakeUpActivity();

        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));
    }

    private void clearAllSettings() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        settings.edit().clear().apply();
    }

    private void setSavedVersionToHugeNumber() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        int currentVersion = Integer.MAX_VALUE;
        settings.edit().putInt(String.valueOf(R.string.pkey_version_number), currentVersion).apply();
    }

    private void launchAndWakeUpActivity() {
        listActivityActivityTestRule.launchActivity(null);
        activity = listActivityActivityTestRule.getActivity();
        wakeUpDevice(activity);
    }

    private void wakeUpDevice(final Activity activity) {
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    private void stopActivity() {
        activity.finish();
        activity = null;
    }
}


