package co.swrl.list.item;

import java.util.List;

public interface Search {
    Details byID(String id);
    List<Details> byTitle(String title);
}
