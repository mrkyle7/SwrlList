package co.swrl.list.item;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class DetailsTest extends AndroidTestCase {
    @Test
    public void getNullCategories() throws Exception {
        Details details = getNullDetails();
        assertNull(details.getCategories());
    }

    @Test
    public void getNullCategoriesWhenEmpty() throws Exception {
        ArrayList<String> categories = new ArrayList<>();
        Details details = new Details(null, null, null, null, null, categories, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertNull(details.getCategories());
    }
    @Test
    public void getCategoriesFormatsCorrectly() throws Exception {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("cat1");
        categories.add("cat2");
        Details details = new Details(null, null, null, null, null, categories, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("cat1, cat2", details.getCategories());
    }

    @Test
    public void getCategoriesFormatsCorrectlyWithOne() throws Exception {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("cat1");
        Details details = new Details(null, null, null, null, null, categories, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("cat1", details.getCategories());
    }

    @Test
    public void getCategoriesRemovesAmazonBadFormatting() throws Exception {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("adventure-game-genre");
        categories.add("action-game-genre");
        Details details = new Details(null, null, null, null, null, categories, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("adventure, action", details.getCategories());
    }

    @NonNull
    private Details getNullDetails() {
        return new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    public void getNullTagline() throws Exception {
        Details details = getNullDetails();
        assertNull(details.getTagline());
    }

    @Test
    public void getNullTaglineWhenNone() throws Exception {
        Details details = new Details(null, null, null, null, null, null, "None", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertNull(details.getTagline());
    }
    @Test
    public void getTagline() throws Exception {
        Details details = new Details(null, null, null, null, null, null, "Tagline", null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        assertEquals("Tagline", details.getTagline());
    }

    @Test
    public void getNullRatings() throws Exception {
        Details details = getNullDetails();
        assertNull(details.getRatings());
    }

    @Test
    public void getNullRatingsWhenEmpty() throws Exception {
        ArrayList<Details.Ratings> ratings = new ArrayList<>();
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ratings, null, null, null, null, null);
        assertNull(details.getRatings());
    }

    @Test
    public void getRatings() throws Exception {
        ArrayList<Details.Ratings> ratings = new ArrayList<>();
        Details.Ratings imdb = new Details.Ratings("Internet Movie Database", "9.1");
        Details.Ratings rotten = new Details.Ratings("Rotten", "90%");

        ratings.add(imdb);
        ratings.add(rotten);

        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ratings, null, null, null, null, null);
        assertEquals("IMDB: 9.1, Rotten: 90%", details.getRatings());
    }

    @Test
    public void getNullRuntime() throws Exception {
        Details details = getNullDetails();
        assertNull(details.getRuntime());
    }
    @Test
    public void getRuntimeWhenNA() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "N/A", null, null, null, null, null, null);
        assertNull(details.getRuntime());
    }
    @Test
    public void getRuntime() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "90 min", null, null, null, null, null, null);
        assertEquals("90 min", details.getRuntime());
    }
    @Test
    public void getRuntimeJustNumber() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, "90", null, null, null, null, null, null);
        assertEquals("90 min", details.getRuntime());
    }

    @Test
    public void getNullMinToMaxPlayers() throws Exception {
        Details details = getNullDetails();
        assertNull(details.getMinToMaxPlayers());
    }
    @Test
    public void getMinToMaxPlayersMaxNull() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", null, null, null, null);
        assertEquals("1", details.getMinToMaxPlayers());
    }

    @Test
    public void getMinToMaxPlayersMinNull() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", null, null, null);
        assertEquals("1", details.getMinToMaxPlayers());
    }

    @Test
    public void getMinToMaxPlayersMaxSameAsMin() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", "1", null, null, null);
        assertEquals("1", details.getMinToMaxPlayers());
    }

    @Test
    public void getMinToMaxPlayers() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", "4", null, null, null);
        assertEquals("1 - 4", details.getMinToMaxPlayers());
    }

    @Test
    public void getNullMinToMaxPlaytime() throws Exception {
        Details details = getNullDetails();
        assertNull(details.getMinToMaxPlaytime());
    }
    @Test
    public void getMinToMaxPlaytimeMaxNull() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", null, null);
        assertEquals("1 min", details.getMinToMaxPlaytime());
    }

    @Test
    public void getMinToMaxPlaytimeMinNull() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", null);
        assertEquals("1 min", details.getMinToMaxPlaytime());
    }

    @Test
    public void getMinToMaxPlaytimeMaxSameAsMin() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,  null, null, "1", "1", null);
        assertEquals("1 min", details.getMinToMaxPlaytime());
    }

    @Test
    public void getMinToMaxPlaytime() throws Exception {
        Details details = new Details(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "1", "4", null);
        assertEquals("1 - 4 min", details.getMinToMaxPlaytime());
    }

}