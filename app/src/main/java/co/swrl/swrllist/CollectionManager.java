package co.swrl.swrllist;

import java.util.List;

interface CollectionManager {
    List<Swrl> getSwrls();
    List<Swrl> getDeletedSwrls();
    void saveSwrl(Swrl swrl);
    void markAsDeleted(Swrl swrl);
    void recoverDeletedSwrl(Swrl swrl);
    void permanentlyDeleteAll();
}
