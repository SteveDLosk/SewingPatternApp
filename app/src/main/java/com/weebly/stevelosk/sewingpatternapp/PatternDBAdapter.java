package com.weebly.stevelosk.sewingpatternapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.text.Selection;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

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
    final static String BRAND = "brand";
    final static String PATTERN_NUMBER = "pattern_number";
    final static String SIZES = "sizes";
    final static String CONTENT = "content";
    final static String NOTES = "notes";
    final static String FRONT_IMAGE = "front_image";
    final static String BACK_IMAGE = "back_image";

    private static final String[] PATTERN_FIELDS = new String[] {
            KEY_ROWID, BRAND, PATTERN_NUMBER, SIZES, CONTENT, NOTES, FRONT_IMAGE, BACK_IMAGE
    };

    // SQL string to create the table
    private static final String CREATE_PATTERN_TABLE = "create table " + PATTERN_TABLE + "("
            + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + BRAND + " text not null," + PATTERN_NUMBER + " text,"
            + SIZES + " text," + CONTENT + " text, " + NOTES + " text, "
            + FRONT_IMAGE + " BLOB, " + BACK_IMAGE + " BLOB"
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
        p.setFrontImgBytes(cursor.getBlob(cursor.getColumnIndex(FRONT_IMAGE)));
        p.setBackImgBytes(cursor.getBlob(cursor.getColumnIndex(BACK_IMAGE)));

        return p;
    }

    public static byte[] bitmapToByteArray2 (Bitmap bitmap) {

        // from  https://android--code.blogspot.com/2015/09/android-how-to-convert-bitmap-to-byte.html
        // Initializing a new ByteArrayOutputStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                    /*
                        public boolean compress (Bitmap.CompressFormat format, int quality, OutputStream stream)
                            Write a compressed version of the bitmap to the specified outputstream.
                            If this returns true, the bitmap can be reconstructed by passing a
                            corresponding inputstream to BitmapFactory.decodeStream().

                            Note: not all Formats support all bitmap configs directly, so it is
                            possible that the returned bitmap from BitmapFactory could be in
                            a different bitdepth, and/or may have lost per-pixel alpha
                            (e.g. JPEG only supports opaque pixels).

                            Parameters
                            format : The format of the compressed image
                            quality : Hint to the compressor, 0-100. 0 meaning compress for small
                                size, 100 meaning compress for max quality. Some formats,
                                like PNG which is lossless, will ignore the quality setting
                            stream : The outputstream to write the compressed data.

                            Returns
                                true if successfully compressed to the specified stream.
                    */

        // Compress the bitmap to jpeg format and 50% image quality
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,stream);

        // Create a byte array from ByteArrayOutputStream
        byte[] byteArray = stream.toByteArray();
        return byteArray;

    }
    public static byte[] bitmapToByeArray (Bitmap bitmap ) {

            int bufferSize = bitmap.getByteCount();

            //allocate new instances which will hold bitmap
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            byte[] bytes = new byte[bufferSize];

            //copy the bitmap's pixels into the specified buffer
            bitmap.copyPixelsToBuffer(buffer);

            //rewinds buffer (buffer position is set to zero and the mark is discarded)
            buffer.rewind();

            //transfer bytes from buffer into the given destination array
            buffer.get(bytes);

            //return bitmap's pixels
            return bytes;
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
