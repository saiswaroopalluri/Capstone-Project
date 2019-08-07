package com.example.android.favoritephotos.utils;

import com.google.android.gms.maps.model.LatLng;

public class FlickrConstants {

    // Flickr
    private static final double SearchBBoxHalfWidth = 1.0;
    private static final double SearchBBoxHalfHeight = 1.0;
    private static final double SearchLatRangeMin = -90.0;
    private static final double SearchLatRangeMax = 90.0;
    private static final double SearchLonRangeMin = -180.0;
    private static final double SearchLonRangeMax = 180.0;

    // Flickr Photos Parameter Keys
    public static final String KMethod = "method";
    public static final String KAPIKey = "api_key";
    public static final String KBoundingBox = "bbox";
    public static final String KSafeSearch = "safe_search";
    public static final String KExtras = "extras";
    public static final String KFormat = "format";
    public static final String KNoJSONCallback = "nojsoncallback";
    public static final String KPerPage = "per_page";
    public static final String KPage = "page";

    // Flickr Photos Parameter Values
    public static final String VSearchMethod = "flickr.photos.search";
    public static final String VAPIKey = "";
    public static final String VResponseFormat = "json";
    public static final String VDisableJSONCallback = "1";
    public static final String VMediumURL = "url_m";
    public static final String VUseSafeSearch = "1";
    public static final String VPerPage = "20";

    public static String bboxString(LatLng latLng) {
        double minimumLongitude = Math.max(latLng.longitude - SearchBBoxHalfWidth, SearchLonRangeMin);
        double minimumLatitude = Math.max(latLng.latitude - SearchBBoxHalfHeight, SearchLatRangeMin);
        double maximumLongitude = Math.max(latLng.longitude + SearchBBoxHalfWidth, SearchLonRangeMax);
        double maximumLatitude = Math.max(latLng.latitude + SearchBBoxHalfHeight, SearchLatRangeMax);

        StringBuilder bboxStringBuilder = new StringBuilder();
        bboxStringBuilder.append(minimumLongitude);
        bboxStringBuilder.append(",");
        bboxStringBuilder.append(minimumLatitude);
        bboxStringBuilder.append(",");
        bboxStringBuilder.append(maximumLongitude);
        bboxStringBuilder.append(",");
        bboxStringBuilder.append(maximumLatitude);

        return bboxStringBuilder.toString();
    }

}
