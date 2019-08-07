package com.example.android.favoritephotos.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.favoritephotos.R;
import com.example.android.favoritephotos.data.FavoritePhotosContract;
import com.example.android.favoritephotos.utils.ImageUtils;

import java.io.File;

public class FavoritesGridWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.i("Favorites Service", "entered");
        return new FavoritesGridRemoteViewsFactory(getApplicationContext());
    }
}

class FavoritesGridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Cursor mCursor;

    public FavoritesGridRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    private void initCursor() {
        Uri favoritesUri = FavoritePhotosContract.FavoritePhotosEntry.CONTENT_URI;
        String sortOrder = FavoritePhotosContract.FavoritePhotosEntry.COLUMN_CREATED_AT + " DESC";
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = mContext.getContentResolver().query(favoritesUri, null, null, null, null);

    }

    @Override
    public void onCreate() {
        initCursor();
    }

    @Override
    public void onDataSetChanged() {
        initCursor();
    }

    @Override
    public void onDestroy() {
        mCursor.close();
    }

    @Override
    public int getCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (mCursor == null || mCursor.getCount() == 0) {
            return null;
        }

        mCursor.moveToPosition(position);
        int media_url_index = mCursor.getColumnIndex(FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL);

        String media_url_string = mCursor.getString(media_url_index);

        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.favorite_photo_widget);
        Uri uri = Uri.parse(media_url_string);
        String imageName = uri.getLastPathSegment();

        final File internal_url = ImageUtils.getImageFileDownloaded(mContext, imageName, mContext.getString(R.string.image_directory));
        Bitmap bitmap = BitmapFactory.decodeFile(internal_url.getAbsolutePath());
        remoteViews.setImageViewBitmap(R.id.widget_favorite_photo, bitmap);


        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
