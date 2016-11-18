package co.swrl.swrllist;

import java.util.ArrayList;
import java.util.List;


class SqlLiteCollectionManager implements CollectionManager {
    @Override
    public List<Swrl> getSwrls() {
        return new ArrayList<>();
    }

    @Override
    public List<Swrl> getDeletedSwrls() {
        return null;
    }

    @Override
    public void saveSwrl(Swrl swrl) {

    }

    @Override
    public void deleteSwrl(Swrl swrl) {

    }

    @Override
    public void recoverDeletedSwrl(Swrl swrl) {

    }
}
