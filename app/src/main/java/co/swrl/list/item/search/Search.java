package co.swrl.list.item.search;

import java.util.List;

import co.swrl.list.item.Details;

public interface Search {
    Details byID(String id);
    List<Details> byTitle(String title);
}
