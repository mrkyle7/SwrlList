package co.swrl.list.ui.list.swrllists;

import android.support.v7.widget.RecyclerView;

import co.swrl.list.item.Type;

public interface SwrlListRecyclerAdapter {
    void refreshList(Type type, String textFilter, boolean updateFromSource);
    void swipeLeft(RecyclerView.ViewHolder viewHolder, int position);
    void swipeRight(RecyclerView.ViewHolder viewHolder, int position);
    int getSwrlCount();
    int getSwrlCount(Type type);
}
