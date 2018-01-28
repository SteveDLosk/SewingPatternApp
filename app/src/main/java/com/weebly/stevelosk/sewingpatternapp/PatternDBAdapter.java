package com.weebly.stevelosk.sewingpatternapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Selection;
import android.util.Log;

/**
 * Created by steve on 1/27/2018.
 */

public class PatternDBAdapter {

    private static final String DATABASE_NAME = "PATTERNS_DATABASE.db";
    private static final String PATTERN_TABLE = "PATTERN_TABLE";
    private static final int DATABASE_VERSION = 1;
    private final Context myContext;
    static String TAG = "SDL, PatternDBAdapter";

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    private final static String KEY_ROWID = "_id";
    private final static String BRAND = "brand";
    private final static String PATTERN_NUMBER = "pattern_number";
    private final static String SIZES = "sizes";
    private final static String CONTENT = "content";
    private final static String NOTES = "notes";
    // TODO: Add BLOBs (pictures)

    private static final String[] PATTERN_FIELDS = new String[] {
            KEY_ROWID, BRAND, PATTERN_NUMBER, SIZES, CONTENT, NOTES
    };

    // SQL string to create the table
    private static final String CREATE_PATTERN_TABLE = "create table " + PATTERN_TABLE + "("
            + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + BRAND + " text not null," + PATTERN_NUMBER + " text,"
            + SIZES + " text," + CONTENT + " text, " + NOTES + " text"
            + ");";

    PatternDBAdapter(Context context) {
        this.myContext = context;
    }

    PatternDBAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(myContext);
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    void close() {
        if (myDBHelper != null) {
            myDBHelper.close();
        }
    }

    // SQL DML methods
    long insertPattern(ContentValues values) {
        return db.insertWithOnConflict(PATTERN_TABLE, null,
                values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public boolean updatePattern(int id, ContentValues newValues) {
        String[] selectionArgs = {String.valueOf(id)};
        return db.update(PATTERN_TABLE, newValues, KEY_ROWID + "=?",
                selectionArgs ) > 0;
    }

    public boolean deletePattern(int id) {
        String[] selectionArgs = {String.valueOf(id) };
        return db.delete(PATTERN_TABLE, KEY_ROWID + "=?", selectionArgs ) > 0;
    }

    // SQL Query methods

    Cursor getAllPatterns() {
        return db.query(PATTERN_TABLE, PATTERN_FIELDS, null, null,
                null, null, null);
    }

    Cursor getPatternByID(String[] id) {
        return db.query(PATTERN_TABLE, PATTERN_FIELDS, PATTERN_NUMBER + " =? ", id,
                null, null, null);
    }

    static Pattern getPatternFromCursor(Cursor cursor) {
        Pattern p = new Pattern();
        p.setPatternId(cursor.getInt(cursor.getColumnIndex(KEY_ROWID)));
        p.setBrand(cursor.getString(cursor.getColumnIndex(BRAND)));
        p.setPatternNumber(cursor.getString(cursor.getColumnIndex(PATTERN_NUMBER)));
        p.setSizes(cursor.getString(cursor.getColumnIndex(SIZES)));
        p.setContent(cursor.getString(cursor.getColumnIndex(CONTENT)));
        p.setNotes(cursor.getString(cursor.getColumnIndex(NOTES)));
        // TODO: get BLOBs
        return p;
    }



    public void upgrade(int version) throws SQLException {
        myDBHelper = new DatabaseHelper(myContext);
        db = myDBHelper.getWritableDatabase();
        myDBHelper.onUpgrade(db, 1, version);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
          sqLiteDatabase.execSQL(CREATE_PATTERN_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
            /*
             *  Destroys old table, and calls a new onCreate()
             */

            Log.w(TAG, "Upgrading database from version " + oldVer + " to " + newVer);
            Log.w(TAG, "This will DROP TABLE!");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PATTERN_TABLE);
            onCreate(sqLiteDatabase);
        }
    }
}
