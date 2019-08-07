package com.example.android.favoritephotos.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.favoritephotos.DetailActivity;
import com.example.android.favoritephotos.R;
import com.example.android.favoritephotos.adapters.PhotosAdapter;
import com.example.android.favoritephotos.data.FavoritePhotosContract;
import com.example.android.favoritephotos.interfaces.PhotoItemClickListener;
import com.example.android.favoritephotos.models.FlickrPhoto;
import com.example.android.favoritephotos.network.PhotosLoaderCallbacks;
import com.example.android.favoritephotos.utils.JsonUtils;
import com.example.android.favoritephotos.utils.NetworkUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhotosFragment extends Fragment implements PhotosLoaderCallbacks.PhotosLoaderListener, OnMapReadyCallback {

    private static final int ID_PHOTOS_DATALOADER = 111;

    private static final String INTENT_MARKER_FLICKR_PHOTO = "intent_marker_flickr_photo";
    private static final String INTENT_MARKER_FLICKR_URL = "flickr_url";

    private static final String PAGE_NUMBER = "page_number";

    private List<FlickrPhoto> photosList;
    private GoogleMap mMap;
    private LatLng latLng;
    private int pageNumber;
    private PhotosAdapter photosAdapter;

    @BindView(R.id.mapView)
    MapView mapView;

    @BindView(R.id.recyclerView_photos)
    RecyclerView recyclerViewPhotos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Context context = getContext();
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);
        ButterKnife.bind(this, rootView);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        recyclerViewPhotos.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.span_count), StaggeredGridLayoutManager.VERTICAL);
        recyclerViewPhotos.setLayoutManager(staggeredGridLayoutManager);

        if (savedInstanceState == null) {
            pageNumber = 1;
        } else {
            pageNumber = savedInstanceState.getInt(PAGE_NUMBER, 1);
        }

        getPhotos();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions()
                .position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setScrollGesturesEnabled(false);
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PAGE_NUMBER, pageNumber);
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void refreshNewContent() {
        pageNumber = pageNumber+1;
        getPhotos();
    }

    private void getPhotos() {
        URL photosURL = NetworkUtility.buildFlickrUrl(pageNumber, latLng);
        PhotosLoaderCallbacks photosLoaderCallbacks = new PhotosLoaderCallbacks(getContext(), photosURL, this);
        try {
            getActivity().getSupportLoaderManager().initLoader(ID_PHOTOS_DATALOADER, null, photosLoaderCallbacks).forceLoad();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPhotos() {
        if (photosList == null) {
            return;
        }

        if (photosAdapter == null) {
            photosAdapter = new PhotosAdapter(getContext(), new PhotoItemClickListener() {
                @Override
                public void onPhotoClick(FlickrPhoto photo) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra(INTENT_MARKER_FLICKR_PHOTO, photo.getUrl_m());
                    intent.putExtra(INTENT_MARKER_FLICKR_URL, photo.getUrl_m());
                    startActivity(intent);
                }

                @Override
                public void onFavoriteClick(FlickrPhoto flickrPhoto, String internalMediaUrl, boolean isFavorite) {
                    ContentResolver contentResolver = getContext().getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL, flickrPhoto.getUrl_m());
                    contentValues.put(FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_URL_INTERNAL, internalMediaUrl);
                    contentValues.put(FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_WIDTH, flickrPhoto.getWidth_m());
                    contentValues.put(FavoritePhotosContract.FavoritePhotosEntry.COLUMN_MEDIA_HEIGHT, flickrPhoto.getHeight_m());
                    if (isFavorite) {
                        Uri mUri = FavoritePhotosContract.FavoritePhotosEntry.CONTENT_URI
                                .buildUpon()
                                .appendPath(flickrPhoto.getUrl_m())
                                .build();
                        int deletedID = contentResolver.delete(mUri, null, null);
                        if(deletedID != 0) {
                            Toast.makeText(getContext(), Integer.toString(deletedID), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Uri mUri = FavoritePhotosContract.FavoritePhotosEntry.CONTENT_URI;
                        Uri insertedUri = contentResolver.insert(mUri, contentValues);
                        if(insertedUri != null) {
                            Toast.makeText(getContext(), insertedUri.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
            recyclerViewPhotos.setAdapter(photosAdapter);
        }


        photosAdapter.swapData(photosList);
    }


    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(String jsonString) {
        try {
            getActivity().getSupportLoaderManager().destroyLoader(ID_PHOTOS_DATALOADER);
            photosList = JsonUtils.getFlickrPhotos(jsonString);
            Log.d("Photos ", "onPostExecute: "+ photosList.toString());
            loadPhotos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
