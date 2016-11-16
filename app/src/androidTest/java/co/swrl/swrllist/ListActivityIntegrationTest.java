package co.swrl.swrllist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static android.support.test.espresso.matcher.RootMatchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

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

    @Test
    public void canAddAnItemToTheListThenClearsTextAndRetainsFocus() throws Exception {
        avoidWhatsNewDialog();

        onView(withId(R.id.addItemEditText)).perform(typeText("The Matrix"));
        onView(withId(R.id.addItemButton)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("The Matrix"))).check(matches(isDisplayed()));
        onView(withId(R.id.addItemEditText)).check(matches(hasFocus()));
        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));
    }

    @Test
    public void canAddAnItemToTheListOnEnterKeyThenClearsTextAndRetainsFocus() throws Exception {
        avoidWhatsNewDialog();

        onView(withId(R.id.addItemEditText))
                .perform(typeText("The Matrix"))
                .perform(pressImeActionButton());

        onData(allOf(is(instanceOf(String.class)), is("The Matrix"))).check(matches(isDisplayed()));
        onView(withId(R.id.addItemEditText)).check(matches(hasFocus()));
        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));

    }

    @Test
    public void newItemsInTheListAreAddedOnTop() throws Exception {
        avoidWhatsNewDialog();

        onView(withId(R.id.addItemEditText))
                .perform(typeText("First Item"))
                .perform(pressImeActionButton());

        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.itemListView)).atPosition(0).check(matches(withText(containsString("First Item"))));

        onView(withId(R.id.addItemEditText))
                .perform(typeText("The Matrix"))
                .perform(pressImeActionButton());

        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.itemListView)).atPosition(0).check(matches(withText(containsString("The Matrix"))));


        onView(withId(R.id.addItemEditText)).perform(typeText("The Jungle Book"));
        onView(withId(R.id.addItemButton)).perform(click());

        onData(is(instanceOf(String.class))).inAdapterView(withId(R.id.itemListView)).atPosition(0).check(matches(withText(containsString("The Jungle Book"))));
    }

    @Test
    public void emptyTextIsNotAddedToTheList() throws Exception {
        avoidWhatsNewDialog();

        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));

        onView(withId(R.id.addItemButton)).perform(click());
        onView(withId(R.id.addItemEditText)).perform(pressImeActionButton());

        onView(withId(R.id.itemListView)).check(matches(not(withAdaptedData(isEmptyString()))));
    }

    private void avoidWhatsNewDialog() {
        setSavedVersionToHugeNumber();
        launchAndWakeUpActivity();
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

    private static Matcher<View> withAdaptedData(final Matcher<String> dataMatcher) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with class name: ");
                dataMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof AdapterView)) {
                    return false;
                }
                @SuppressWarnings("rawtypes")
                Adapter adapter = ((AdapterView) view).getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    if (dataMatcher.matches(adapter.getItem(i))) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
}


