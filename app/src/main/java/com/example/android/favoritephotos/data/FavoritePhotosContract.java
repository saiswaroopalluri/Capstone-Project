package com.example.android.favoritephotos.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritePhotosContract {

    public static final String AUTHORITY = "com.example.android.favoritephotos";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_PINS = "pins";
    public static final String PATH_FAVORITE_PHOTOS = "favorites";


    public static final class PinsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                                                .buildUpon()
                                                .appendPath(PATH_PINS)
                                                .build();

        public static final String TABLE_NAME = "pins";
        public static final String COLUMN_PIN_LATITUDE = "pins_latitude";
        public static final String COLUMN_PIN_LONGITUDE = "pins_longitude";

    }


    public static final class FavoritePhotosEntry implements BaseColumns {

        public static final  Uri CONTENT_URI = BASE_CONTENT_URI
                                                .buildUpon()
                                                .appendPath(PATH_FAVORITE_PHOTOS)
                                                .build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MEDIA_URL = "media_url";
        public static final String COLUMN_MEDIA_URL_INTERNAL = "media_url_internal";
        public static final String COLUMN_MEDIA_WIDTH = "media_width";
        public static final String COLUMN_MEDIA_HEIGHT = "media_height";
        public static final String COLUMN_CREATED_AT = "created_at";
    }



}
