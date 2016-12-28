package co.swrl.list;

import android.app.Activity;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.addSwrlsToList;
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

    @Before @After
    public void setupAndTearDown() {
        clearAllSettings();
        purgeDatabase();
    }

    @Test
    public void canNavigateBetweenListAndView() throws Exception {
        activity = launchAndAvoidWhatsNewDialog(listActivityIntents);
        addSwrlsToList(new Swrl[]{THE_MATRIX, THE_MATRIX_RELOADED});

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX)))
                .onChildView(withId(R.id.list_title))
                .perform(click());

        intended(allOf(
                hasComponent(ViewActivity.class.getName()),
                hasExtras(allOf(
                        hasEntry(equalTo("swrls"), contains(THE_MATRIX_RELOADED, THE_MATRIX)),
                        hasEntry(equalTo("index"), equalTo(1))
                ))));

        pressBack();

        onData(allOf(is(instanceOf(Swrl.class)), equalTo(THE_MATRIX_RELOADED)))
                .onChildView(withId(R.id.list_title))
                .perform(click());

        intended(allOf(
                hasComponent(ViewActivity.class.getName()),
                hasExtras(allOf(
                        hasEntry(equalTo("swrls"), contains(THE_MATRIX_RELOADED, THE_MATRIX)),
                        hasEntry(equalTo("index"), equalTo(0))))));
    }

}
