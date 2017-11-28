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
    private static final String DB_LOG = "DB";
    private final DBHelper db;
    private final String[] projection = new String[]{
            Swrls._ID,
            Swrls.COLUMN_NAME_TITLE,
            Swrls.COLUMN_NAME_TYPE,
            Swrls.COLUMN_NAME_DETAILS,
            Swrls.COLUMN_NAME_AUTHOR,
            Swrls.COLUMN_NAME_AUTHOR_ID,
            Swrls.COLUMN_NAME_AUTHOR_AVATAR_URL,
            Swrls.COLUMN_NAME_REVIEW,
            Swrls.COLUMN_NAME_SWRL_ID
    };

    public SQLiteCollectionManager(Context context) {
        db = new DBHelper(context);
    }

    @Override
    public List<Swrl> getAll() {
        SQLiteDatabase dbReader = db.getReadableDatabase();

        String sortOrder = Swrls.COLUMN_NAME_CREATED + " DESC";

        Cursor row = dbReader.query(
                Swrls.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        ArrayList<Swrl> swrls = getSwrlsFromRows(row);
        row.close();
        dbReader.close();
        return swrls;
    }

    @Override
    public List<Swrl> getActive() {
        return getSwrlsByStatus(Swrls.STATUS_ACTIVE);
    }

    @Override
    public List<Swrl> getActive(Type typeFilter) {
        return getSwrlsByStatusAndType(Swrls.STATUS_ACTIVE, typeFilter);
    }

    @Override
    public int countActive() {
        return countByStatus(Swrls.STATUS_ACTIVE);
    }

    private int countByStatus(int status) {
        SQLiteDatabase dbReader = db.getReadableDatabase();
        String[] projection = {
                "count(1)"
        };
        String selection = Swrls.COLUMN_NAME_STATUS + " = ?";
        String[] selectionArgs = {
                String.valueOf(status)
        };

        Cursor row = dbReader.query(
                Swrls.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        row.moveToFirst();
        int count = row.getInt(0);

        row.close();
        dbReader.close();
        return count;
    }

    @Override
    public int countActive(Type typeFilter) {
        return countByStatusAndType(typeFilter, Swrls.STATUS_ACTIVE);
    }

    private int countByStatusAndType(Type typeFilter, int status) {
        SQLiteDatabase dbReader = db.getReadableDatabase();

        String[] projection = {
                "count(1)"
        };
        String selection = Swrls.COLUMN_NAME_STATUS + " = ?" +
                " AND " + Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] selectionArgs = {
                String.valueOf(status),
                typeFilter.toString()
        };

        Cursor row = dbReader.query(
                Swrls.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        row.moveToFirst();
        int count = row.getInt(0);

        row.close();
        dbReader.close();
        return count;
    }

    @Override
    public List<Swrl> getDone() {
        return getSwrlsByStatus(Swrls.STATUS_DONE);
    }

    @Override
    public List<Swrl> getDone(Type typeFilter) {
        return getSwrlsByStatusAndType(Swrls.STATUS_DONE, typeFilter);
    }

    @Override
    public int countDone() {
        return countByStatus(Swrls.STATUS_DONE);
    }

    @Override
    public int countDone(Type typeFilter) {
        return countByStatusAndType(typeFilter, Swrls.STATUS_DONE);
    }

    @NonNull
    private List<Swrl> getSwrlsByStatus(int status) {
        SQLiteDatabase dbReader = db.getReadableDatabase();

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

        ArrayList<Swrl> swrls = getSwrlsFromRows(row);
        row.close();
        dbReader.close();
        return swrls;
    }

    @NonNull
    private List<Swrl> getSwrlsByStatusAndType(int status, Type typeFilter) {
        SQLiteDatabase dbReader = db.getReadableDatabase();

        String selection = Swrls.COLUMN_NAME_STATUS + " = ?" +
                " AND " + Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] selectionArgs = {
                String.valueOf(status),
                typeFilter.toString()
        };

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

        ArrayList<Swrl> swrls = getSwrlsFromRows(row);
        row.close();
        dbReader.close();
        return swrls;
    }

    @NonNull
    private ArrayList<Swrl> getSwrlsFromRows(Cursor row) {
        ArrayList<Swrl> swrls = new ArrayList<>();

        while (row.moveToNext()) {
            String title = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_TITLE));
            Type type = getTypeFromRow(row);
            String author = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_AUTHOR));
            String authorAvatarURL = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_AUTHOR_AVATAR_URL));
            int authorId = row.getInt(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_AUTHOR_ID));
            int id = row.getInt(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_SWRL_ID));
            String review = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_REVIEW));
            String storedDetailsJSON = row.getString(row.getColumnIndexOrThrow(Swrls.COLUMN_NAME_DETAILS));

            Swrl swrl = new Swrl(title, type, review, author, authorId, authorAvatarURL, id);

            Details details = new Gson().fromJson(storedDetailsJSON, Details.class);
            swrl.setDetails(details);

            swrls.add(swrl);
        }
        return swrls;
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
        values.put(Swrls.COLUMN_NAME_REVIEW, swrl.getReview());
        values.put(Swrls.COLUMN_NAME_AUTHOR, swrl.getAuthor());
        values.put(Swrls.COLUMN_NAME_AUTHOR_ID, swrl.getAuthorId());
        values.put(Swrls.COLUMN_NAME_AUTHOR_AVATAR_URL, swrl.getAuthorAvatarURL());
        values.put(Swrls.COLUMN_NAME_SWRL_ID, swrl.getId());

        dbWriter.replace(Swrls.TABLE_NAME, null, values);
        Details details = swrl.getDetails();
        if (details != null) {
            saveDetails(swrl, details);
        }
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

    @Override
    public void updateTitle(Swrl swrl, String title) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Swrls.COLUMN_NAME_TITLE, title);

        String oldTitle = swrl.getTitle();
        String type = swrl.getType().toString();
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ? AND " +
                Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] whereArgs = {oldTitle, type};

        dbWriter.updateWithOnConflict(
                Swrls.TABLE_NAME,
                values,
                whereClause,
                whereArgs,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        dbWriter.close();
    }

    @Override
    public void updateAuthorAvatarURL(Swrl swrl, String authorAvatarURL) {
        SQLiteDatabase dbWriter = db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Swrls.COLUMN_NAME_AUTHOR_AVATAR_URL, authorAvatarURL);

        String oldTitle = swrl.getTitle();
        String type = swrl.getType().toString();
        String whereClause = Swrls.COLUMN_NAME_TITLE + " = ? AND " +
                Swrls.COLUMN_NAME_TYPE + " = ?";
        String[] whereArgs = {oldTitle, type};

        dbWriter.updateWithOnConflict(
                Swrls.TABLE_NAME,
                values,
                whereClause,
                whereArgs,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        dbWriter.close();
    }

    private class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "swrlList.db";
        private static final int DATABASE_VERSION = 5;

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
                        Swrls.COLUMN_NAME_DETAILS + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_REVIEW + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_AUTHOR + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_AUTHOR_ID + INTEGER_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_AUTHOR_AVATAR_URL + TEXT_TYPE + COMMA_SEP +
                        Swrls.COLUMN_NAME_SWRL_ID + INTEGER_TYPE +
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

        private static final String SQL_ADD_REVIEW_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_REVIEW + TEXT_TYPE;

        private static final String SQL_ADD_AUTHOR_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_AUTHOR + TEXT_TYPE;

        private static final String SQL_ADD_AUTHOR_ID_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_AUTHOR_ID + INTEGER_TYPE;

        private static final String SQL_ADD_AUTHOR_AVATAR_URL_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_AUTHOR_AVATAR_URL + TEXT_TYPE;

        private static final String SQL_ADD_SWRL_ID_COLUMN =
                "ALTER TABLE " + Swrls.TABLE_NAME +
                        " ADD COLUMN " + Swrls.COLUMN_NAME_SWRL_ID + INTEGER_TYPE;

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
            if (oldVersion < 4) {
                db.execSQL(SQL_ADD_REVIEW_COLUMN);
                db.execSQL(SQL_ADD_AUTHOR_COLUMN);
                db.execSQL(SQL_ADD_AUTHOR_ID_COLUMN);
                db.execSQL(SQL_ADD_SWRL_ID_COLUMN);
            }
            if (oldVersion < 5) {
                db.execSQL(SQL_ADD_AUTHOR_AVATAR_URL_COLUMN);
            }
        }
    }
}
