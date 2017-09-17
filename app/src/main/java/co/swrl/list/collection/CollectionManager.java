package co.swrl.list.collection;

import java.util.List;

import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

public interface CollectionManager {
    List<Swrl> getActive();
    List<Swrl> getActiveWithFilter(Type typeFilter);
    int countActive();
    int countActiveByFilter(Type typeFilter);
    List<Swrl> getDone();
    void save(Swrl swrl);
    void markAsDone(Swrl swrl);
    void markAsActive(Swrl swrl);
    void permanentlyDelete(Swrl swrl);
    void permanentlyDeleteAll();
    void saveDetails(Swrl swrl, Details details);
    void updateTitle(Swrl swrl, String title);
}
