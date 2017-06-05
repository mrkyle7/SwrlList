package co.swrl.list.item;

import co.swrl.list.R;

public enum Type {
    FILM("Film", R.color.film,
            SwrlSearch.getFilmSearch(), R.drawable.ic_film, "Director"),
    TV("TV Show", R.color.tv,
            SwrlSearch.getTVSearch(), R.drawable.ic_tv_icon, "Director"),
    BOOK("Book", R.color.book,
            SwrlSearch.getBookSearch(), R.drawable.ic_import_contacts_black_24dp, "Author"),
    ALBUM("Music Album", R.color.album,
            SwrlSearch.getAlbumSearch(), R.drawable.ic_headset_black_24dp, "Artist"),
    VIDEO_GAME("Video Game", R.color.video_game,
            SwrlSearch.getVideoGameSearch(), R.drawable.ic_video_game_icon, "Platform"),
    BOARD_GAME("Board Game", R.color.board_game,
            SwrlSearch.getBoardGameSearch(), R.drawable.ic_boardgame_icon, "Designer"),
    APP("Phone App", R.color.app,
            SwrlSearch.getAppSearch(), R.drawable.ic_app_icon, "Creator"),
    PODCAST("Podcast", R.color.podcast,
            SwrlSearch.getPodcastSearch(), R.drawable.ic_podcast_icon, "From"),
    UNKNOWN("Swrl", R.color.unknown,
            new UnknownSearch(), R.drawable.ic_star_black_24dp, "By");

    private String friendlyName;
    private int color;
    private final Search search;
    private int icon;
    private final String creatorType;

    Type(String friendlyName, int color, Search search, int icon, String creatorType) {
        this.friendlyName = friendlyName;
        this.color = color;
        this.search = search;
        this.icon = icon;
        this.creatorType = creatorType;
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

    public int getIcon() {
        return icon;
    }

    public String getCreatorType() {
        return creatorType;
    }
}
