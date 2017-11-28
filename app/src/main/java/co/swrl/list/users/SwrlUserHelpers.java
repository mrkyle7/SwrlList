package co.swrl.list.users;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SwrlUserHelpers {

    public static String getUserAvatarURL(int userId){
        String BASE_URL = "https://www.swrl.co/api/v1/user-avatar";
        HttpUrl getAvatar = HttpUrl.parse(BASE_URL).newBuilder()
                .setQueryParameter("user_id", String.valueOf(userId)).build();

        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        Request request = new Request.Builder().url(getAvatar).build();

        String userAvatarURL;

        try {
            Response response = client.newCall(request).execute();
            userAvatarURL = new Gson().fromJson(response.body().string(), String.class);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return userAvatarURL;
    }
}
