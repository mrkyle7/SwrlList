package co.swrl.list;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import co.swrl.list.item.Details;
import co.swrl.list.item.SwrlSearch;
import co.swrl.list.item.Type;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import static org.junit.Assert.assertEquals;

public class SwrlSearchTest {

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void canConvertJSONResponseIntoFilmDetails() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("{\"results\":[{\"title\":\"Garden State (2004)\",\"tmdb-id\":401,\"overview\":\"Andrew returns to his hometown for the funeral of his mother, a journey that reconnects him with past friends. The trip coincides with his decision to stop taking his powerful antidepressants. A chance meeting with Sam - a girl also suffering from various maladies - opens up the possibility of rekindling emotional attachments, confronting his psychologist father, and perhaps beginning a new life.\",\"create-url\":\"/create/movie?tmdb-id=401&\",\"large-image-url\":\"http://image.tmdb.org/t/p/original/u7IASCZ02Q94SYklSIR2609inis.jpg\",\"thumbnail-url\":\"http://image.tmdb.org/t/p/original/u7IASCZ02Q94SYklSIR2609inis.jpg\"},{\"title\":\"The Marshall Tucker Band - Live From The Garden State 1981 (2004)\",\"tmdb-id\":324409,\"overview\":\"In 1981, The Marshall Tucker Band filmed a live concert for broadcast on MTV. All original members performed except for Tommy Caldwell, who died in a car crash in 1980. The footage is a document to be treasured, capturing as it does a defining moment in the band?s history ? on the heels of tragedy, but at the top of their musical game.\\r Also included is the documentary called \\\"Which One Is Marshall Tucker?\\\".\",\"create-url\":\"/create/movie?tmdb-id=324409&\",\"large-image-url\":\"http://image.tmdb.org/t/p/original/gQU9pdJ4rGN8GvxaL2auvJ5mmXw.jpg\",\"thumbnail-url\":\"http://image.tmdb.org/t/p/original/gQU9pdJ4rGN8GvxaL2auvJ5mmXw.jpg\"}]}"));
        mockWebServer.start();
        HttpUrl mockUrl = mockWebServer.url("/");

        SwrlSearch search = new SwrlSearch(mockUrl, Type.FILM);

        List<Details> results = search.byTitle("Garden State");

        assertEquals(2, results.size());
        assertEquals("Garden State (2004)", results.get(0).getTitle());
        assertEquals("The Marshall Tucker Band - Live From The Garden State 1981 (2004)", results.get(1).getTitle());
        assertEquals(Type.FILM, results.get(0).getType());
        assertEquals(Type.FILM, results.get(1).getType());
        assertEquals("401", results.get(0).getId());
        assertEquals("324409", results.get(1).getId());
        assertEquals("http://image.tmdb.org/t/p/original/u7IASCZ02Q94SYklSIR2609inis.jpg",
                results.get(0).getPosterURL());
        assertEquals("http://image.tmdb.org/t/p/original/gQU9pdJ4rGN8GvxaL2auvJ5mmXw.jpg",
                results.get(1).getPosterURL());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("/?query=Garden%20State", request.getPath());
    }

}