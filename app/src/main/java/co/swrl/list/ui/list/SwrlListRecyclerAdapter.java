package co.swrl.list.ui.list;

import android.support.v7.widget.RecyclerView;

import co.swrl.list.item.Type;

public interface SwrlListRecyclerAdapter{
    void refreshAll();
    void refreshAllWithFilter(Type type);
    void swipeAction(RecyclerView.ViewHolder viewHolder, int position);
    int getSwrlCount();
    int getSwrlCount(Type type);
}
