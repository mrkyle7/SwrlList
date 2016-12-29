package co.swrl.list.item;

import co.swrl.list.R;

public enum Type {
    FILM(R.drawable.row_border_film),
    TV(R.drawable.row_border_tv),
    BOOK(R.drawable.row_border_book),
    ALBUM(R.drawable.row_border_album),
    VIDEO_GAME(R.drawable.row_border_video_game),
    BOARD_GAME(R.drawable.row_border_board_game),
    APP(R.drawable.row_border_app),
    PODCAST(R.drawable.row_border_podcast),
    UNKNOWN(R.drawable.row_border);

    private int rowBorderResource;

    Type(int rowBorderResource){
        this.rowBorderResource = rowBorderResource;
    }

    public int getRowBorderResource(){
        return rowBorderResource;
    }
}
