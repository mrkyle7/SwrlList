package co.swrl.list;

import android.app.Activity;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.item.Swrl;
import co.swrl.list.ui.activity.ListActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED_DETAILS;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.purgeDatabase;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InteractingWithTheView {
    private Activity activity = null;

    @Rule
    public ActivityTestRule listActivityActivityTestRule = new ActivityTestRule<>(ListActivity.class, false, false);

    @Before @After
    public void setupAndTearDown() {
        clearAllSettings();
        purgeDatabase();
    }

    @Test
    public void swrlDetailsAreDisplayedAndOtherSwrlsCanBeNavigatedToBySwiping() throws Exception {
        launchViewWithTheMatrixSelected();

        onView(withId(R.id.container)).perform(swipeRight());

        onView(allOf(withText("The Matrix Reloaded"), withId(R.id.title))).check(matches(isDisplayed()));

        onView(withId(R.id.container)).perform(swipeLeft());

        onView(allOf(withText("The Matrix"), withId(R.id.title))).check(matches(isDisplayed()));
    }

    @Test
    public void canMarkASwrlAsDoneUsingTheButton() throws Exception {
        launchViewWithTheMatrixSelected();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Mark As Done")).perform(click());

        int OK = android.R.id.button1;
        onView(withId(OK)).perform(click());

        onView(allOf(withText("The Matrix Reloaded"), withId(R.id.title))).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.container)).perform(swipeRight());
        onView(allOf(withText("The Matrix Reloaded"), withId(R.id.title))).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.container)).perform(swipeLeft());
        onView(allOf(withText("The Matrix Reloaded"), withId(R.id.title))).check(matches(isCompletelyDisplayed()));

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Mark As Done")).perform(click());

        int CANCEL = android.R.id.button2;
        onView(withId(CANCEL)).perform(click());

        onView(allOf(withText("The Matrix Reloaded"), withId(R.id.title))).check(matches(isCompletelyDisplayed()));
    }

    private void launchViewWithTheMatrixSelected() {
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        THE_MATRIX_RELOADED.setDetails(THE_MATRIX_RELOADED_DETAILS);
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), click()));;

        onView(allOf(withText("The Matrix"), withId(R.id.title))).check(matches(isCompletelyDisplayed()));
    }
}