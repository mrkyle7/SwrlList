package co.swrl.list.item;

import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Swrl implements Serializable {
    private final String title;
    private final Type type;
    private final String review;

    @SerializedName(value = "author", alternate = {"username"})
    private final String author;

    @SerializedName(value = "authorId", alternate = {"author_id"})
    private int authorId;

    @SerializedName(value = "authorAvatarUrl", alternate = {"author_avatar_url"})
    private String authorAvatarUrl;

    private final int id;
    private Details details;

    public Swrl(@NonNull String title, @NonNull Type type, String review, String author, int authorId, String authorAvatarUrl, int id) {
        this.title = (String) assertNonNull(title, "title");
        this.type = (Type) assertNonNull(type, "type");
        this.review = review;
        this.author = author;
        this.authorId = authorId;
        this.authorAvatarUrl = authorAvatarUrl;
        this.id = id;
    }

    public Swrl(@NonNull String title, @NonNull Type type){
        this(title, type, null, null, 0, null, 0);
    }

    public Swrl(String title) {
        this(title, Type.UNKNOWN, null, null, 0, null, 0);
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        if (type == null){
            return Type.UNKNOWN;
        } else {
            return type;
        }
    }

    public Details getDetails() {
        return details;
    }

    public String getReview() {
        if (review == null) return null;
        Log.d("SWRL", review);
        String s = String.valueOf(Html.fromHtml(review));
        return s.trim();
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return title + " (" + type.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swrl)) return false;

        Swrl swrl = (Swrl) o;

        return title.equals(swrl.title) && type == swrl.type;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    private Object assertNonNull(Object value, String parameter) {
        if (value == null){
            throw new IllegalArgumentException(parameter + " is null");
        } else {
            return value;
        }
    }

    public String getAuthor() {
        return author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthorAvatarURL(){
        return authorAvatarUrl;
    }

    public int getId() {
        return id;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setAuthorAvatarURL(String avatarURL) {
        this.authorAvatarUrl = avatarURL;
    }
}
