package co.swrl.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.gson.Gson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.utils.SwrlPreferences;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Checks.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class Helpers {
    private Helpers() {
    }

    static final Swrl THE_MATRIX = new Swrl("The Matrix", Type.FILM);
    static final Swrl GARDEN_STATE_RECOMMENDATION = new Swrl("Garden State", Type.FILM, "A review", "Bobby Joe", 10, "https://avatar.com", 7);
    static final Swrl THE_MATRIX_RELOADED = new Swrl("The Matrix Reloaded", Type.FILM);
    static final Swrl THE_MATRIX_REVOLUTIONS = new Swrl("The Matrix Revolutions", Type.FILM);
    static final Swrl BILLIONS = new Swrl("Billions", Type.TV);
    static final Swrl HUNGER_GAMES_BOOK = new Swrl("Hunger Games", Type.BOOK);
    public static final Swrl BLACK_MIRROR_TV = new Swrl("Black Mirror", Type.TV);
    static final Details THE_MATRIX_DETAILS = new Gson().fromJson("{\"title\":\"The Matrix (1991)\",\"overview\":\"an overview for the matrix\",\"tmdb-id\":\"403\"}", Details.class);
    static final Details THE_MATRIX_RELOADED_DETAILS = new Gson().fromJson("{\"title\":\"The Matrix Reloaded (1992)\",\"overview\":\"an overview for the matrix reloaded\",\"tmdb-id\":\"404\"}", Details.class);
    static final Details THE_MATRIX_REVOLUTIONS_DETAILS = new Gson().fromJson("{\"title\":\"The Matrix Revolutions (1992)\",\"overview\":\"an overview for the matrix revolutions\",\"tmdb-id\":\"405\"}", Details.class);
    public static final Details BLACK_MIRROR_DETAILS = new Gson().fromJson(
            "{\"large-image-url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                    "\"creator\":\"Charlie Brooker, Another Creator\"," +
                    "\"genres\":[\"Drama\",\"Sci-Fi & Fantasy\"]," +
                    "\"tmdb-id\":42009," +
                    "\"thumbnail-url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                    "\"overview\":\"Black Mirror is a British television drama series created by Charlie Brooker\"," +
                    "\"title\":\"Black Mirror\"," +
                    "\"runtime\":60," +
                    "\"url\":\"http://www.channel4.com/programmes/black-mirror/\"}", Details.class);

    static void clearAllSettings() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        settings.edit().clear().apply();
    }

    static Activity launchAndAvoidWhatsNewDialog(ActivityTestRule testRule, Swrl[] swrls, Swrl[] doneSwrls, boolean fakeLogIn) {
        setSavedVersionToHugeNumber();
        if (fakeLogIn) setFakeLoginCredentials();
        SQLiteCollectionManager db = new SQLiteCollectionManager(InstrumentationRegistry.getTargetContext());
        if (swrls != null) {
            for (Swrl swrl : swrls) {
                db.save(swrl);
            }
        }
        if (doneSwrls != null) {
            for (Swrl swrl : doneSwrls) {
                db.save(swrl);
                db.markAsDone(swrl);
            }
        }
        return launchAndWakeUpActivity(testRule);
    }

    private static void setFakeLoginCredentials() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        settings.edit().putString(SwrlPreferences.KEY_AUTH_TOKEN, "auth123").apply();
        settings.edit().putInt(SwrlPreferences.KEY_USER_ID, 123).apply();
    }

    static void setSavedVersionToHugeNumber() {
        Context applicationContext = InstrumentationRegistry.getTargetContext();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(applicationContext);

        int currentVersion = Integer.MAX_VALUE;
        settings.edit().putInt(SwrlPreferences.KEY_VERSION_NUMBER, currentVersion).apply();
    }

    static Activity restartActivity(Activity activity, ActivityTestRule testRule) {
        stopActivity(activity);
        return launchAndWakeUpActivity(testRule);
    }

    static Activity launchAndWakeUpActivity(ActivityTestRule testRule) {
        return launchAndWakeUpActivity(testRule, null);
    }

    static Activity launchAndWakeUpActivity(ActivityTestRule testRule, Intent intent) {
        testRule.launchActivity(intent);
        Activity activity = testRule.getActivity();
        wakeUpDevice(activity);
        return activity;
    }

    private static void wakeUpDevice(final Activity activity) {
        Runnable wakeUpDevice = new Runnable() {
            public void run() {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        };
        activity.runOnUiThread(wakeUpDevice);
    }

    private static void stopActivity(Activity activity) {
        activity.finish();
    }

    static void addSwrlsToList(Swrl[] swrls) {
        for (Swrl swrl : swrls) {
            onView(withId(R.id.addItemEditText)).perform(typeText(swrl.getTitle()));
            onView(withId(R.id.addItemButton)).perform(click());
            onView(withText(swrl.getType().toString())).perform(click());
            onData(allOf(is(instanceOf(Swrl.class)), equalTo(swrl))).check(matches(isDisplayed()));
        }
    }

    static void purgeDatabase() {
        SQLiteCollectionManager db = new SQLiteCollectionManager(InstrumentationRegistry.getTargetContext());
        db.permanentlyDeleteAll();
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }

    public static Matcher<View> doesNotExistAtPosition(final int position) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {

            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                return viewHolder == null;
            }
        };
    }

    static Matcher<View> numberOfChildren(final Matcher<Integer> numChildrenMatcher) {
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
