package co.swrl.list;

import java.util.List;

interface CollectionManager {
    List<Swrl> getActive();
    List<Swrl> getDone();
    void save(Swrl swrl);
    void markAsDone(Swrl swrl);
    void markAsActive(Swrl swrl);
    void permanentlyDelete(Swrl swrl);
    void permanentlyDeleteAll();
}
