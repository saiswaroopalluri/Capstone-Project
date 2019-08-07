package com.example.android.favoritephotos.utils;

import com.example.android.favoritephotos.models.FlickrPhoto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class JsonUtils {

    public static List<FlickrPhoto> getFlickrPhotos(String flickrJSONString) throws JSONException {
        final String FLICKR_RESULTS = "photos";
        final String FLICKR_PHOTO = "photo";

        final String FLICKR_ID = "id";
        final String FLICKR_OWNER = "owner";
        final String FLICKR_SECRET = "secret";
        final String FLICKR_SERVER = "server";
        final String FLICKR_FARM = "farm";
        final String FLICKR_TITLE = "title";
        final String FLICKR_IS_PUBLIC = "ispublic";
        final String FLICKR_IS_FRIEND = "isfriend";
        final String FLICKR_IS_FAMILY = "isfriend";
        final String FLICKR_URL_M = "url_m";
        final String FLICKR_HEIGHT_M = "height_m";
        final String FLICKR_WIDTH_M = "width_m";

        /* Flickr Photos array to hold photos data. */
        ArrayList<FlickrPhoto> flickrPhotoArrayList = new ArrayList<>();

        JSONObject flickrJSON = new JSONObject(flickrJSONString);
        JSONObject photosJSON = flickrJSON.getJSONObject(FLICKR_RESULTS);
        JSONArray resultsArray = photosJSON.getJSONArray(FLICKR_PHOTO);

        for (int i = 0; i < resultsArray.length(); i++) {
            String id, owner, secret, server, title, url_m;
            int farm, isPublic, isFriend, isFamily, height_m, width_m;

            JSONObject photoJSONObject = resultsArray.getJSONObject(i);
            id = photoJSONObject.getString(FLICKR_ID);
            owner = photoJSONObject.getString(FLICKR_OWNER);
            secret = photoJSONObject.getString(FLICKR_SECRET);
            server = photoJSONObject.getString(FLICKR_SERVER);
            farm = photoJSONObject.getInt(FLICKR_FARM);
            title = photoJSONObject.getString(FLICKR_TITLE);
            isPublic = photoJSONObject.getInt(FLICKR_IS_PUBLIC);
            isFriend = photoJSONObject.getInt(FLICKR_IS_FRIEND);
            isFamily = photoJSONObject.getInt(FLICKR_IS_FAMILY);
            url_m = photoJSONObject.getString(FLICKR_URL_M);
            height_m = photoJSONObject.getInt(FLICKR_HEIGHT_M);
            width_m = photoJSONObject.getInt(FLICKR_WIDTH_M);

            FlickrPhoto flickrPhoto = new  FlickrPhoto(id, owner, secret, server, farm, title, isPublic, isFriend, isFamily, url_m, height_m, width_m);

            flickrPhotoArrayList.add(flickrPhoto);
        }

        return flickrPhotoArrayList;
    }

}
