package com.example.android.favoritephotos.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritePhotosProvider extends ContentProvider {

    public static final int PINS = 100;
    public static final int PIN_WITH_ID = 101;
    public static final int FAV_PHOTOS = 200;
    public static final int FAV_PHOTOS_WITH_ID = 201;


    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoritePhotosContract.AUTHORITY, FavoritePhotosContract.PATH_PINS, PINS);
        uriMatcher.addURI(FavoritePhotosContract.AUTHORITY, FavoritePhotosContract.PATH_PINS + "/#", PIN_WITH_ID);
        uriMatcher.addURI(FavoritePhotosContract.AUTHORITY, FavoritePhotosContract.PATH_FAVORITE_PHOTOS, FAV_PHOTOS);
        uriMatcher.addURI(FavoritePhotosContract.AUTHORITY, FavoritePhotosContract.PATH_FAVORITE_PHOTOS + "/*", FAV_PHOTOS_WITH_ID);

        return uriMatcher;
    }

    private FavoritePhotosDBHelper mFavoritePhotosDBHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavoritePhotosDBHelper = new FavoritePhotosDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case PIN_WITH_ID: {
                String pinID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{pinID};
                cursor = mFavoritePhotosDBHelper.getReadableDatabase().query(
                        FavoritePhotosContract.PinsEntry.TABLE_NAME,
                        projection,
                        FavoritePhotosContract.PinsEntry._ID + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case PINS: {
                cursor = mFavoritePhotosDBHelper.getReadableDatabase().query(
                        FavoritePhotosContract.PinsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case FAV_PHOTOS : {
                cursor = mFavoritePhotosDBHelper.getReadableDatabase().query(
                        FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

                break;
            }
            case FAV_PHOTOS_WITH_ID : {
                String photoID = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{photoID};
                cursor = mFavoritePhotosDBHelper.getReadableDatabase().query(
                        FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME,
                        projection,
                        FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Favorite Photos");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = mFavoritePhotosDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case PINS: {
                long id = db.insert(FavoritePhotosContract.PinsEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoritePhotosContract.PinsEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert pin into " + uri);
                }
                break;
            }
            case FAV_PHOTOS: {
                long id = db.insert(FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoritePhotosContract.FavoritePhotosEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert favorite into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String whereClause, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoritePhotosDBHelper.getWritableDatabase();
        int deletedRows;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PIN_WITH_ID: {
                String pinID = uri.getPathSegments().get(1);
                deletedRows = db.delete(FavoritePhotosContract.PinsEntry.TABLE_NAME, FavoritePhotosContract.PinsEntry._ID + "=?", new String[]{pinID});
                break;
            }
            case FAV_PHOTOS_WITH_ID: {
                String favoritePhotoID = uri.getPathSegments().get(1);
                deletedRows = db.delete(FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME, FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + "=?", new String[]{favoritePhotoID});
                break;
            }
            case FAV_PHOTOS : {
                deletedRows = db.delete(FavoritePhotosContract.FavoritePhotosEntry.TABLE_NAME, whereClause, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException("We are not implementing update in favorite photos");
    }

    @Override
    public void shutdown() {
        mFavoritePhotosDBHelper.close();
        super.shutdown();
    }
}
