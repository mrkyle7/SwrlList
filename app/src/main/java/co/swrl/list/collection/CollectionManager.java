package co.swrl.list.collection;

import java.util.List;

import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;

public interface CollectionManager {
    List<Swrl> getActive();
    List<Swrl> getDone();
    void save(Swrl swrl);
    void markAsDone(Swrl swrl);
    void markAsActive(Swrl swrl);
    void permanentlyDelete(Swrl swrl);
    void permanentlyDeleteAll();
    void saveDetails(Swrl swrl, Details details);
    void updateTitle(Swrl swrl, String title);
}
