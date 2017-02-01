package co.swrl.list.collection;

import android.provider.BaseColumns;

final class DBContract {
    private DBContract(){}

    static class Swrls implements BaseColumns {
        static final String TABLE_NAME = "swrls";
        static final String UNIQUE_INDEX_TITLE_TYPE = "unique_index_title_type";

        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_STATUS = "status";
        static final String COLUMN_NAME_CREATED = "created";
        static final String COLUMN_NAME_TYPE = "type";
        static final String COLUMN_NAME_DETAILS = "details";

        static final int STATUS_ACTIVE = 0;
        static final int STATUS_DONE = 1;
    }
}
