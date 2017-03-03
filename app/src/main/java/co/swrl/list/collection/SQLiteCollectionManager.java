package co.swrl.list.collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static co.swrl.list.collection.DBContract.Swrls;


public class SQLiteCollectionManager implements CollectionManager, Serializable {
    public static final String DB_LOG = "DB";
    private final DBHelper db;

    public SQLiteCollectionManager(Context context) {
        db = new DBHelper(context);
    }

    @Override
    public List<Swrl> getActive() {
        return getSwrlsByStatus(Swrls.STATUS_ACTIVE);
    }

    @Override
    public List<Swrl> getDone() {
        return getSwrlsByStatus(Swrls.STATUS_DONE);
    }

    @NonNull
    private List<Swrl> getSwrlsByStatus(int status) {
        SQLiteDatabase dbReader = db.getReadableDatabase();
        String[] projection = {
                Swrls._ID,
                Swrls.COLUMN_NAME_TITLE,
                Swrls.COLUMN_NAME_TYPE,
                Swrls.COLUMN_NAME_DETAILS
        };

        String selection = Swrls.COLUMN_NAME_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(status)};

        String sortOrder = Swrls.COLUMN_NAME_CREATED + " DESC";

        Cursor row = dbReader.query(
                Swrls.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ArrayList<Swrl> swrls = new ArrayList<>();

        while (row.moveToNext()) {
            String title = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_TITLE));
            Type type = getTypeFromRow(row);
            String storedDetailsJSON = getDetailsFromRow(row);

            Swrl swrl = new Swrl(title, type);

            Details details = new Gson().fromJson(storedDetailsJSON, Details.class);
            swrl.setDetails(details);

            swrls.add(swrl);
        }
        row.close();
        dbReader.close();
        return swrls;
    }

    private String getDetailsFromRow(Cursor row) {
        return row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_DETAILS));
    }

    private Type getTypeFromRow(Cursor row) {
        Type type;
        String storedType = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_TYPE));
        try {
            type = Type.valueOf(storedType);
        } catch (IllegalArgumentException e) {
            Log.e(DB_LOG, storedType + " is not a valid type");
            e.printStackTrace();
            type = Type.UNKNOWN;
        }
        return type;
    }

    @Override
    public void save(Swrl swrl) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Swrls.COLUMN_NAME_STATUS, Swrls.STATUS_ACTIVE);
        values.put(Swrls.COLUMN_NAME_TITLE, swrl.getTitle());
        values.put(Swrls.COLUMN_NAME_TYPE, swrl.getType().toString());
        values.put(Swrls.COLUMN_NAME_CREATED, System.currentTimeMillis());

        dbWriter.replace(Swrls.TABLE_NAME, null, values);
        dbWriter.close();
    }

    @Override
    public void markAsDone(Swrl swrl) {
        markAs(swrl, Swrls.STATUS_DONE);
    }

    @Override
    public void markAsActive(Swrl swrl) {
        markAs(swrl, Swrls.STATUS_ACTIVE);
    }

    private void markAs(Swrl swrl, int status) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Swrls.COLUMN_NAME_STATUS, status);

        String title = swrl.getTitle();
        String type = swrl.getType().toString();
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ? AND " +
                Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] whereArgs = {title, type};

        dbWriter.update(
                Swrls.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );
        dbWriter.close();
    }

    @Override
    public void permanentlyDelete(Swrl swrl) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();
        String title = swrl.getTitle();
        String type = swrl.getType().toString();
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ? AND " +
                Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] whereArgs = {title, type};

        dbWriter.delete(
                Swrls.TABLE_NAME,
                whereClause,
                whereArgs
        );
        dbWriter.close();
    }

    @Override
    public void permanentlyDeleteAll() {
        SQLiteDatabase dbWriter = db.getWritableDatabase();
        String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + Swrls.TABLE_NAME;

        dbWriter.execSQL(SQL_DROP_TABLE);

        db.onCreate(dbWriter);
        dbWriter.close();
    }

    @Override
    public void saveDetails(Swrl swrl, Details details) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();

        String detailsJSONString = new Gson().toJson(details);

        ContentValues values = new ContentValues();
        values.put(Swrls.COLUMN_NAME_DETAILS, detailsJSONString);

        String title = swrl.getTitle();
        String type = swrl.getType().toString();
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ? AND " +
                Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] whereArgs = {title, type};

        dbWriter.update(
                Swrls.TABLE_NAME,
                values,
                whereClause,
                whereArgs
        );
        dbWriter.close();
    }

    private class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "swrlList.db";
        private static final int DATABASE_VERSION = 3;

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES_SWRLS =
                "CREATE TABLE " + Swrls.TABLE_NAME + " (" +
                        Swrls._ID + " INTEGER PRIMARY KEY," +
                        Swrls.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_TYPE + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_CREATED + INTEGER_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_DETAILS + TEXT_TYPE +
                        " )";

        private static final String SQL_CREATE_UNIQUE_INDEX_TITLE_TYPE =
                "CREATE UNIQUE INDEX " + Swrls.UNIQUE_INDEX_TITLE_TYPE +
                        " ON " + Swrls.TABLE_NAME +
                        "( " + Swrls.COLUMN_NAME_TITLE + COMMA_SEP + Swrls.COLUMN_NAME_TYPE + ")";

        private static final String SQL_ADD_TYPE_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_TYPE + TEXT_TYPE +
                        " DEFAULT 'UNKNOWN'";

        private static final String SQL_ADD_DETAILS_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_DETAILS + TEXT_TYPE;

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES_SWRLS);
            db.execSQL(SQL_CREATE_UNIQUE_INDEX_TITLE_TYPE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                db.execSQL(SQL_ADD_TYPE_COLUMN);
                db.execSQL(SQL_CREATE_UNIQUE_INDEX_TITLE_TYPE);
            }
            if (oldVersion < 3) {
                db.execSQL(SQL_ADD_DETAILS_COLUMN);
            }
        }
    }
}
