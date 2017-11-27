package co.swrl.list.item;

import com.google.gson.annotations.SerializedName;

import co.swrl.list.R;
import co.swrl.list.item.search.Search;
import co.swrl.list.item.search.SwrlSearch;
import co.swrl.list.item.search.UnknownSearch;

public enum Type {
    @SerializedName(value = "FILM", alternate = {"movie"})
    FILM("Film", "Films",
            R.color.film, R.color.film_dark, SwrlSearch.getFilmSearch(), R.drawable.ic_film),

    @SerializedName(value = "TV", alternate = {"tv"})
    TV("TV Show", "TV Shows",
            R.color.tv, R.color.tv_dark, SwrlSearch.getTVSearch(), R.drawable.ic_tv_icon),

    @SerializedName(value = "BOOK", alternate = {"book"})
    BOOK("Book", "Books",
            R.color.book, R.color.book_dark, SwrlSearch.getBookSearch(), R.drawable.ic_import_contacts_black_24dp),

    @SerializedName(value = "ALBUM", alternate = {"album"})
    ALBUM("Music Album", "Music Albums",
            R.color.album, R.color.album_dark, SwrlSearch.getAlbumSearch(), R.drawable.ic_headset_black_24dp),

    @SerializedName(value = "VIDEO_GAME", alternate = {"game"})
    VIDEO_GAME("Video Game", "Video Games",
            R.color.video_game, R.color.video_game_dark, SwrlSearch.getVideoGameSearch(), R.drawable.ic_video_game_icon),

    @SerializedName(value = "BOARD_GAME", alternate = {"boardgame"})
    BOARD_GAME("Board Game", "Board Games",
            R.color.board_game, R.color.board_game_dark, SwrlSearch.getBoardGameSearch(), R.drawable.ic_boardgame_icon),

    @SerializedName(value = "APP", alternate = {"app"})
    APP("Phone App", "Phone Apps",
            R.color.app, R.color.app_dark, SwrlSearch.getAppSearch(), R.drawable.ic_app_icon),

    @SerializedName(value = "PODCAST", alternate = {"podcast"})
    PODCAST("Podcast", "Podcasts",
            R.color.podcast, R.color.podcast_dark, SwrlSearch.getPodcastSearch(), R.drawable.ic_podcast_icon),

    @SerializedName(value = "WEBSITE", alternate = {"website"})
    WEBSITE("Website", "Websites",
            R.color.website, R.color.website_dark, new UnknownSearch(), R.drawable.ic_website),

    @SerializedName(value = "VIDEO", alternate = {"video"})
    VIDEO("Video", "Videos",
            R.color.video, R.color.video_dark, new UnknownSearch(), R.drawable.ic_videocam_black_24dp),

    UNKNOWN("Swrl", "All Swrls",
            R.color.colorPrimary, R.color.colorPrimaryDark, new UnknownSearch(), R.drawable.ic_nut);

    private final String friendlyName;
    private final String friendlyNamePlural;
    private final int color;
    private final Search search;
    private final int icon;
    private final int darkColor;

    Type(String friendlyName, String friendlyNamePlural, int color, int darkColor, Search search, int icon) {
        this.friendlyName = friendlyName;
        this.friendlyNamePlural = friendlyNamePlural;
        this.color = color;
        this.search = search;
        this.icon = icon;
        this.darkColor = darkColor;
    }

    public int getColor() {
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

    public int getDarkColor() {
        return darkColor;
    }
}
