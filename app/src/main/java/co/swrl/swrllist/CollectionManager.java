package co.swrl.swrllist;

import java.util.List;

public interface CollectionManager {
    List<Swrl> getSwrls();
    List<Swrl> getDeletedSwrls();
    void saveSwrl(Swrl swrl);
    void deleteSwrl(Swrl swrl);
    void recoverDeletedSwrl(Swrl swrl);
}
