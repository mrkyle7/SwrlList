package co.swrl.list.item;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FilmSearch implements Search {
    private final HttpUrl BASE_URL;

    public FilmSearch() {
        this(HttpUrl.parse("http://www.swrl.co/api/v1/search/film"));
    }

    public FilmSearch(HttpUrl baseURL) {
        BASE_URL = baseURL;
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
        if (searchResponse != null){
            for (Details details: searchResponse.results) {
                filmDetails.add(details.setType(Type.FILM));
            }
        }
        return filmDetails;
    }
}
