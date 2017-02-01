package co.swrl.list;

import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.MalformedURLException;
import java.net.URL;

import co.swrl.list.item.details.FilmDetails;
import co.swrl.list.item.details.FilmDetailsBuilder;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FilmDetailsTest {
    private static final URL THE_MATRIX_POSTER_URL = makeUrl("http://www.posters.com/the_matrix.jpg");

    @Nullable
    private static URL makeUrl(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    @Test
    public void testDetailsAreTheSame() throws Exception {
        assert THE_MATRIX_POSTER_URL != null;
        FilmDetails details = new FilmDetails("The Matrix (1991)", "Overview", "603", THE_MATRIX_POSTER_URL);
        FilmDetails details2 = new FilmDetails("The Matrix (1991)", "Overview", "603", THE_MATRIX_POSTER_URL);
        URL differentPoster = new URL("http://something.different.com");
        FilmDetails details3 = new FilmDetails("The Matrix (1991)", "Overview", "603", differentPoster);


        assertTrue(details.equals(details));
        assertTrue(details.equals(details2));
        assertFalse(details.equals(details3));
    }


    @Test
    public void canConvertToAndFromJSON() throws Exception {
        assert THE_MATRIX_POSTER_URL != null;
        FilmDetails original = new FilmDetails("The Matrix (1991)", "Overview", "603", THE_MATRIX_POSTER_URL);

        JSONObject json = original.toJSON();

        FilmDetails fromJSON = new FilmDetailsBuilder().fromJSON(json);

        assertTrue(original.equals(fromJSON));
    }
}