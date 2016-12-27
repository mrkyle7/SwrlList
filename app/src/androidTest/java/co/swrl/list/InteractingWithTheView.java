package co.swrl.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;

import static android.R.attr.id;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.addSwrlsToList;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.launchAndWakeUpActivity;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.Helpers.restartActivity;
import static co.swrl.list.Helpers.setSavedVersionToHugeNumber;
import static co.swrl.list.R.id.container;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InteractingWithTheView {
    private Activity activity = null;

    @Rule
    public ActivityTestRule viewActivityActivityTestRule = new ActivityTestRule<>(ViewActivity.class, false, false);

    @Before
    public void setUp() {
        clearAllSettings();
    }

    @After
    public void tearDown() {
        clearAllSettings();
        purgeDatabase(activity);
    }

    @Test
    public void swrlDetailsAreDisplayedAndOtherSwrlsCanBeNavigatedToBySwiping() throws Exception {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent matrixFirst = new Intent(targetContext, ViewActivity.class);

        ArrayList<Swrl> swrls = new ArrayList<>();
        swrls.add(THE_MATRIX);
        swrls.add(THE_MATRIX_RELOADED);

        matrixFirst.putExtra("swrls", swrls);
        matrixFirst.putExtra("index", 0);

        activity = launchAndWakeUpActivity(viewActivityActivityTestRule, matrixFirst);

        onView(allOf(withText("The Matrix"), withParent(withId(R.id.toolbar)))).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.container)).perform(swipeLeft());

        onView(allOf(withText("The Matrix Reloaded"), withParent(withId(R.id.toolbar)))).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.container)).perform(swipeRight());

        onView(allOf(withText("The Matrix"), withParent(withId(R.id.toolbar)))).check(matches(isCompletelyDisplayed()));
    }
}


