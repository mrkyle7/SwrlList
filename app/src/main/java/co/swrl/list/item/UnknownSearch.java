package co.swrl.list.item;

import java.util.List;

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
