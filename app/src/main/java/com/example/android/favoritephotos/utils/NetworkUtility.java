package com.example.android.favoritephotos.utils;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtility {

    private static final String TAG = NetworkUtility.class.getSimpleName();

    private static final String FLICKR_SCHEME = "https";
    private static final String FLICKR_BASE_URL = "api.flickr.com";
    private static final String FLICKR_APPENDED_PATH = "/services/rest";


    public static URL buildFlickrUrl(int pageNumber, LatLng latLng) {
        Uri.Builder builder = new Uri.Builder();

        Uri uri = builder.scheme(FLICKR_SCHEME)
                .authority(FLICKR_BASE_URL)
                .appendEncodedPath(FLICKR_APPENDED_PATH)
                .appendQueryParameter(FlickrConstants.KMethod, FlickrConstants.VSearchMethod)
                .appendQueryParameter(FlickrConstants.KAPIKey, FlickrConstants.VAPIKey)
                .appendQueryParameter(FlickrConstants.KSafeSearch, FlickrConstants.VUseSafeSearch)
                .appendQueryParameter(FlickrConstants.KExtras, FlickrConstants.VMediumURL)
                .appendQueryParameter(FlickrConstants.KFormat, FlickrConstants.VResponseFormat)
                .appendQueryParameter(FlickrConstants.KNoJSONCallback, FlickrConstants.VDisableJSONCallback)
                .appendQueryParameter(FlickrConstants.KPerPage, FlickrConstants.VPerPage)
                .appendQueryParameter(FlickrConstants.KPage, String.valueOf(pageNumber))
                .appendQueryParameter(FlickrConstants.KBoundingBox,FlickrConstants.bboxString(latLng))
                .build();

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d(TAG, "Built URI " + url);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Log.v("input stream", in.toString());

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
