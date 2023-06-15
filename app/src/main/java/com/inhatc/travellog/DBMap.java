package com.inhatc.travellog;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBMap extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LocationDB";
    private static final String TABLE_NAME = "Locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CODE = "code";
    public DBMap(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_CODE + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }


    public void insertLocation(String name, String code) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CODE, code);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public boolean isLocationNameExists(String locationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isExists = false;

        try {
            String query = "SELECT * FROM locations WHERE name=?";
            cursor = db.rawQuery(query, new String[]{locationName});

            if (cursor != null && cursor.getCount() > 0) {
                isExists = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return isExists;
    }
}
