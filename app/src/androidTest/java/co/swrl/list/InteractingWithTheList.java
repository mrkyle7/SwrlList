package co.swrl.list;

import android.app.Activity;
import android.os.SystemClock;
import android.support.test.espresso.action.EspressoKey;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
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

import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.ui.ListActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static co.swrl.list.Helpers.addSwrlsToList;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.launchAndWakeUpActivity;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.Helpers.restartActivity;
import static co.swrl.list.Helpers.setSavedVersionToHugeNumber;
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
    public void newItemsInTheListAreAddedOnTopAndTextInputIsClearedAndFocused() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        //With IME Action Button
        onView(withId(R.id.addItemEditText))
                .perform(typeText("First Item"))
                .perform(pressImeActionButton());
        onView(withText(Type.FILM.toString())).perform(click());

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("First Item"))));
        onView(withId(R.id.addItemEditText)).check(matches(hasFocus()));
        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));

        //With Enter key
        onView(withId(R.id.addItemEditText))
                .perform(typeText("Second Item"))
                .perform(pressKey(ENTER_KEY));
        onView(withText(Type.FILM.toString())).perform(click());

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("Second Item"))));
        onView(withId(R.id.addItemEditText)).check(matches(hasFocus()));
        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));

        //With Add Button
        onView(withId(R.id.addItemEditText)).perform(typeText("Third Item"));
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.FILM.toString())).perform(click());

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("Third Item"))));
        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(1)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("Second Item"))));
        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(2)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("First Item"))));
        onView(withId(R.id.addItemEditText)).check(matches(hasFocus()));
        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));
    }

    @Test
    public void canAddAnItemWithAType() throws Exception{
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        Swrl ITEM = new Swrl("My Item", Type.FILM);

        onView(withId(R.id.addItemEditText)).perform(typeText("My Item"));
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.FILM.toString())).perform(click());

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(ITEM))).check(matches(isDisplayed()));
    }

    @Test
    public void cannotAddDuplicateItems() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        addSwrlsToList(new Swrl[]{THE_MATRIX});

        onView(withId(R.id.addItemEditText)).perform(typeText("The Matrix"));
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.FILM.toString())).perform(click());

        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(1))));
    }

    @Test
    public void emptyTextIsNotAddedToTheList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        Swrl emptySwrl = new Swrl("");

        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));

        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.UNKNOWN.toString())).perform(click());
        onView(withId(R.id.addItemEditText)).perform(pressImeActionButton());
        onView(withText(Type.FILM.toString())).perform(click());

        onView(withId(R.id.itemListView)).check(matches(not(exists(equalTo(emptySwrl)))));
    }


    @Test
    public void itemsOnTheListArePersistedAfterRestart() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        addSwrlsToList(new Swrl[]{THE_MATRIX});

        activity = restartActivity(activity, listActivityActivityTestRule);

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));
    }

    @Test
    public void canDeleteAndReAddItemsOnTheList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        addSwrlsToList(new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX)))
                .onChildView(withId(R.id.list_item_done))
                .perform(click());

        onView(withId(R.id.itemListView)).check(matches(not(exists(equalTo(THE_MATRIX)))));
        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(1))));
        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX_RELOADED))).check(matches(isDisplayed()));

        activity = restartActivity(activity, listActivityActivityTestRule);

        onView(withId(R.id.itemListView)).check(matches(not(exists(equalTo(THE_MATRIX)))));
        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX_RELOADED))).check(matches(isDisplayed()));
        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(1))));

        addSwrlsToList(new Swrl[]{THE_MATRIX});

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));
        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX_RELOADED))).check(matches(isDisplayed()));
        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(2))));

        activity = restartActivity(activity, listActivityActivityTestRule);

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));
        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX_RELOADED))).check(matches(isDisplayed()));
        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(2))));
    }

    @Test
    public void canUndoMarkingItemsAsDone() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule);

        addSwrlsToList(new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED, THE_MATRIX_REVOLUTIONS});

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



    private static Matcher<View> exists(final Matcher<Swrl> dataMatcher) {
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


