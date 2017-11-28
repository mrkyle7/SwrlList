package co.swrl.list.collection;

import java.util.List;

import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

public interface CollectionManager {
    List<Swrl> getAll();
    List<Swrl> getActive();
    List<Swrl> getActive(Type typeFilter);
    int countActive();
    int countActive(Type typeFilter);
    List<Swrl> getDone();
    List<Swrl> getDone(Type typeFilter);
    int countDone();
    int countDone(Type typeFilter);
    void save(Swrl swrl);
    void markAsDone(Swrl swrl);
    void markAsActive(Swrl swrl);
    void permanentlyDelete(Swrl swrl);
    void permanentlyDeleteAll();
    void saveDetails(Swrl swrl, Details details);
    void updateTitle(Swrl swrl, String title);
    void updateAuthorAvatarURL(Swrl swrl, String authorAvatarURL);
}
