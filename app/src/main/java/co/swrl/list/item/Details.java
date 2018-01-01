package co.swrl.list.item;

import android.text.Html;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Details implements Serializable {
    private final String title;

    @SerializedName(value = "overview", alternate = {"blurb", "description"})
    private final String overview;

    @SerializedName(value = "id", alternate = {"tmdb-id", "asin-id", "itunes-id", "bgg-id", "book-id", "game-id"})
    private final String id;

    @SerializedName(value = "large-image-url", alternate = {"posterURL", "big-img-url", "thumbnail-url", "image-url"})
    private final String posterURL;

    private final Type type;

    @SerializedName(value = "genres", alternate = {"categories"})
    private final ArrayList<String> categories;

    private final String tagline;

    @SerializedName(value = "release-year", alternate = {"releaseYear"})
    private final String releaseYear;

    @SerializedName(value = "publication-date", alternate = {"publicationDate"})
    private final String publicationDate;

    @SerializedName(value = "website-url", alternate = {"url"})
    private final String url;

    @SerializedName(value = "imdb-id", alternate = {"imdbID"})
    private final String imdbID;

    @SerializedName(value = "creator", alternate = {"artist-name", "artist", "author", "director", "designer", "publisher"})
    private final String creator;

    private final String actors;

    private final String runtime;

    private final ArrayList<Ratings> ratings;

    @SerializedName(value = "min-players", alternate = {"minPlayers"})
    private final String minPlayers;

    @SerializedName(value = "max-players", alternate = {"maxPlayers"})
    private final String maxPlayers;

    @SerializedName(value = "min-playtime", alternate = {"minPlaytime"})
    private final String minPlaytime;

    @SerializedName(value = "max-playtime", alternate = {"maxPlaytime"})
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

    public String getIMDBURL() {
        if (imdbID == null) return null;
        return "http://m.imdb.com/title/" + imdbID;
    }

    public static class Ratings implements Serializable {
        private final String Source;
        private final String Value;

        public Ratings(String source, String value) {
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


    public Details(String title, String overview, String id, String posterURL, ArrayList<String> categories, String tagline, Type type, String releaseYear, String publicationDate, String url, String imdbID, String creator, String actors, String runtime, ArrayList<Ratings> ratings, String minPlayers, String maxPlayers, String minPlaytime, String maxPlaytime, String platform) {
        this.title = title;
        this.overview = overview;
        this.id = id;
        this.posterURL = posterURL;
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
        return new Details(title, overview, id, posterURL, categories, tagline, type, releaseYear, publicationDate, url, imdbID, creator, actors, runtime, ratings, minPlayers, maxPlayers, minPlaytime, maxPlaytime, platform);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        if (overview == null) return null;
        String s = String.valueOf(Html.fromHtml(overview));
        return s.trim();
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getId() {
        return id;
    }

    public String getCategories() {
        if (categories == null || categories.isEmpty()) return null;
        ArrayList<String> formattedCategories = new ArrayList<>();
        for (String category : categories) {
            category = category.replace("-game-genre", "");
            formattedCategories.add(category);
        }
        return TextUtils.join(", ", formattedCategories);
    }

    public Type getType() {
        return type == null ? Type.UNKNOWN : type;
    }

    public String getTagline() {
        if (Objects.equals(tagline, "None")) return null;
        return tagline;
    }

    public String getUrl() {
        return url;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getRatings() {
        if (ratings == null || ratings.isEmpty()) return null;

        List<String> ratingsAsString = new ArrayList<>();
        for (Details.Ratings rating : ratings) {
            String text = "";
            text += rating.getSource().equals("Internet Movie Database") ? "IMDB" : rating.getSource();
            text += ": ";
            text += rating.getValue();
            ratingsAsString.add(text);
        }
        return TextUtils.join(", ", ratingsAsString);
    }

    public String getCreator() {
        return creator;
    }

    public String getRuntime() {
        if (runtime == null || runtime.equals("N/A")) return null;
        if (runtime.matches("\\d+")) return runtime + " min";
        return runtime;
    }

    public String getActors() {
        return actors;
    }

    public String getMinToMaxPlayers() {
        if (minPlayers == null && maxPlayers == null) return null;
        if (Objects.equals(minPlayers, maxPlayers)) return minPlayers;
        if (minPlayers == null || maxPlayers == null)
            return minPlayers == null ? maxPlayers : minPlayers;
        return minPlayers + " - " + maxPlayers;
    }

    public String getMinToMaxPlaytime() {
        if (minPlaytime == null && maxPlaytime == null) return null;
        String minToMaxPlaytime;
        if (Objects.equals(minPlaytime, maxPlaytime)) {
            minToMaxPlaytime = minPlaytime;
        } else if (minPlaytime == null || maxPlaytime == null) {
            minToMaxPlaytime = minPlaytime == null ? maxPlaytime : minPlaytime;
        } else {
            minToMaxPlaytime = minPlaytime + " - " + maxPlaytime;
        }
        return minToMaxPlaytime + " min";
    }

    public String getPlatform() {
        return platform;
    }

    @Override
    public String toString() {
        return "Details{" +
                "title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", id='" + id + '\'' +
                ", posterURL='" + posterURL + '\'' +
                ", type=" + type +
                ", categories=" + categories +
                ", tagline='" + tagline + '\'' +
                ", releaseYear='" + releaseYear + '\'' +
                ", publicationDate='" + publicationDate + '\'' +
                ", url='" + url + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", creator='" + creator + '\'' +
                ", actors='" + actors + '\'' +
                ", runtime='" + runtime + '\'' +
                ", ratings=" + ratings +
                ", minPlayers='" + minPlayers + '\'' +
                ", maxPlayers='" + maxPlayers + '\'' +
                ", minPlaytime='" + minPlaytime + '\'' +
                ", maxPlaytime='" + maxPlaytime + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
