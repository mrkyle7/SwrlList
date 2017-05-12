package co.swrl.list.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Details implements Serializable{
    private final String title;
    private final String overview;
    @SerializedName(value = "id", alternate = {"tmdb-id", "asin-id"})
    private final String id;
    @SerializedName(value = "posterURL", alternate = {"large-image-url"})
    private final String posterURL;
    private final Type type;

    private Details(String title, String overview, String id, String posterURL, Type type) {
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.posterURL = posterURL;
        this.type = type;
    }

    public Details setType(Type type) {
        return new Details(title, overview, id, posterURL, type);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type == null ? Type.UNKNOWN : type;
    }

    @Override
    public String toString() {
        return "Details{" +
                "title='" + title + '\'' +
                ", id='" + getId() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Details)) return false;

        Details details = (Details) o;

        if (title != null ? !title.equals(details.title) : details.title != null) return false;
        if (overview != null ? !overview.equals(details.overview) : details.overview != null)
            return false;
        if (id != null ? !id.equals(details.id) : details.id != null) return false;
        if (posterURL != null ? !posterURL.equals(details.posterURL) : details.posterURL != null)
            return false;
        return type == details.type;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (posterURL != null ? posterURL.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
