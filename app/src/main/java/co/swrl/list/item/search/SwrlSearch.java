package co.swrl.list.item.search;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.swrl.list.item.Details;
import co.swrl.list.item.Type;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwrlSearch implements Search {
    private final HttpUrl SEARCH_BASE_URL;
    private final HttpUrl DETAILS_BASE_URL;
    private final Type type;
    private final String LOG_NAME = "Swrl_Search";

    public static SwrlSearch getFilmSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/film"), HttpUrl.parse("https://www.swrl.co/api/v1/details/film"), Type.FILM);
    }

    public static SwrlSearch getTVSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/tv"), HttpUrl.parse("https://www.swrl.co/api/v1/details/tv"), Type.TV);
    }

    public static SwrlSearch getBookSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/book"), HttpUrl.parse("https://www.swrl.co/api/v1/details/book"), Type.BOOK);
    }

    public static SwrlSearch getPodcastSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/podcast"), HttpUrl.parse("https://www.swrl.co/api/v1/details/podcast"), Type.PODCAST);
    }

    public static SwrlSearch getAppSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/app"), HttpUrl.parse("https://www.swrl.co/api/v1/details/app"), Type.APP);
    }

    public static SwrlSearch getAlbumSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/album"), HttpUrl.parse("https://www.swrl.co/api/v1/details/album"), Type.ALBUM);
    }

    public static SwrlSearch getVideoGameSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/videogame"), HttpUrl.parse("https://www.swrl.co/api/v1/details/videogame"), Type.VIDEO_GAME);
    }

    public static SwrlSearch getBoardGameSearch() {
        return new SwrlSearch(HttpUrl.parse("https://www.swrl.co/api/v1/search/boardgame"), HttpUrl.parse("https://www.swrl.co/api/v1/details/boardgame"), Type.BOARD_GAME);
    }

    public SwrlSearch(HttpUrl searchBaseURL, HttpUrl details_base_url, Type type) {
        SEARCH_BASE_URL = searchBaseURL;
        DETAILS_BASE_URL = details_base_url;
        this.type = type;
    }

    @Override
    public Details byID(String id) {
        HttpUrl searchUrl = DETAILS_BASE_URL.newBuilder().addPathSegment(id).build();
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        Request request = new Request.Builder().url(searchUrl).build();

        Gson gson = new Gson();
        Details details = null;

        try {
            Response response = client.newCall(request).execute();

            if (response != null) {
                String body = response.body().string();
//                Log.d(LOG_NAME, "response.body=" + body);
                details = gson.fromJson(body, Details.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (details != null) {
            details = details.setType(type);
        }

        return details;
    }

    private class SearchResponse {
        private List<Details> results;

        SearchResponse() {
        }
    }

    @Override
    public List<Details> byTitle(String title) {
        HttpUrl searchUrl = SEARCH_BASE_URL.newBuilder().setQueryParameter("query", title).build();
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        Request request = new Request.Builder().url(searchUrl).build();

        Gson gson = new Gson();
        SearchResponse searchResponse = null;

        try {
            Response response = client.newCall(request).execute();

            if (response != null) {
                String body = response.body().string();
//                Log.d(LOG_NAME, "response.body=" + body);
                searchResponse = gson.fromJson(body, SearchResponse.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Details> filmDetails = new ArrayList<>();
        if (searchResponse != null && searchResponse.results != null) {
            for (Details details : searchResponse.results) {
                filmDetails.add(details.setType(type));
            }
        }
        return filmDetails;
    }
}
