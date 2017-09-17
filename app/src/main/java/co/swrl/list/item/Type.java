package co.swrl.list.item;

import co.swrl.list.R;
import co.swrl.list.item.search.Search;
import co.swrl.list.item.search.SwrlSearch;
import co.swrl.list.item.search.UnknownSearch;

public enum Type {
    FILM("Film", "Films",
            R.color.film, SwrlSearch.getFilmSearch(), R.drawable.ic_film, "Director"),
    TV("TV Show", "TV Shows",
            R.color.tv, SwrlSearch.getTVSearch(), R.drawable.ic_tv_icon, "Director"),
    BOOK("Book", "Books",
            R.color.book, SwrlSearch.getBookSearch(), R.drawable.ic_import_contacts_black_24dp, "Author"),
    ALBUM("Music Album", "Music Albums",
            R.color.album, SwrlSearch.getAlbumSearch(), R.drawable.ic_headset_black_24dp, "Artist"),
    VIDEO_GAME("Video Game", "Video Games",
            R.color.video_game, SwrlSearch.getVideoGameSearch(), R.drawable.ic_video_game_icon, "Platform"),
    BOARD_GAME("Board Game", "Board Games",
            R.color.board_game, SwrlSearch.getBoardGameSearch(), R.drawable.ic_boardgame_icon, "Designer"),
    APP("Phone App", "Phone Apps",
            R.color.app, SwrlSearch.getAppSearch(), R.drawable.ic_app_icon, "Creator"),
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
