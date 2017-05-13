package co.swrl.list.item;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwrlSearch implements Search {
    private final HttpUrl BASE_URL;
    private final Type type;

    public static SwrlSearch getFilmSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/film"), Type.FILM);
    }

    public static SwrlSearch getTVSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/tv"), Type.TV);
    }

    public static SwrlSearch getBookSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/book"), Type.BOOK);
    }

    public static SwrlSearch getPodcastSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/podcast"), Type.PODCAST);
    }

    public static SwrlSearch getAppSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/app"), Type.APP);
    }

    public static SwrlSearch getAlbumSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/album"), Type.ALBUM);
    }

    public static SwrlSearch getVideoGameSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/videogame"), Type.VIDEO_GAME);
    }

    public SwrlSearch(HttpUrl baseURL, Type type) {
        BASE_URL = baseURL;
        this.type = type;
    }

    @Override
    public Details byID(String id) {
        return null;
    }

    private class SearchResponse {
        private List<Details> results;

        SearchResponse() {
        }
    }

    @Override
    public List<Details> byTitle(String title) {
        HttpUrl searchUrl = BASE_URL.newBuilder().setQueryParameter("query", title).build();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(searchUrl).build();

        Gson gson = new Gson();
        SearchResponse searchResponse = null;

        try {
            Response response = client.newCall(request).execute();

            if (response != null) {
                String body = response.body().string();
                System.out.println("response.body=" + body);
                searchResponse = gson.fromJson(body, SearchResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Details> filmDetails = new ArrayList<>();
        if (searchResponse != null) {
            for (Details details : searchResponse.results) {
                filmDetails.add(details.setType(type));
            }
        }
        return filmDetails;
    }
}
