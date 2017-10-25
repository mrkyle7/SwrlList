package co.swrl.list.item.discovery;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.swrl.list.item.Swrl;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PublicSwrls {
    private final HttpUrl url;

    public PublicSwrls(HttpUrl url) {
        this.url = url;
    }

    public PublicSwrls() {
        this(HttpUrl.parse("https://www.swrl.co/api/v1/discover/public"));
    }

    public List<Swrl> get() {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        Request request = new Request.Builder().url(url).build();

        Gson gson = new Gson();

        List<Swrl> publicSwrls = new ArrayList<>();

        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                String body = response.body().string();
                Swrl[] swrls = gson.fromJson(body, Swrl[].class);
                Collections.addAll(publicSwrls, swrls);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publicSwrls;
    }
}
