package co.swrl.list.users;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static List<SwrlCoUser> getUsers(){
        HttpUrl url = HttpUrl.parse("https://www.swrl.co/api/v1/users");

        return getSwrlCoUsers(url);
    }

    public static List<SwrlCoUser> getContacts(int userId, String authKey){
        String BASE_URL = "https://www.swrl.co/api/v1/contacts";
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .setQueryParameter("user_id", String.valueOf(userId))
                .setQueryParameter("auth_key", authKey)
                .build();


        return getSwrlCoUsers(url);
    }

    @NonNull
    private static List<SwrlCoUser> getSwrlCoUsers(HttpUrl url) {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
        Request request = new Request.Builder().url(url).build();

        List<SwrlCoUser> users = new ArrayList<>();
        Log.d("USER_HELPER_USERS", "Getting Users from " + url.toString());

        try {
            Response response = client.newCall(request).execute();
            SwrlCoUser[] usersFromResponse = new Gson().fromJson(response.body().string(), SwrlCoUser[].class);
            Collections.addAll(users, usersFromResponse);
        } catch (Exception e){
            Log.e("USER_HELPER_USERS", "Error getting Users from " + url.toString());
            e.printStackTrace();
        }
        return users;
    }
}
