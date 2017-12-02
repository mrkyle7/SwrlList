package co.swrl.list.item;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SwrlTest {
    @Test
    public void testEqualsOverride() throws Exception {
        Swrl swrl = new Swrl("Swrl");
        Swrl sameSwrl = new Swrl("Swrl");
        Swrl unknownSwrl = new Swrl("Swrl", Type.UNKNOWN);
        Swrl albumSwrl = new Swrl("Swrl", Type.ALBUM);
        Swrl swrlRecommendation = new Swrl("Swrl", Type.UNKNOWN, "review", "author", 1, null, 1);

        assertTrue(swrl.equals(swrl));
        assertTrue(swrl.equals(sameSwrl));
        assertTrue(sameSwrl.equals(swrl));
        assertTrue(swrl.equals(unknownSwrl)); //because default is type UNKNOWN
        assertTrue(swrl.equals(swrlRecommendation)); //because default is type UNKNOWN
        assertTrue(unknownSwrl.equals(swrl));
        assertFalse(swrl.equals(albumSwrl));
        assertFalse(albumSwrl.equals(swrlRecommendation));
        assertFalse(unknownSwrl.equals(albumSwrl));

        sameSwrl.setDetails(new Gson().fromJson("{\"title\":\"The Matrix (1991)\",\"overview\":\"overview\"}", Details.class));
        assertTrue(swrl.equals(sameSwrl));
    }

    @Test
    public void canParseRecommendationIntoASwrl() throws Exception {
        String bookJson = "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years the Galactic Empire has ruled supreme. Now it is dying. But only Hari Sheldon, creator of the revolutionary science of psychohistory, can see into the future--to a dark age of ignorance, barbarism, and warfare that will last thirty thousand years. To preserve knowledge and save mankind, Seldon gathers the best minds in the Empire--both scientists and scholars--and brings them to a bleak planet at the edge of the Galaxy to serve as a beacon of hope for a fututre generations. He calls his sanctuary the Foundation.<br><br>But soon the fledgling Foundation finds itself at the mercy of corrupt warlords rising in the wake of the receding Empire. Mankind's last best hope is faced with an agonizing choice: submit to the barbarians and be overrun--or fight them and be destroyed.<I>Foundation</I> marks the first of a series of tales set so far in the future that Earth is all but forgotten by humans who live throughout the galaxy. Yet all is not well with the Galactic Empire. Its vast size is crippling to it. In particular, the administrative planet, honeycombed and tunneled with offices and staff, is vulnerable to attack or breakdown. The only person willing to confront this imminent catastrophe is Hari Seldon, a psychohistorian and mathematician. Seldon can scientifically predict the future, and it doesn't look pretty: a new Dark Age is scheduled to send humanity into barbarism in 500 years. He concocts a scheme to save the knowledge of the race in an Encyclopedia Galactica. But this project will take generations to complete, and who will take up the torch after him? The first Foundation trilogy (<I>Foundation</I>, <I>Foundation and Empire</I>, <I>Second Foundation</I>) won a Hugo Award in 1965 for \\\"Best All-Time Series.\\\" It's science fiction on the grand scale; one of the classics of the field. <I>--Brooks Peck</I>\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293354\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"0553293354\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}";

        Swrl book = new Gson().fromJson(bookJson, Swrl.class);

        assertEquals("Foundation by Isaac Asimov", book.getTitle());
        assertEquals(Type.BOOK, book.getType());
        assertEquals("Isaac Asimov", book.getDetails().getCreator());
        assertEquals("A true classic, and rightly so.", book.getReview());
        assertEquals("Kyle Harrison", book.getAuthor());
    }

    @Test
    public void canParseTheKnownTypes() throws Exception {
        checkIsType(Type.FILM, "movie");
        checkIsType(Type.TV, "tv");
        checkIsType(Type.BOOK, "book");
        checkIsType(Type.ALBUM, "album");
        checkIsType(Type.VIDEO_GAME, "game");
        checkIsType(Type.BOARD_GAME, "boardgame");
        checkIsType(Type.APP, "app");
        checkIsType(Type.PODCAST, "podcast");
        checkIsType(Type.WEBSITE, "website");
        checkIsType(Type.VIDEO, "video");
        checkIsType(Type.UNKNOWN, "blah");
    }

    @Test
    public void canParseUnknownFields() throws Exception {
        String json = "{\"title\":\"A title\",\"unknownField\":\"blah\"}";
        Swrl swrl = new Gson().fromJson(json, Swrl.class);
        assertEquals("A title", swrl.getTitle());
    }

    private void checkIsType(Type expectedType, String typeInJson) {
        String json = String.format("{\"title\":\"A title\",\"type\":\"%s\"}", typeInJson);
        Swrl swrl = new Gson().fromJson(json, Swrl.class);
        assertEquals(expectedType, swrl.getType());
    }
}