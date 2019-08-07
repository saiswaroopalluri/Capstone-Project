package com.example.android.favoritephotos.interfaces;

import com.example.android.favoritephotos.models.FlickrPhoto;

public interface PhotoItemClickListener {
    void onPhotoClick(FlickrPhoto photo);
    void onFavoriteClick(FlickrPhoto flickrPhoto, String internalMediaUrl ,boolean isFavorite);
}
