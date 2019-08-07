package com.example.android.favoritephotos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoritePhotosDBHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "favoritePhotosDB.db";

    // If you change the database schema, increment the database version
    private static final int VERSION = 4;

    public FavoritePhotosDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE_PINS = "CREATE TABLE "  + FavoritePhotosContract.PinsEntry.TABLE_NAME + " (" +
                FavoritePhotosContract.PinsEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritePhotosContract.PinsEntry.COLUMN_PIN_LATITUDE + " DOUBLE NOT NULL, " +
                FavoritePhotosContract.PinsEntry.COLUMN_PIN_LONGITUDE + " DOUBLE NOT NULL);";

        db.execSQL(CREATE_TABLE_PINS);

        final String CREATE_TABLE_FAV_PHOTOS = "CREATE TABLE "  + FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME + " (" +
                FavoritePhotosContract.FavoritePhotosEntry._ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " TEXT NOT NULL, " +
                FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL_INTERNAL + " TEXT NOT NULL, " +
                FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_WIDTH + " INTEGER NOT NULL, " +
                FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_HEIGHT + " INTEGER NOT NULL, " +
                FavoritePhotosContract.FavoritePhotosEntry.COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

        db.execSQL(CREATE_TABLE_FAV_PHOTOS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritePhotosContract.PinsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME);
        onCreate(db);
    }
}
