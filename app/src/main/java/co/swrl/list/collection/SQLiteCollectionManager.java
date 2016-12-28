package co.swrl.list.collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.swrl.list.item.Swrl;

import static co.swrl.list.collection.DBContract.Swrls;


public class SQLiteCollectionManager implements CollectionManager, Serializable {
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
                Swrls.COLUMN_NAME_TITLE
        };

        String selection = Swrls.COLUMN_NAME_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(status)};

        String sortOrder = Swrls.COLUMN_NAME_CREATED + " DESC";

        Cursor c = dbReader.query(
                Swrls.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        ArrayList<Swrl> swrls = new ArrayList<>();

        while (c.moveToNext()){
            String title = c.getString(c.getColumnIndexOrThrow(Swrls.COLUMN_NAME_TITLE));
            swrls.add(new Swrl(title));
        }

        c.close();
        dbReader.close();
        return swrls;
    }

    @Override
    public void save(Swrl swrl) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Swrls.COLUMN_NAME_STATUS, Swrls.STATUS_ACTIVE);
        values.put(Swrls.COLUMN_NAME_TITLE, swrl.getTitle());
        values.put(Swrls.COLUMN_NAME_CREATED, System.currentTimeMillis());

        dbWriter.insert(Swrls.TABLE_NAME, null, values);
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
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ?";
        String[] whereArgs = {title};

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
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ?";
        String[] whereArgs = {swrl.getTitle()};

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

    private class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "swrlList.db";
        private static final int DATABASE_VERSION = 1;

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES_SWRLS =
                "CREATE TABLE " + Swrls.TABLE_NAME + " (" +
                        Swrls._ID + " INTEGER PRIMARY KEY," +
                        Swrls.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_STATUS + INTEGER_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_CREATED + INTEGER_TYPE + COMMA_SEP +
                        "UNIQUE( " + Swrls.COLUMN_NAME_TITLE + " ) ON CONFLICT REPLACE" +
                        " )";

        DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES_SWRLS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}