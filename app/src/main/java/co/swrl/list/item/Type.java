package co.swrl.list.item;

import co.swrl.list.R;

public enum Type {
    FILM(R.drawable.row_border_film,
            new FilmSearch()),
    TV(R.drawable.row_border_tv,
            new UnknownSearch()),
    BOOK(R.drawable.row_border_book,
            new UnknownSearch()),
    ALBUM(R.drawable.row_border_album,
            new UnknownSearch()),
    VIDEO_GAME(R.drawable.row_border_video_game,
            new UnknownSearch()),
    BOARD_GAME(R.drawable.row_border_board_game,
            new UnknownSearch()),
    APP(R.drawable.row_border_app,
            new UnknownSearch()),
    PODCAST(R.drawable.row_border_podcast,
            new UnknownSearch()),
    UNKNOWN(R.drawable.row_border,
            new UnknownSearch());

    private int rowBorderResource;
    private final Search search;

    Type(int rowBorderResource, Search search) {
        this.rowBorderResource = rowBorderResource;
        this.search = search;
    }

    public int getRowBorderResource() {
        return rowBorderResource;
    }

    public Search getSearch() {
        return search;
    }
}
