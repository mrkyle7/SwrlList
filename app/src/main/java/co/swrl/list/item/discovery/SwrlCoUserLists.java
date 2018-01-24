package co.swrl.list.item.discovery;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.TimeUnit;

import co.swrl.list.item.Swrl;
import co.swrl.list.utils.SwrlPreferences;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwrlCoUserLists{
    private final HttpUrl url;
    private final SwrlPreferences preferences;

    SwrlCoUserLists(HttpUrl url, SwrlPreferences preferences) {
        this.url = url;
        this.preferences = preferences;
    }

    public static SwrlCoUserLists currentUserLists(SwrlPreferences preferences){
         return new SwrlCoUserLists(HttpUrl.parse("https://www.swrl.co/api/v1/discover/lists"), preferences);
    }

    public class UserLists {
        public List<Swrl> active;
        public List<Swrl> dismissed;
        public List<Swrl> swrled;
        public List<Swrl> done;
    }

    public UserLists get() {
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

        UserLists lists = null;

        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                String body = response.body().string();
                lists = gson.fromJson(body, UserLists.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }
}
