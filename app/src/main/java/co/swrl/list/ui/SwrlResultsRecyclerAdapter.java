package co.swrl.list.ui;

import java.util.List;

import co.swrl.list.item.Swrl;

public interface SwrlResultsRecyclerAdapter {
    void clear();
    void addAll(List<Swrl> results);
}
