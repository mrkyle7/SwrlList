package co.swrl.list.item;

import com.google.gson.annotations.SerializedName;

import co.swrl.list.R;
import co.swrl.list.item.search.Search;
import co.swrl.list.item.search.SwrlSearch;
import co.swrl.list.item.search.UnknownSearch;

public enum Type {
    @SerializedName(value = "FILM", alternate = {"movie"})
    FILM("Film", "Films",
            R.color.film, SwrlSearch.getFilmSearch(), R.drawable.ic_film, "Director"),

    @SerializedName(value = "TV", alternate = {"tv"})
    TV("TV Show", "TV Shows",
            R.color.tv, SwrlSearch.getTVSearch(), R.drawable.ic_tv_icon, "Director"),

    @SerializedName(value = "BOOK", alternate = {"book"})
    BOOK("Book", "Books",
            R.color.book, SwrlSearch.getBookSearch(), R.drawable.ic_import_contacts_black_24dp, "Author"),

    @SerializedName(value = "ALBUM", alternate = {"album"})
    ALBUM("Music Album", "Music Albums",
            R.color.album, SwrlSearch.getAlbumSearch(), R.drawable.ic_headset_black_24dp, "Artist"),

    @SerializedName(value = "VIDEO_GAME", alternate = {"game"})
    VIDEO_GAME("Video Game", "Video Games",
            R.color.video_game, SwrlSearch.getVideoGameSearch(), R.drawable.ic_video_game_icon, "Publisher"),

    @SerializedName(value = "BOARD_GAME", alternate = {"boardgame"})
    BOARD_GAME("Board Game", "Board Games",
            R.color.board_game, SwrlSearch.getBoardGameSearch(), R.drawable.ic_boardgame_icon, "Designer"),

    @SerializedName(value = "APP", alternate = {"app"})
    APP("Phone App", "Phone Apps",
            R.color.app, SwrlSearch.getAppSearch(), R.drawable.ic_app_icon, "Creator"),

    @SerializedName(value = "PODCAST", alternate = {"podcast"})
    PODCAST("Podcast", "Podcasts",
            R.color.podcast, SwrlSearch.getPodcastSearch(), R.drawable.ic_podcast_icon, "From"),

    UNKNOWN("Swrl", "All Swrls",
            R.color.colorPrimary, new UnknownSearch(), R.drawable.ic_star_black_24dp, "By");

    private String friendlyName;
    private final String friendlyNamePlural;
    private int color;
    private final Search search;
    private int icon;
    private final String creatorType;

    Type(String friendlyName, String friendlyNamePlural, int color, Search search, int icon, String creatorType) {
        this.friendlyName = friendlyName;
        this.friendlyNamePlural = friendlyNamePlural;
        this.color = color;
        this.search = search;
        this.icon = icon;
        this.creatorType = creatorType;
    }

    public int      getColor() {
        return color;
    }

    public Search getSearch() {
        return search;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getFriendlyNamePlural() {
        return friendlyNamePlural;
    }

    public int getIcon() {
        return icon;
    }

    public String getCreatorType() {
        return creatorType;
    }
}
