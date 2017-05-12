package co.swrl.list.item;

import co.swrl.list.R;

public enum Type {
    FILM("Film", R.drawable.row_border_film,
            new FilmSearch()),
    TV("TV Show", R.drawable.row_border_tv,
            new UnknownSearch()),
    BOOK("Book", R.drawable.row_border_book,
            new UnknownSearch()),
    ALBUM("Music Album", R.drawable.row_border_album,
            new UnknownSearch()),
    VIDEO_GAME("Video Game", R.drawable.row_border_video_game,
            new UnknownSearch()),
    BOARD_GAME("Board Game", R.drawable.row_border_board_game,
            new UnknownSearch()),
    APP("Phone App", R.drawable.row_border_app,
            new UnknownSearch()),
    PODCAST("Podcast", R.drawable.row_border_podcast,
            new UnknownSearch()),
    UNKNOWN("Swrl", R.drawable.row_border,
            new UnknownSearch());

    private String friendlyName;
    private int rowBorderResource;
    private final Search search;

    Type(String friendlyName, int rowBorderResource, Search search) {
        this.friendlyName = friendlyName;
        this.rowBorderResource = rowBorderResource;
        this.search = search;
    }

    public int getRowBorderResource() {
        return rowBorderResource;
    }

    public Search getSearch() {
        return search;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
}
