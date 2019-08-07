package com.example.android.favoritephotos;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.android.favoritephotos.adapters.FavoritesAdapter;
import com.example.android.favoritephotos.data.FavoritePhotosContract;
import com.example.android.favoritephotos.interfaces.FavoritePhotoDeleteListener;
import com.example.android.favoritephotos.widget.FavoritePhotosWidgetProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, FavoritePhotoDeleteListener {

    private static final String INTENT_MARKER_FLICKR_PHOTO = "intent_marker_flickr_photo";
    private static final String INTENT_MARKER_FLICKR_URL = "flickr_url";
    private static final String IS_INTERNAL = "isInternal";

    public static final int INDEX_FAVORITE_URL_M = 1;
    public static final int INDEX_FAVORITE_INTERNAL_URL = 2;
    public static final int INDEX_FAVORITE_MEDIA_WIDTH = 3;
    public static final int INDEX_FAVORITE_MEDIA_HEIGHT = 4;


    public static final String[] FAVORITES_PROJECTION = {
            FavoritePhotosContract.FavoritePhotosEntry.COLUMN_CREATED_AT,
            FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL,
            FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL_INTERNAL,
            FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_WIDTH,
            FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_HEIGHT
    };

    private static final int ID_FAVORITES_LOADER = 44;

    private FavoritesAdapter mFavoritesAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    @BindView(R.id.recyclerView_favorites)
    RecyclerView recyclerViewFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);

        recyclerViewFavorites.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewFavorites.setLayoutManager(staggeredGridLayoutManager);

        mFavoritesAdapter = new FavoritesAdapter(this, this);
        recyclerViewFavorites.setAdapter(mFavoritesAdapter);
        getSupportLoaderManager().initLoader(ID_FAVORITES_LOADER, null, this).forceLoad();

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        switch (loaderId) {

            case ID_FAVORITES_LOADER:
                Uri favoritesUri = FavoritePhotosContract.FavoritePhotosEntry.CONTENT_URI;
                String sortOrder = FavoritePhotosContract.FavoritePhotosEntry.COLUMN_CREATED_AT + " DESC";

                return new CursorLoader(this,
                        favoritesUri,
                        FAVORITES_PROJECTION,
                        null,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mFavoritesAdapter.swapCursor(cursor);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        recyclerViewFavorites.smoothScrollToPosition(mPosition);
        setupAppWidget();


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mFavoritesAdapter.swapCursor(null);
    }

    private void setupAppWidget() {
        FavoritePhotosWidgetProvider.sendRefreshBroadcast(FavoritesActivity.this);
    }


    @Override
    public void deletePhotos(List<String> selectedUrls) {
        String selectedUrlsString = TextUtils.join(",",selectedUrls);
        Uri mUri = FavoritePhotosContract.FavoritePhotosEntry.CONTENT_URI;
        int deletedID = getContentResolver().delete(mUri, FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL + " IN ("+ selectedUrlsString+")",null);
        if(deletedID != 0) {
            Toast.makeText(this, Integer.toString(deletedID), Toast.LENGTH_LONG).show();
            getSupportLoaderManager().getLoader(ID_FAVORITES_LOADER).forceLoad();
        }
    }

    @Override
    public void onPhotoClick(String photoUrl, String flickrUrl) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(INTENT_MARKER_FLICKR_PHOTO, photoUrl);
        intent.putExtra(INTENT_MARKER_FLICKR_URL, flickrUrl);
        intent.putExtra(IS_INTERNAL, true);
        startActivity(intent);
    }
}
