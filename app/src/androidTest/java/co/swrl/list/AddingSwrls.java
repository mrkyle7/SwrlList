package co.swrl.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.ui.AddSwrlActivity;
import co.swrl.list.ui.ListActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.addSwrlsToList;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.Helpers.restartActivity;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddingSwrls {
    private Activity activity = null;
    private static final EspressoKey ENTER_KEY = new EspressoKey.Builder().withKeyCode(KeyEvent.KEYCODE_ENTER).build();

    @Rule
    public ActivityTestRule listActivityActivityTestRule = new ActivityTestRule<>(ListActivity.class, false, false);

    @Rule
    public ActivityTestRule<AddSwrlActivity> addSwrlActivityActivityTestRule = new ActivityTestRule<AddSwrlActivity>(AddSwrlActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation()
                    .getTargetContext();
            Intent result = new Intent(targetContext, AddSwrlActivity.class);
            result.putExtra("type", Type.UNKNOWN);
            return result;
        }
    };

    @Before
    @After
    public void setupAndTearDown() {
        clearAllSettings();
        purgeDatabase();
    }

    @Test @Ignore //TODO: fix test
    public void newItemsInTheListAreAddedOnTop() throws Exception {

        activity = launchAndAvoidWhatsNewDialog(addSwrlActivityActivityTestRule, new Swrl[]{THE_MATRIX});

        //With IME Action Button
        onView(withId(R.id.addSwrlText))
                .perform(typeText("Top Item"));
        onView(withId(R.id.add_swrl_button)).perform(click());

        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, null);
        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(0)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("Top Item"))));

        onData(is(instanceOf(Swrl.class)))
                .inAdapterView(withId(R.id.itemListView)).atPosition(1)
                .onChildView(withId(R.id.list_title))
                .check(matches(withText(containsString("The Matrix"))));
    }

    @Test @Ignore //TODO: fix test
    public void canAddAnItemWithAType() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, null);

        Swrl ITEM = new Swrl("My Item", Type.FILM);

        onView(withId(R.id.addItemEditText)).perform(typeText("My Item"));
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.FILM.toString())).perform(click());

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(ITEM))).check(matches(isDisplayed()));
    }

    @Test @Ignore //TODO: fix test
    public void cannotAddDuplicateItems() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX});

        onView(withId(R.id.addItemEditText)).perform(typeText("The Matrix"));
        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.FILM.toString())).perform(click());

        onView(withId(R.id.itemListView)).check(matches(numberOfChildren(is(1))));
    }

    @Test @Ignore //TODO: fix test
    public void emptyTextIsNotAddedToTheList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, null);

        Swrl emptySwrl = new Swrl("");

        onView(withId(R.id.addItemEditText)).check(matches(withText(isEmptyString())));

        onView(withId(R.id.addItemButton)).perform(click());
        onView(withText(Type.FILM.toString())).perform(click());
        onView(withId(R.id.addItemEditText)).perform(pressImeActionButton());
        onView(withText(Type.FILM.toString())).perform(click());

        onView(withId(R.id.itemListView)).check(matches(not(exists(equalTo(emptySwrl)))));
    }


    @Test
    public void itemsOnTheListArePersistedAfterRestart() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule, new Swrl[]{THE_MATRIX});

        activity = restartActivity(activity, listActivityActivityTestRule);

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));
    }

    @Test @Ignore //TODO: fix test
    public void canDeleteAndReAddItemsOnTheList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityActivityTestRule,
                new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX)))
                .onChildView(withId(R.id.list_item_button))
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

