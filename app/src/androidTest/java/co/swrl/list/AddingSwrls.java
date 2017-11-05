package co.swrl.list;

import android.app.Activity;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.item.Swrl;
import co.swrl.list.ui.activity.AddSwrlActivity;
import co.swrl.list.ui.activity.ListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.atPosition;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.Helpers.restartActivity;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddingSwrls {
    private Activity activity = null;

    @Rule
    public ActivityTestRule listActivityActivityTestRule = new ActivityTestRule<>(ListActivity.class, false, false);

    @Rule
    public ActivityTestRule<AddSwrlActivity> addSwrlActivityActivityTestRule = new ActivityTestRule<>(AddSwrlActivity.class, false, false);

    @Before
    @After
    public void setupAndTearDown() {
        clearAllSettings();
        purgeDatabase();
    }

    @Test
    public void newItemsInTheListAreAddedOnTop() throws Exception {

        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, null);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_film)).perform(click());

        onView(withId(R.id.addSwrlText))
                .perform(typeText("Another Film"));
        onView(withId(R.id.add_swrl_button)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Another Film")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));
    }

    @Test
    public void duplicateItemsReplaceExisting() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, null);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_film)).perform(click());

        onView(withId(R.id.addSwrlText))
                .perform(typeText("The Matrix"));
        onView(withId(R.id.add_swrl_button)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
    }

    @Test
    public void cannotAddSwrlWithNoTitle() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, null, null);

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_film)).perform(click());

        onView(withId(R.id.addSwrlText)).check(matches(withText(isEmptyString())));
        onView(withId(R.id.add_swrl_button)).check(matches(not(isEnabled())));

        onView(withId(R.id.addSwrlText))
                .perform(typeText("a"));
        onView(withId(R.id.add_swrl_button)).check(matches(isEnabled()));
    }

    @Test
    public void itemsOnTheListArePersistedAfterRestart() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX}, null);
        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix")))));

        activity = restartActivity(activity, listActivityActivityTestRule);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix")))));
    }
}


