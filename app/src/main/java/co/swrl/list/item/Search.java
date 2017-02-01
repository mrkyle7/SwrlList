package co.swrl.list.item;

import java.util.List;

import co.swrl.list.item.details.Details;

public interface Search {
    Details byID(String id);
    List<Details> byTitle(String title);
}
