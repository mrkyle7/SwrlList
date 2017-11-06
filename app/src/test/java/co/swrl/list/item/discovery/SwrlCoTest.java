package co.swrl.list.item.discovery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import co.swrl.list.SwrlPreferences;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SwrlCoTest {
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
    public void canGetSwrlsFromSwrlCoWithSwrlsInDBFilteredOut() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[" +
                        "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years...\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293354\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"0553293354\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}," +
                        "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation2 by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years...\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293355\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"123\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}," +
                        "{\"is_private\":false,\"author_id\":10,\"thumbnail_url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"username\":\"Kyle Harrison\",\"type\":\"book\",\"title\":\"Foundation3 by Isaac Asimov\",\"creation_date\":\"2017-10-09T20:09:33Z\",\"details\":{\"url\":\"https://www.amazon.com/Foundation-Isaac-Asimov/dp/0553293354?SubscriptionId=AKIAIO3J752UN7X4HUWA&tag=corejavaint0d-20&linkCode=xm2&camp=2025&creative=165953&creativeASIN=0553293354\",\"blurb\":\"For twelve thousand years...\",\"title\":\"Foundation\",\"author\":\"Isaac Asimov\",\"book-id\":\"0553293356\",\"big-img-url\":\"https://images-na.ssl-images-amazon.com/images/I/51RZ30jFyDL.jpg\",\"publication-date\":\"1991-10-01\"},\"review\":\"A true classic, and rightly so.\",\"external_id\":\"123\",\"id\":906,\"email_md5\":\"08fb49c589a4e65b9984148e17cdc1d9\"}" +
                        "]"));
        mockWebServer.start();

        HttpUrl mockUrl = mockWebServer.url("/");

        ArrayList<Swrl> mockedAllFromDB = new ArrayList<>();

        Swrl foundation3 = new Swrl("Foundation3", Type.BOOK);
        Details foundation3Details = new Details("Foundation 3", "Overview", "0553293356", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        foundation3.setDetails(foundation3Details);

        mockedAllFromDB.add(foundation3);

        CollectionManager mockCollectionManager = mock(CollectionManager.class);
        when(mockCollectionManager.getAll()).thenReturn(mockedAllFromDB);

        SwrlPreferences mockPreferences = mock(SwrlPreferences.class);
        when(mockPreferences.getUserID()).thenReturn("123");
        when(mockPreferences.getAuthToken()).thenReturn("222ddd");

        SwrlCoLists swrlCoLists = new SwrlCoLists(mockUrl, mockCollectionManager, mockPreferences);

        List<Swrl> results = swrlCoLists.get();

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/?auth_token=222ddd&user_id=123", request.getPath());

        assertEquals(2, results.size());
        assertEquals("Foundation by Isaac Asimov", results.get(0).getTitle());
        assertEquals("Foundation2 by Isaac Asimov", results.get(1).getTitle());
    }
}