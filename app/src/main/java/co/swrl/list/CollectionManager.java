package co.swrl.list;

import java.util.List;

interface CollectionManager {
    List<Swrl> getSwrls();
    List<Swrl> getDeletedSwrls();
    void saveSwrl(Swrl swrl);
    void markAsDone(Swrl swrl);
    void recoverDoneSwrl(Swrl swrl);
    void permanentlyDelete(Swrl swrl);
    void permanentlyDeleteAll();
}
