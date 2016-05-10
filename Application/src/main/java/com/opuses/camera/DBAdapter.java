package com.opuses.camera;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressWarnings("UnusedReturnValue")
class DBAdapter {

    /////////////////////////////////////////////////////////////////////
    //	Constants & Data
    /////////////////////////////////////////////////////////////////////
    // For logging:
    private static final String TAG     = "DBAdapter";

    // Track DB version if a new version of your app changes the format.
    private static final int DATABASE_VERSION    = 3;

    // DB name and Table names
    private static final String DATABASE_NAME    = "myDb";
    private static final String TABLE_setting    = "SettingTable";
    private static final String TABLE_burst      = "BurstTable";

    // DB Fields for table - camera settings
    // common one
    private static final String KEY_ROWID    = "_id";

    // DB table for single setting
    public static final String KEY_NAME_SETTING     = "settingName";
    public static final String KEY_FOCUS            = "focus";
    public static final String KEY_EXPOSURE         = "exposure";
    public static final String KEY_ISO              = "iso";
    public static final String KEY_FLASH            = "flash";

    // table create statement
    // table for camera setting 
    private static final String CREATE_TABLE_setting =
            "create table " + TABLE_setting
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_NAME_SETTING + " text not null, "
                    + KEY_FOCUS + " integer not null, "
                    + KEY_EXPOSURE + " integer not null, "
                    + KEY_ISO + " integer not null,"
                    + KEY_FLASH + " integer not null"
                    + ");";


    private static final String[] ALL_KEYS_setting =
            new String[]{KEY_ROWID, KEY_NAME_SETTING, KEY_FOCUS, KEY_EXPOSURE, KEY_ISO, KEY_FLASH};

    private final DatabaseHelper mDBHelper;
    private SQLiteDatabase mDB;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    /**
     * general functions
     */
    public DBAdapter(Context ctx) {
        mDBHelper = new DatabaseHelper(ctx);
    }

    // Open the database connection.
    public void open() {
        mDB = mDBHelper.getWritableDatabase();
    }

    // Close the database connection.
    public void close() {
        mDBHelper.close();
    }

    /***********************************************************************************************
     * functions for TABLE_setting
     **********************************************************************************************/
    // Add a new set of values to the database.
    public long insertRow_setting(String name, int focus, int exposure, int iso, int flash) {
        // Create row's data:
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME_SETTING, name);
        initialValues.put(KEY_FOCUS, focus);
        initialValues.put(KEY_EXPOSURE, exposure);
        initialValues.put(KEY_ISO, iso);
        initialValues.put(KEY_FLASH, flash);

        // Insert it into the database.
        return mDB.insert(TABLE_setting, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    private boolean deleteRow_setting(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return mDB.delete(TABLE_setting, where, null) != 0;
    }

    public void deleteAll_setting() {
        Cursor c = getAllRows_setting();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow_setting(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows_setting() {
        Cursor c = mDB.query(true, TABLE_setting, ALL_KEYS_setting,
                null, null, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_setting);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_setting);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_burst);

            // Recreate new database:
            onCreate(db);
        }
    }
}

