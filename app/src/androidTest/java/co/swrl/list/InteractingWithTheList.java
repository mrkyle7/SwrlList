package co.swrl.list;

import android.app.Activity;
import android.support.test.espresso.contrib.DrawerActions;
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
import co.swrl.list.item.Type;
import co.swrl.list.ui.activity.ListActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.BILLIONS;
import static co.swrl.list.Helpers.GARDEN_STATE_RECOMMENDATION;
import static co.swrl.list.Helpers.HUNGER_GAMES_BOOK;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static co.swrl.list.Helpers.atPosition;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.doesNotExistAtPosition;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.launchAndWakeUpActivity;
import static co.swrl.list.Helpers.numberOfChildren;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.Helpers.restartActivity;
import static co.swrl.list.Helpers.setSavedVersionToHugeNumber;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class InteractingWithTheList {
    private Activity activity = null;

    @Rule
    public ActivityTestRule listActivityActivityTestRule = new ActivityTestRule<>(ListActivity.class, false, false);

    @Before
    @After
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

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.show_whats_new)).perform(click());
        onView(withId(R.id.whatsNew)).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(R.id.dismissWhatsNewButton)).perform(click());
    }

    @Test
    public void whatsNewDialogIsNotShownWhenNotNewVersion() throws Exception {
        setSavedVersionToHugeNumber();
        activity = launchAndWakeUpActivity(listActivityActivityTestRule);

        onView(withId(R.id.activity_list)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void canDeleteAndReAddItemsOnTheList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, null);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), swipeRight()));

        onView(withId(R.id.listView)).check(matches(not(atPosition(1, hasDescendant(withText("The Matrix"))))));
        onView(withId(R.id.listView)).check(matches(doesNotExistAtPosition(1)));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(1))));
        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));

        activity = restartActivity(activity, listActivityActivityTestRule);

        onView(withId(R.id.listView)).check(matches(not(atPosition(1, hasDescendant(withText("The Matrix"))))));
        onView(withId(R.id.listView)).check(matches(doesNotExistAtPosition(1)));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(1))));
        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_film)).perform(click());

        onView(withId(R.id.addSwrlText))
                .perform(typeText("The Matrix"));
        onView(withId(R.id.add_swrl_button)).perform(click());


        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(2))));
    }


    @Test
    public void markingItemsAsDonePutsThemOnDoneList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED, THE_MATRIX_REVOLUTIONS}, null);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Revolutions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), swipeRight()));

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Reloaded")), swipeRight()));

        onView(withId(R.id.snackbar_text)).check(matches(withText("\"The Matrix Reloaded\" marked as done")));
        onView(withId(R.id.snackbar_action)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Revolutions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(2))));

        onView(withId(R.id.done_swrls)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(1))));
    }

    @Test
    public void canUndoMarkingItemsAsDone() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED, THE_MATRIX_REVOLUTIONS}, null);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Revolutions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Reloaded")), swipeRight()));

        onView(withId(R.id.snackbar_text)).check(matches(withText("\"The Matrix Reloaded\" marked as done")));
        onView(withId(R.id.snackbar_action)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Revolutions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(3))));

        activity = restartActivity(activity, listActivityActivityTestRule);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Revolutions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(3))));
    }

    @Test
    public void canFilterTheList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED, BILLIONS},
                new Swrl[]{GARDEN_STATE_RECOMMENDATION, HUNGER_GAMES_BOOK});

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Billions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(3))));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("Books (0)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Films (2)")).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(2))));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("TV Shows (1)")).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Billions")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(1))));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("All Swrls (3)")).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Billions")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(2, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(3))));

        onView(withId(R.id.done_swrls)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Hunger Games")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("Garden State")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(2))));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("Books (1)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Podcasts (0)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Films (1)")).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Garden State")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(1))));

        onView(withId(R.id.active_swrls)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(2))));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("TV Shows (1)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Films (2)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Books (0)")).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), swipeRight()));
        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Reloaded")), swipeRight()));

        onView(withId(R.id.snackbar_text)).check(matches(withText("\"The Matrix Reloaded\" marked as done")));
        onView(withId(R.id.snackbar_action)).perform(click());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("TV Shows (1)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Films (1)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Books (0)")).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        onView(withId(R.id.done_swrls)).perform(click());

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        onView(withText("Books (1)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Podcasts (0)")).check(matches(isCompletelyDisplayed()));
        onView(withText("Films (2)")).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("Garden State")))));
        onView(withId(R.id.listView)).check(matches(atPosition(1, hasDescendant(withText("The Matrix")))));
        onView(withId(R.id.listView)).check(matches(numberOfChildren(is(2))));

    }

    @Test
    public void refreshAllActionIsAvailable() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED, BILLIONS}, null);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.refresh)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void listRemembersLastPositionWhenPressingBack() throws Exception {
        Swrl[] swrls = new Swrl[21];
        swrls[0] = THE_MATRIX;
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        for (int x = 1; x <= 20; x++){
            swrls[x] = new Swrl(String.valueOf(x), Type.BOARD_GAME);
        }
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                swrls, null);

        onView(withId(R.id.listView)).perform(RecyclerViewActions.scrollToPosition(20));
        onView(withId(R.id.listView)).check(matches(atPosition(20, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), click()));
        onView(allOf(withText("The Matrix"), withId(R.id.title))).check(matches(isCompletelyDisplayed()));

        pressBack();

        onView(withId(R.id.listView)).check(matches(atPosition(20, hasDescendant(withText("The Matrix")))));
    }
}


