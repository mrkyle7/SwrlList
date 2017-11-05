package co.swrl.list.item.discovery;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PublicSwrls {
    private final HttpUrl url;
    private final CollectionManager db;

    public PublicSwrls(HttpUrl url, CollectionManager db) {
        this.url = url;
        this.db = db;
    }

    public PublicSwrls(CollectionManager db) {
        this(HttpUrl.parse("https://www.swrl.co/api/v1/discover/public"), db);
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
                publicSwrls.addAll(filterOutSavedSwrlsByExternalID(swrls));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return publicSwrls;
    }

    private List<Swrl> filterOutSavedSwrlsByExternalID(Swrl[] swrls) {
        ArrayList<Swrl> filtered = new ArrayList<>();
        if (db == null) {
            Collections.addAll(filtered, swrls);
        } else {
            ArrayList<Swrl> saved = (ArrayList<Swrl>) db.getAll();
            HashSet<String> savedIDs = new HashSet<>();
            for (Swrl eachSaved : saved) {
                if (eachSaved.getDetails() != null && eachSaved.getDetails().getId() != null) {
                    savedIDs.add(eachSaved.getDetails().getId());
                }
            }
            for (Swrl swrl : swrls) {
                if (swrl.getDetails() == null
                        || swrl.getDetails().getId() == null
                        || !savedIDs.contains(swrl.getDetails().getId())) {
                    filtered.add(swrl);
                }
            }
        }
        return filtered;
    }
}
