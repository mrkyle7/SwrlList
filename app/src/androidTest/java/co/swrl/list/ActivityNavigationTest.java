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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.ui.activity.AddSwrlActivity;
import co.swrl.list.ui.activity.ListActivity;
import co.swrl.list.ui.activity.LoginActivity;
import co.swrl.list.ui.activity.RecommendationCreationActivity;
import co.swrl.list.ui.activity.ViewActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static co.swrl.list.Helpers.atPosition;
import static co.swrl.list.Helpers.clearAllSettings;
import static co.swrl.list.Helpers.launchAndAvoidWhatsNewDialog;
import static co.swrl.list.Helpers.purgeDatabase;
import static co.swrl.list.ui.activity.ViewActivity.ViewType.VIEW;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
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
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, null, false);

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), click()));

        intended(allOf(
                hasComponent(ViewActivity.class.getName()),
                hasExtras(allOf(
                        hasEntry(equalTo("swrls"), contains(THE_MATRIX_RELOADED, THE_MATRIX)),
                        hasEntry(equalTo("index"), equalTo(1)),
                        hasEntry(equalTo("type"), equalTo(VIEW))
                ))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("The Matrix"))));

        pressBack();

        onView(withId(R.id.listView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Reloaded")), click()));

        intended(allOf(
                hasComponent(ViewActivity.class.getName()),
                hasExtras(allOf(
                        hasEntry(equalTo("swrls"), contains(THE_MATRIX_RELOADED, THE_MATRIX)),
                        hasEntry(equalTo("index"), equalTo(0)),
                        hasEntry(equalTo("type"), equalTo(VIEW))))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("The Matrix Reloaded"))));

    }

    @Test
    public void canOpenViewWhenOver3000SwrlsOnList() throws Exception {
        Swrl[] swrls = new Swrl[3001];
        swrls[0] = THE_MATRIX;
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        for (int x = 1; x <= 3000; x++){
            swrls[x] = new Swrl(String.valueOf(x), Type.BOARD_GAME);
        }
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents,
                swrls, null, false);

        onView(withId(R.id.listView)).perform(RecyclerViewActions.scrollToPosition(3000));
        onView(withId(R.id.listView)).check(matches(atPosition(3000, hasDescendant(withText("The Matrix")))));

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), click()));
        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("The Matrix"))));
    }

    @Test
    public void canNavigateBetweenListAndRecommendScreen() throws Exception {
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        THE_MATRIX_RELOADED.setDetails(THE_MATRIX_RELOADED_DETAILS);
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, new Swrl[]{THE_MATRIX_REVOLUTIONS}, true);

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix")), swipeRight()));

        intended(allOf(
                hasComponent(RecommendationCreationActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrl"), equalTo(THE_MATRIX)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Recommend The Matrix to friends"))));

        pressBack();

        onView(withId(R.id.listView))
                .perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Reloaded")), swipeRight()));

        intended(allOf(
                hasComponent(RecommendationCreationActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrl"), equalTo(THE_MATRIX_RELOADED)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Recommend The Matrix Reloaded to friends"))));

        pressBack();

        onView(withId(R.id.done_swrls)).perform(click());

        onView(withId(R.id.listView)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText("The Matrix Revolutions")), swipeRight()));

        intended(allOf(
                hasComponent(RecommendationCreationActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrl"), equalTo(THE_MATRIX_REVOLUTIONS)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Recommend The Matrix Revolutions to friends"))));
    }

    @Test
    public void canNavigateBetweenListAndAddSwrlScreen() throws Exception {
        THE_MATRIX.setDetails(THE_MATRIX_DETAILS);
        THE_MATRIX_RELOADED.setDetails(THE_MATRIX_RELOADED_DETAILS);
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, null, false);

        onView(withId(R.id.listView))
                .check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));

        onView(withId(R.id.fab_expand_menu_button)).perform(click());

        onView(withId(R.id.add_film)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrlType"), equalTo(Type.FILM)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Film"))));

        pressBack();
        pressBack();

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_board_game)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrlType"), equalTo(Type.BOARD_GAME)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Board Game"))));

        pressBack();
        pressBack();


        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.show_others)).perform(click());
        onView(withId(R.id.add_podcast)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrlType"), equalTo(Type.PODCAST)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Podcast"))));

        pressBack();
        pressBack();

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_video_game)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrlType"), equalTo(Type.VIDEO_GAME)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Video Game"))));

        pressBack();
        pressBack();

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.show_others)).perform(click());
        onView(withId(R.id.add_book)).perform(click());

        intended(allOf(
                hasComponent(AddSwrlActivity.class.getName()),
                hasExtras(hasEntry(equalTo("swrlType"), equalTo(Type.BOOK)))));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Book"))));

        pressBack();
        pressBack();

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));
    }

    @Test
    public void pressingCancelOnAddNewSwrlScreenTakesYouBackToList() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED}, null, false);

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.add_film)).perform(click());

        intended(hasComponent(AddSwrlActivity.class.getName()));

        onView(isAssignableFrom(Toolbar.class)).check(matches(withToolbarTitle(is("Add a Film"))));

        onView(withId(R.id.cancel_add)).perform(click());

        onView(withId(R.id.listView)).check(matches(atPosition(0, hasDescendant(withText("The Matrix Reloaded")))));

    }

    @Test
    public void canNavigateToTheLoginScreen() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents, null, null, false);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.login_menu_link)).perform(click());

        intended(hasComponent(LoginActivity.class.getName()));

        onView(withId(R.id.login_username)).check(matches(isCompletelyDisplayed()));
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
