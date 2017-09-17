package co.swrl.list;

import android.app.Activity;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.ui.activity.AddSwrlActivity;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.activity.ViewActivity;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED_DETAILS;
import static co.swrl.list.Helpers.atPosition;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.purgeDatabase;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityNavigationTest {

    private Activity activity;

    @Rule
    public IntentsTestRule<ListActivity> listActivityIntents = new IntentsTestRule<>(ListActivity.class, false, false);

    @Before
    @After
    public void setupAndTearDown() {
        clearAllSettings();
        purgeDatabase();
    }

    @Test
    public void canNavigateBetweenListAndView() throws Exception {
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        THE_MATRIX_RELOADED.setDetails(THE_MATRIX_RELOADED_DETAILS);
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onView(withId(R.id.listView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), click()));

        intended(allOf(
                hasComponent(ViewActivity.class.getName()),
                hasExtras(allOf(
                        hasEntry(equalTo("swrls"), contains(THE_MATRIX_RELOADED, THE_MATRIX)),
                        hasEntry(equalTo("index"), equalTo(1))
                ))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("The Matrix"))));

        pressBack();

        onView(withId(R.id.listView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Reloaded")), click()));

        intended(allOf(
                hasComponent(ViewActivity.class.getName()),
                hasExtras(allOf(
                        hasEntry(equalTo("swrls"), contains(THE_MATRIX_RELOADED, THE_MATRIX)),
                        hasEntry(equalTo("index"), equalTo(0))))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("The Matrix Reloaded"))));

    }

    @Test //TODO: fix test
    public void canNavigateBetweenListAndAddSwrlScreen() throws Exception {
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        THE_MATRIX_RELOADED.setDetails(THE_MATRIX_RELOADED_DETAILS);
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onView(withId(R.id.listView))
                .check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));

        onView(withId(R.id.addItemFAB)).perform(click());

        onView(withId(R.id.add_film)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("type"), equalTo(Type.FILM)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Film"))));

        pressBack();

        onView(withId(R.id.addItemFAB)).perform(click());
        onView(withId(R.id.add_board_game)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("type"), equalTo(Type.BOARD_GAME)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Board Game"))));

        pressBack();

        onView(withId(R.id.addItemFAB)).perform(click());
        onView(withId(R.id.add_tv)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("type"), equalTo(Type.TV)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a TV Show"))));

        pressBack();

        onView(withId(R.id.addItemFAB)).perform(click());
        onView(withId(R.id.add_book)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("type"), equalTo(Type.BOOK)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Book"))));

        pressBack();
        onView(withId(R.id.addItemFAB)).perform(click());
        onView(withId(R.id.add_album)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("type"), equalTo(Type.ALBUM)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Music Album"))));

        pressBack();
        onView(withId(R.id.addItemFAB)).perform(click());
        onView(withId(R.id.add_unknown)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("type"), equalTo(Type.UNKNOWN)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Swrl"))));

        pressBack();

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));
    }

    @Test @Ignore //TODO: fix test
    public void pressingCancelOnAddNewSwrlScreenTakesYouBackToList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));

        onView(withId(R.id.addItemFAB)).perform(click());
        onView(withId(R.id.add_film)).perform(click());

        intended(hasComponent(AddSwrlActivity.class.getName()));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Film"))));

        onView(withId(R.id.cancel_add)).perform(click());

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX))).check(matches(isDisplayed()));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<String> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

}
