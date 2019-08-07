package com.example.android.favoritephotos.interfaces;

import java.util.List;

public interface FavoritePhotoDeleteListener {

    void deletePhotos(List<String> selectedUrls);
    void onPhotoClick(String photoUrl, String flickrUrl);

}
