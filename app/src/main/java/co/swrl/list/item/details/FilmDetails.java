package co.swrl.list.item.details;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class FilmDetails implements Details {
    private final String title;
    private final String overview;
    private final String id;
    private final URL posterURL;

    public final static String KEY_TITLE = "title";
    public final static String KEY_OVERVIEW = "overview";
    public final static String KEY_ID = "id";
    public final static String KEY_POSTER_URL = "posterURL";

    public FilmDetails(@NonNull String title, @NonNull String overview,
                       @NonNull String id, @NonNull URL posterURL){
        assertNonNull(title, overview, id, posterURL);
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.posterURL = posterURL;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getId() {
        return id;
    }

    public URL getPosterURL() {
        return posterURL;
    }

    @Override
    public JSONObject toJSON() {
        HashMap<String,String > details = new HashMap<>();
        details.put(KEY_TITLE, title);
        details.put(KEY_OVERVIEW, overview);
        details.put(KEY_ID, id);
        details.put(KEY_POSTER_URL, posterURL.toString());
        return new JSONObject(details);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilmDetails)) return false;

        FilmDetails that = (FilmDetails) o;

        if (!title.equals(that.title)) return false;
        if (!overview.equals(that.overview)) return false;
        if (!id.equals(that.id)) return false;
        return posterURL.equals(that.posterURL);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + overview.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + posterURL.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FilmDetails{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    private void assertNonNull(String title, String overview, String id, URL posterURL) {
        if (title == null){
            throw new NullPointerException("title");
        }
        if (overview == null){
            throw new NullPointerException("overview");
        }
        if (id == null){
            throw new NullPointerException("id");
        }
        if (posterURL == null){
            throw new NullPointerException("posterURL");
        }
    }
}
