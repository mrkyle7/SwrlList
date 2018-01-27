package co.swrl.list.item.actions;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import co.swrl.list.collection.CollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;
import co.swrl.list.utils.SwrlPreferences;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_OK;

public class SwrlCoActions {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String LOG_TAG = "SwrlCoActions";
    public static final String LATER = "Later";
    public static final String DONE = "Done";
    public static final String DISMISSED = "Dismissed";
    public static final String REMOVE_RESPONSE = "";
    public static final String SWRLED = "Swrled";
    public static final String NOT_BAD = "Not bad";
    public static final String NOT_FOR_ME = "Not for me";
    public static final String LOVED_IT = "Loved it";
    private static final HttpUrl CREATE_URL = HttpUrl.parse("https://www.swrl.co/api/v1/swrl-actions/create-swrl");

    private static class RespondBody {
        String auth_token;
        String user_id;

        @SerializedName(value = "response-summary")
        String responseSummary;
    }
    private static class CommentBody {
        String auth_token;
        String user_id;
        String comment;
    }

    public static void respond(Swrl swrl, String response, SwrlPreferences preferences, CollectionManager collectionManager) {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

        int swrlId = swrl.getId();
        int userId = preferences.getUserID();
        String authToken = preferences.getAuthToken();

        if (swrlId == 0 || swrlId == -1 ||
                userId == 0 || userId == -1 ||
                authToken == null || authToken.isEmpty()) {
            Log.i(LOG_TAG, String.format("No Swrl ID or not logged in. swrlId=%s, userId=%s, authtoken=%s",
                    swrlId, userId, authToken));
            return;
        }

        HttpUrl respondURL = HttpUrl.parse("https://www.swrl.co/api/v1/swrl-actions/" + swrlId + "/respond");

        RespondBody respondBody = new RespondBody();
        respondBody.auth_token = authToken;
        respondBody.user_id = String.valueOf(userId);
        respondBody.responseSummary = response;

        String json = new Gson().toJson(respondBody);

        Log.d(LOG_TAG, String.format("Responding %s to swrl ID %s with URL %s and body %s",
                response, swrlId, respondURL, json));

        RequestBody postBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(respondURL)
                .post(postBody)
                .build();
        try {
            Response swrlResponse = client.newCall(request).execute();
            Log.d(LOG_TAG, "response: " + swrlResponse.body().string());
            if (Objects.equals(response, DISMISSED) && swrlResponse.code() == HTTP_OK && collectionManager != null) {
                Log.i(LOG_TAG, "Deleting Swrl: " + swrl.toString());
                collectionManager.permanentlyDelete(swrl);
            }
        } catch (IOException e) {
            Log.i(LOG_TAG, "Response Failed.");
            e.printStackTrace();
        }


    }

    public static void comment(Swrl swrl, String comment, SwrlPreferences preferences, CollectionManager collectionManager) {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

        int swrlId = swrl.getId();
        int userId = preferences.getUserID();
        String authToken = preferences.getAuthToken();

        if (swrlId == 0 || swrlId == -1 ||
                userId == 0 || userId == -1 ||
                authToken == null || authToken.isEmpty()) {
            Log.i(LOG_TAG, String.format("No Swrl ID or not logged in. swrlId=%s, userId=%s, authtoken=%s",
                    swrlId, userId, authToken));
            return;
        }

        HttpUrl commentURL = HttpUrl.parse("https://www.swrl.co/api/v1/swrl-actions/" + swrlId + "/comment");

        CommentBody commentBody = new CommentBody();
        commentBody.auth_token = authToken;
        commentBody.user_id = String.valueOf(userId);
        commentBody.comment = comment;

        String json = new Gson().toJson(commentBody);

        Log.d(LOG_TAG, String.format("Commenting %s to swrl ID %s with URL %s and body %s",
                comment, swrlId, commentURL, json));

        RequestBody postBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(commentURL)
                .post(postBody)
                .build();
        try {
            Response swrlResponse = client.newCall(request).execute();
            Log.d(LOG_TAG, "response: " + swrlResponse.body().string());
        } catch (IOException e) {
            Log.i(LOG_TAG, "Response Failed.");
            e.printStackTrace();
        }


    }

