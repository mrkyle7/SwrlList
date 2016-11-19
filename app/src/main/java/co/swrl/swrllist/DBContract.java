package co.swrl.swrllist;

import android.provider.BaseColumns;

final class DBContract {
    private DBContract(){}

    static class Swrls implements BaseColumns {
        static final String TABLE_NAME = "swrls";
        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_STATUS = "status";
        static final String COLUMN_NAME_CREATED = "created";

        static final int STATUS_ACTIVE = 0;
        static final int STATUS_DELETED = 1;
    }
}
