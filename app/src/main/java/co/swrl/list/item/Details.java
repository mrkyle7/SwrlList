package co.swrl.list.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Details implements Serializable {
    private final String title;

    @SerializedName(value = "overview", alternate = {"blurb", "description"})
    private final String overview;

    @SerializedName(value = "id", alternate = {"tmdb-id", "asin-id", "itunes-id", "bgg-id", "book-id", "game-id"})
    private final String id;

    @SerializedName(value = "posterURL", alternate = {"large-image-url", "big-img-url"})
    private final String posterURL;

    @SerializedName(value = "thumbnailURL", alternate = {"thumbnail-url"})
    private final String thumbnailURL;

    private final Type type;

    @SerializedName(value = "categories", alternate = {"genres"})
    private final ArrayList<String> categories;

    private final String tagline;

    @SerializedName(value = "releaseYear", alternate = {"release-year"})
    private final String releaseYear;

    @SerializedName(value = "publicationDate", alternate = {"publication-date"})
    private final String publicationDate;

    private final String url;

    @SerializedName(value = "imdbID", alternate = {"imdb-id"})
    private final String imdbID;

    @SerializedName(value = "creator", alternate = {"artist-name", "artist", "author", "director", "designer", "publisher"})
    private final String creator;

    private final String actors;

    private final String runtime;

    private final ArrayList<Ratings> ratings;

    @SerializedName(value = "minPlayers", alternate = {"min-players"})
    private final String minPlayers;

    @SerializedName(value = "maxPlayers", alternate = {"max-players"})
    private final String maxPlayers;

    @SerializedName(value = "minPlaytime", alternate = {"min-playtime"})
    private final String minPlaytime;

    @SerializedName(value = "maxPlaytime", alternate = {"max-playtime"})
    private final String maxPlaytime;

    private final String platform;

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
        if (thumbnailURL != null ? !thumbnailURL.equals(details.thumbnailURL) : details.thumbnailURL != null)
            return false;
        if (type != details.type) return false;
        if (categories != null ? !categories.equals(details.categories) : details.categories != null)
            return false;
        if (tagline != null ? !tagline.equals(details.tagline) : details.tagline != null)
            return false;
        if (releaseYear != null ? !releaseYear.equals(details.releaseYear) : details.releaseYear != null)
            return false;
        if (publicationDate != null ? !publicationDate.equals(details.publicationDate) : details.publicationDate != null)
            return false;
        if (url != null ? !url.equals(details.url) : details.url != null) return false;
        if (imdbID != null ? !imdbID.equals(details.imdbID) : details.imdbID != null) return false;
        if (creator != null ? !creator.equals(details.creator) : details.creator != null)
            return false;
        if (actors != null ? !actors.equals(details.actors) : details.actors != null) return false;
        if (runtime != null ? !runtime.equals(details.runtime) : details.runtime != null)
            return false;
        if (ratings != null ? !ratings.equals(details.ratings) : details.ratings != null)
            return false;
        if (minPlayers != null ? !minPlayers.equals(details.minPlayers) : details.minPlayers != null)
            return false;
        if (maxPlayers != null ? !maxPlayers.equals(details.maxPlayers) : details.maxPlayers != null)
            return false;
        if (minPlaytime != null ? !minPlaytime.equals(details.minPlaytime) : details.minPlaytime != null)
            return false;
        if (maxPlaytime != null ? !maxPlaytime.equals(details.maxPlaytime) : details.maxPlaytime != null)
            return false;
        return platform != null ? platform.equals(details.platform) : details.platform == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (posterURL != null ? posterURL.hashCode() : 0);
        result = 31 * result + (thumbnailURL != null ? thumbnailURL.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (categories != null ? categories.hashCode() : 0);
        result = 31 * result + (tagline != null ? tagline.hashCode() : 0);
        result = 31 * result + (releaseYear != null ? releaseYear.hashCode() : 0);
        result = 31 * result + (publicationDate != null ? publicationDate.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (imdbID != null ? imdbID.hashCode() : 0);
        result = 31 * result + (creator != null ? creator.hashCode() : 0);
        result = 31 * result + (actors != null ? actors.hashCode() : 0);
        result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
        result = 31 * result + (ratings != null ? ratings.hashCode() : 0);
        result = 31 * result + (minPlayers != null ? minPlayers.hashCode() : 0);
        result = 31 * result + (maxPlayers != null ? maxPlayers.hashCode() : 0);
        result = 31 * result + (minPlaytime != null ? minPlaytime.hashCode() : 0);
        result = 31 * result + (maxPlaytime != null ? maxPlaytime.hashCode() : 0);
        result = 31 * result + (platform != null ? platform.hashCode() : 0);
        return result;
    }


    public class Ratings implements Serializable {
        private final String Source;
        private final String Value;

        private Ratings(String source, String value) {
            Source = source;
            Value = value;
        }

        public String getSource() {
            return Source;
        }

        public String getValue() {
            return Value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Ratings)) return false;

            Ratings ratings = (Ratings) o;

            if (Source != null ? !Source.equals(ratings.Source) : ratings.Source != null)
                return false;
            return Value != null ? Value.equals(ratings.Value) : ratings.Value == null;

        }

        @Override
        public int hashCode() {
            int result = Source != null ? Source.hashCode() : 0;
            result = 31 * result + (Value != null ? Value.hashCode() : 0);
            return result;
        }
    }


    private Details(String title, String overview, String id, String posterURL, String thumbnailURL, ArrayList<String> categories, String tagline, Type type, String releaseYear, String publicationDate, String url, String imdbID, String creator, String actors, String runtime, ArrayList<Ratings> ratings, String minPlayers, String maxPlayers, String minPlaytime, String maxPlaytime, String platform) {
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.posterURL = posterURL;
        this.thumbnailURL = thumbnailURL;
        this.categories = categories;
        this.tagline = tagline;
        this.type = type;
        this.releaseYear = releaseYear;
        this.publicationDate = publicationDate;
        this.url = url;
        this.imdbID = imdbID;
        this.creator = creator;
        this.actors = actors;
        this.runtime = runtime;
        this.ratings = ratings;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.minPlaytime = minPlaytime;
        this.maxPlaytime = maxPlaytime;
        this.platform = platform;
    }

    public Details setType(Type type) {
        return new Details(title, overview, id, posterURL, thumbnailURL, categories, tagline, type, releaseYear, publicationDate, url, imdbID, creator, actors, runtime, ratings, minPlayers, maxPlayers, minPlaytime, maxPlaytime, platform);
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

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getId() {
        return id;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public Type getType() {
        return type == null ? Type.UNKNOWN : type;
    }

    public String getTagline() {
        return tagline;
    }

    public String getUrl() {
        return url;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getImdbID() {
        return imdbID;
    }

    public ArrayList<Ratings> getRatings() {
        return ratings;
    }

    public String getCreator() {
        return creator;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getActors() {
        return actors;
    }

    public String getMinPlayers() {
        return minPlayers;
    }

    public String getMaxPlayers() {
        return maxPlayers;
    }

    public String getMinPlaytime() {
        return minPlaytime;
    }

    public String getMaxPlaytime() {
        return maxPlaytime;
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "Details{" +
                "title='" + title + '\'' +
                ", id='" + getId() + '\'' +
                '}';
    }

}