    private static class CreateBody {
        //        {:auth_token       "+&/7\"%bl?P,E|z'[$8RK(R.4yJc0j;v=clq,f9VXH-GLv%wRq1B1:pouiD%5bF{TyNx/Iq`^J3%e<h{dC0EXJhU&//J:AreA~(GuF5Jb4<~F3Yl9CEn/MN}8d4h?5~'s",
//                                   :user_id          "377"
//                                   :title "Black Mirror"
//                                   :external-id "42009"
//                                   :type "tv"
//                                   :image-url "https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg"
//                                   :private "false"
//                                   :quick-response nil
//        :users-and-emails-to-notify []
//                                   :details {:large-image-url "https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg",
//                                             :creator "Charlie Brooker, Another Creator",
//                                             :genres ["Drama" "Sci-Fi & Fantasy"],
//                                             :tmdb-id 42009,
//                                             :thumbnail-url "https://image.tmdb.org/t/p/original/djUxgzSIdfS5vNP2EHIBDIz9I8A.jpg",
//                                             :overview "Black Mirror is a British television drama series created by Charlie Brooker and shows the dark side of life and technology. The series is produced by Zeppotron for Endemol. Regarding the programme's content and structure, Brooker noted, \"each episode has a different cast, a different setting, even a different reality. But they're all about the way we live now – and the way we might be living in 10 minutes' time if we're clumsy.\"
//
//            An Endemol press release describes the series as \"a hybrid of The Twilight Zone and Tales of the Unexpected which taps into our contemporary unease about our modern world\", with the stories having a \"techno-paranoia\" feel. Channel 4 describes the first episode as \"a twisted parable for the Twitter age\". Black Mirror Series 1 was released on DVD on 27 February 2012.
//
//            In November 2012, Black Mirror won the Best TV movie/mini-series award at the International Emmys.
//
//                    Announced on 12 July 2012, the second series began broadcasting on 11 February 2013. Like the first series, it is made up of three episodes with unconnected narratives.
//
//                    Robert Downey, Jr. has optioned the episode The Entire History of You, to potentially be made into a film by Warner Bros. and his own production company Team Downey.",
//                                             :website-url "http://www.channel4.com/programmes/black-mirror/",
//                                             :title "Black Mirror",
//                                             :id "42009",
//                                             :runtime 60,
//                                             :url "http://www.channel4.com/programmes/black-mirror/"}}
        String auth_token;
        String user_id;
        String title;

        @SerializedName(value = "external-id")
        String externalId;

        Type type;

        @SerializedName(value = "image-url")
        String imageUrl;

        @SerializedName(value = "private")
        boolean isPrivate;

        @SerializedName(value = "quick-response")
        String response;

        @SerializedName(value = "users-and-emails-to-notify")
        List<String> usersAndEmailsToNotify;

        String review;

        Details details;
    }

    public static void create(Swrl swrl, String response, SwrlPreferences preferences,
                              CollectionManager collectionManager) {
        create(CREATE_URL, swrl, response, preferences, collectionManager, false, null, null);
    }

    public static Swrl createRecommendation(Swrl swrl, String review, List<String> recipients,
                                            SwrlPreferences preferences, CollectionManager collectionManager) {
        return create(CREATE_URL, swrl, SWRLED, preferences, collectionManager, false, recipients, review);
    }

    static Swrl create(HttpUrl createURL, Swrl swrl, String response, SwrlPreferences preferences,
                       CollectionManager collectionManager, boolean recommendation, List<String> recipients, String review) {
        OkHttpClient client =
                new OkHttpClient.Builder()
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();

        CreateBody createBody = new CreateBody();
        createBody.user_id = String.valueOf(preferences.getUserID());
        createBody.auth_token = preferences.getAuthToken();
        createBody.title = swrl.getTitle();
        createBody.externalId = swrl.getDetails() != null ? swrl.getDetails().getId() : null;
        createBody.type = swrl.getType();
        createBody.imageUrl = swrl.getDetails() != null ? swrl.getDetails().getPosterURL() : null;
        createBody.isPrivate = false;
        createBody.response = response;
        createBody.usersAndEmailsToNotify = recipients;
        createBody.review = review;
        createBody.details = swrl.getDetails();

        String json = new Gson().toJson(createBody);

        Log.d(LOG_TAG, "Creating swrl with json: " + json);

        RequestBody postBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(createURL)
                .post(postBody)
                .build();
        try {
            Response creationResponse = client.newCall(request).execute();
            String body = creationResponse.body().string();
            Log.d(LOG_TAG, "response: " + body);
            Swrl swrlCreated = new Gson().fromJson(body, Swrl.class);
            if (recommendation){
                collectionManager.saveRecommendation(swrlCreated);
            } else {
                collectionManager.updateSwrlID(swrl, swrlCreated.getId());
            }
            return swrlCreated;
        } catch (Exception e) {
            Log.i(LOG_TAG, "Creation Failed.");
            e.printStackTrace();
            return null;
        }
    }
}
