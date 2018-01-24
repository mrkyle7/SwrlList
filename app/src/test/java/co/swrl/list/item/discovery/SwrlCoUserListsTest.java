package co.swrl.list.item.discovery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import co.swrl.list.utils.SwrlPreferences;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SwrlCoUserListsTest {

    MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
    }

    @After
    public void tearDown() throws Exception {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    public void canGetLists() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody(
                        "{\"active\":" +
                                "[" +
                                "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years...\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293354\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"0553293354\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}," +
                                "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation2 by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years...\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293355\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"123\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}" +
                                "]," +
                           "\"done\":" +
                                "[" +
                                "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation3 by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years...\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293356\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"123\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}" +
                                "]" +
                                "}"));
        mockWebServer.start();

        HttpUrl mockUrl = mockWebServer.url("/");

        SwrlPreferences mockPreferences = mock(SwrlPreferences.class);
        when(mockPreferences.getUserID()).thenReturn(123);
        when(mockPreferences.getAuthToken()).thenReturn("222ddd");

        SwrlCoUserLists swrlCoLists = new SwrlCoUserLists(mockUrl, mockPreferences);

        SwrlCoUserLists.UserLists results = swrlCoLists.get();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/?auth_token=222ddd&user_id=123", request.getPath());

        assertEquals(2, results.active.size());
        assertEquals(1, results.done.size());
        assertNull(results.dismissed);
        assertNull(results.swrled);
        assertEquals("Foundation by Isaac Asimov", results.active.get(0).getTitle());
        assertEquals("Foundation2 by Isaac Asimov", results.active.get(1).getTitle());
        assertEquals("Foundation3 by Isaac Asimov", results.done.get(0).getTitle());
    }

}