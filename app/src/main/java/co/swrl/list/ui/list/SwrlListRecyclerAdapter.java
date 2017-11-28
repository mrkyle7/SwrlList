package co.swrl.list.ui.list;

import android.support.v7.widget.RecyclerView;

import co.swrl.list.item.Type;

public interface SwrlListRecyclerAdapter {
    void refreshList(Type type, boolean updateFromSource);
    void swipeAction(RecyclerView.ViewHolder viewHolder, int position);
    int getSwrlCount();
    int getSwrlCount(Type type);
}
