package co.swrl.list.item.search;

import java.util.List;

import co.swrl.list.item.Details;

public class UnknownSearch implements Search {
    @Override
    public Details byID(String id) {
        return null;
    }

    @Override
    public List<Details> byTitle(String title) {
        return null;
    }
}
