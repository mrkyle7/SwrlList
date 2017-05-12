package co.swrl.list;

import android.app.Activity;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.item.Swrl;
import co.swrl.list.ui.ListActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.launchAndWakeUpActivity;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.Helpers.restartActivity;
import static co.swrl.list.Helpers.setSavedVersionToHugeNumber;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InteractingWithTheList {
    private Activity activity = null;
    private static final EspressoKey ENTER_KEY = new EspressoKey.Builder().withKeyCode(KeyEvent.KEYCODE_ENTER).build();

    @Rule
    public ActivityTestRule listActivityActivityTestRule = new ActivityTestRule<>(ListActivity.class, false, false);

    @Before @After
    public void setupAndTearDown() {
        clearAllSettings();
        purgeDatabase();
    }

    @Test
    public void whatsNewDialogIsOnlyShownOnFirstLaunchOfActivity() throws Exception {
        activity = launchAndWakeUpActivity(listActivityActivityTestRule);

        onView(withId(R.id.whatsNew)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.whatsNewMessage)).check(matches(withText(containsString("Version"))));
        onView(withId(R.id.whatsNewMoreButton)).check(matches(isClickable()));

        onView(withId(R.id.dismissWhatsNewButton)).perform(click());
        onView(withId(R.id.whatsNew)).check(doesNotExist());
        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));

        restartActivity(activity, listActivityActivityTestRule);

        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void whatsNewDialogIsNotShownWhenNotNewVersion() throws Exception {
        setSavedVersionToHugeNumber();
        activity = launchAndWakeUpActivity(listActivityActivityTestRule);

        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));
    }


    @Test
    public void canUndoMarkingItemsAsDone() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED, THE_MATRIX_REVOLUTIONS});

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix Revolutions"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(1)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix Reloaded"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(2)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix"))));

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX_RELOADED)))
                .onChildView(withId(R.id.list_item_done))
                .perform(click());

        onView(withId(R.id.snackbar_text)).check(matches(withText("\"The Matrix Reloaded\" marked as done")));
        onView(withId(R.id.snackbar_action)).perform(click());

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix Revolutions"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(1)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix Reloaded"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(2)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix"))));

        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(3))));

        activity = restartActivity(activity, listActivityActivityTestRule);

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix Revolutions"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(1)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix Reloaded"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(2)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix"))));

        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(3))));
    }

    private static Matcher<View> numberOfChildren(final Matcher<Integer> numChildrenMatcher) {
        return new TypeSafeMatcher<View>() {

            /**
             * matching with viewgroup.getChildCount()
             */
            @Override
            public boolean matchesSafely(View view) {
                return view instanceof ViewGroup && numChildrenMatcher.matches(((ViewGroup) view).getChildCount());
            }

            /**
             * gets the description
             */
            @Override
            public void describeTo(Description description) {
                description.appendText(" a view with # children ");
                numChildrenMatcher.describeTo(description);
            }
        };
    }
}


