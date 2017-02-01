package co.swrl.list.item;

import co.swrl.list.R;
import co.swrl.list.item.details.DetailsBuilder;
import co.swrl.list.item.details.FilmDetailsBuilder;
import co.swrl.list.item.details.UnknownDetailsBuilder;

public enum Type {
    FILM(R.drawable.row_border_film,
            new FilmSearch(),
            new FilmDetailsBuilder()),
    TV(R.drawable.row_border_tv,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    BOOK(R.drawable.row_border_book,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    ALBUM(R.drawable.row_border_album,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    VIDEO_GAME(R.drawable.row_border_video_game,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    BOARD_GAME(R.drawable.row_border_board_game,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    APP(R.drawable.row_border_app,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    PODCAST(R.drawable.row_border_podcast,
            new UnknownSearch(),
            new UnknownDetailsBuilder()),
    UNKNOWN(R.drawable.row_border,
            new UnknownSearch(),
            new UnknownDetailsBuilder());

    private int rowBorderResource;
    private final Search search;
    private final DetailsBuilder detailsBuilder;

    Type(int rowBorderResource, Search search, DetailsBuilder detailsBuilder) {
        this.rowBorderResource = rowBorderResource;
        this.search = search;
        this.detailsBuilder = detailsBuilder;
    }

    public int getRowBorderResource() {
        return rowBorderResource;
    }

    public Search getSearch() {
        return search;
    }

    public DetailsBuilder getDetailsBuilder() {
        return detailsBuilder;
    }
}
