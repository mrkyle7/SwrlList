package co.swrl.list.item.discovery;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.swrl.list.item.SwrlListGenerator;
import co.swrl.list.utils.SwrlPreferences;
import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Swrl;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwrlCoLists implements SwrlListGenerator{
    private final HttpUrl url;
    private final CollectionManager db;
    private final SwrlPreferences preferences;

    SwrlCoLists(HttpUrl url, CollectionManager db, SwrlPreferences preferences) {
        this.url = url;
        this.db = db;
        this.preferences = preferences;
    }

    public static SwrlCoLists publicSwrls(CollectionManager db){
         return new SwrlCoLists(HttpUrl.parse("https://www.swrl.co/api/v1/discover/public"), db, null);
    }

    public static SwrlCoLists weightedSwrls(CollectionManager db, SwrlPreferences preferences){
         return new SwrlCoLists(HttpUrl.parse("https://www.swrl.co/api/v1/discover/weighted"), db, preferences);
    }

    public static SwrlCoLists inboxSwrls(CollectionManager db, SwrlPreferences preferences){
         return new SwrlCoLists(HttpUrl.parse("https://www.swrl.co/api/v1/discover/inbox"), db, preferences);
    }

    @Override
    public List<Swrl> get() {
        String authToken = preferences != null ? preferences.getAuthToken() : null;
        int userID = preferences != null ? preferences.getUserID() : 0;
        HttpUrl urlWithAuth = url.newBuilder()
                .addQueryParameter("auth_token", authToken)
                .addQueryParameter("user_id", String.valueOf(userID))
                .build();
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        Request request = new Request.Builder().url(urlWithAuth).build();

        Gson gson = new Gson();

        List<Swrl> publicSwrls = new ArrayList<>();

        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                String body = response.body().string();
                Swrl[] swrls = gson.fromJson(body, Swrl[].class);
                publicSwrls.addAll(filterOutSavedSwrlsByExternalID(swrls));
            }
        } catch (Exception e) {
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
