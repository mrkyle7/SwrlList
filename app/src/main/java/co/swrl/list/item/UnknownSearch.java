package co.swrl.list.item;

import java.util.List;

import co.swrl.list.item.details.Details;

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
