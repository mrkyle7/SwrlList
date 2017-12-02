package co.swrl.list.item.actions;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.swrl.list.SwrlPreferences;
import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static co.swrl.list.Helpers.BLACK_MIRROR_DETAILS;
import static co.swrl.list.Helpers.BLACK_MIRROR_TV;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActionsTest {

    private MockWebServer mockWebServer;
    private SQLiteCollectionManager db;
    private SwrlPreferences preferences = new SwrlPreferences(InstrumentationRegistry.getTargetContext());


    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        db = new SQLiteCollectionManager(InstrumentationRegistry.getTargetContext());
        db.permanentlyDeleteAll();
        preferences.saveAuthToken("authtoken123");
        preferences.saveUserID(123);
    }

    @After
    public void tearDown() throws Exception {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
        db.permanentlyDeleteAll();
        preferences.saveAuthToken(null);
        preferences.saveUserID(0);
    }

    @Test
    public void canCreateASwrl() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"is_private\":false," +
                        "\"author_id\":377," +
                        "\"thumbnail_url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                        "\"username\":\"testauth2\"," +
                        "\"type\":\"tv\"," +
                        "\"itunes-collection-id\":null," +
                        "\"title\":\"Black Mirror\"," +
                        "\"creation_date\":\"2017-12-01 15:56:38.532242\"," +
                        "\"details\":" +
                            "{\"large-image-url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                            "\"creator\":\"Charlie Brooker, Another Creator\"," +
                            "\"genres\":[\"Drama\",\"Sci-Fi & Fantasy\"]," +
                            "\"tmdb-id\":42009," +
                            "\"thumbnail-url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                            "\"overview\":\"Black Mirror is a British television drama series created by Charlie Brooker\"," +
                            "\"title\":\"Black Mirror\"," +
                            "\"runtime\":60," +
                            "\"url\":\"http://www.channel4.com/programmes/black-mirror/\"}," +
                        "\"review\":\"\"," +
                        "\"external_id\":\"42009\"," +
                        "\"id\":663," +
                        "\"email_md5\":\"cdbbc5ae343d68b0ad81dccb2445dbcb\"}"));
        mockWebServer.start();

        HttpUrl mockUrl = mockWebServer.url("/");

        BLACK_MIRROR_TV.setDetails(BLACK_MIRROR_DETAILS);
        db.save(BLACK_MIRROR_TV);

        Swrl beforeCreateCalled = db.getActive().get(0);

        assertEquals(0, beforeCreateCalled.getId());

        SwrlCoActions.create(mockUrl, BLACK_MIRROR_TV, "Later", preferences, db);

        Swrl afterCreateCalled = db.getActive().get(0);

        assertEquals(663, afterCreateCalled.getId());

        RecordedRequest request = mockWebServer.takeRequest();

        assertEquals("{\"auth_token\":\"authtoken123\"," +
                "\"details\":{\"genres\":[\"Drama\",\"Sci-Fi \\u0026 Fantasy\"]," +
                "\"creator\":\"Charlie Brooker, Another Creator\"," +
                "\"id\":\"42009\"," +
                "\"overview\":\"Black Mirror is a British television drama series created by Charlie Brooker\"," +
                "\"large-image-url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                "\"runtime\":\"60\"," +
                "\"title\":\"Black Mirror\"," +
                "\"website-url\":\"http://www.channel4.com/programmes/black-mirror/\"}," +
                "\"external-id\":\"42009\"," +
                "\"image-url\":\"https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg\"," +
                "\"private\":false," +
                "\"quick-response\":\"Later\"," +
                "\"title\":\"Black Mirror\"," +
                "\"type\":\"tv\"," +
                "\"user_id\":\"123\"}", request.getBody().readUtf8());
    }
}
