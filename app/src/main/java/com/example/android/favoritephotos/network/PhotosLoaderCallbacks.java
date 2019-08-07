package com.example.android.favoritephotos.network;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.net.URL;

public class PhotosLoaderCallbacks implements LoaderManager.LoaderCallbacks<String> {


    private Context context;
    private URL url;
    private PhotosLoaderListener photosLoaderListener;


    public interface PhotosLoaderListener {
        void onPreExecute();
        void onPostExecute(String jsonString);
    }

    public PhotosLoaderCallbacks(Context context, URL url, PhotosLoaderListener photosLoaderListener) {
        this.context = context;
        this.url = url;
        this.photosLoaderListener = photosLoaderListener;
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle bundle) {
        photosLoaderListener.onPreExecute();
        return new PhotosAsyncTaskLoader(context, url);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        photosLoaderListener.onPostExecute(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
