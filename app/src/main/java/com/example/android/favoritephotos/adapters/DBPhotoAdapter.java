package com.example.android.favoritephotos.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.favoritephotos.data.FavoritePhotosContract;
import com.example.android.favoritephotos.data.FavoritePhotosDBHelper;

public class DBPhotoAdapter {
    private Context mContext;
    private SQLiteDatabase db;
    private FavoritePhotosDBHelper dbHelper;

    public DBPhotoAdapter(Context context) {
        mContext = context;
        dbHelper = new FavoritePhotosDBHelper(context);
    }

    //Open DB Writable
    private void openWritableDB() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Open DB Readable
    private void openReadableDB() {
        try {
            db = dbHelper.getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDB() {
        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isFavorite(String photoURL) {
        openReadableDB();
        String[] selectionArguments = new String[]{photoURL};
        Cursor cursor = db.query(FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME, null, FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " = ?", selectionArguments, null, null, null);

        return (cursor != null && cursor.moveToFirst());
    }

}
