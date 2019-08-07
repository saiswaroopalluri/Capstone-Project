package com.example.android.favoritephotos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetailActivity extends AppCompatActivity {

    private static final String INTENT_MARKER_FLICKR_PHOTO = "intent_marker_flickr_photo";
    private static final String INTENT_MARKER_FLICKR_URL = "flickr_url";
    private static final String IS_INTERNAL = "isInternal";

    private String photoUrl;
    private String flickrPhotoUrl;
    private boolean isInternal = false;

    @BindView(R.id.imageView_photo_detail)
    ImageView imageViewPhoto;

    @BindView(R.id.btnShare)
    FloatingActionButton btnShare;

    @BindView(R.id.adView)
    PublisherAdView mPublisherAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Intent intentCalled = getIntent();
        if (intentCalled != null) {
            photoUrl = intentCalled.getStringExtra(INTENT_MARKER_FLICKR_PHOTO);
            flickrPhotoUrl = intentCalled.getStringExtra(INTENT_MARKER_FLICKR_URL);
            if (intentCalled.hasExtra(IS_INTERNAL)) {
                isInternal = true;
                File filePath = new File(photoUrl);
                Picasso.get().load(filePath).into(imageViewPhoto);
            } else {
                Picasso.get().load(photoUrl).into(imageViewPhoto);
            }
        }
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

    }

    @OnClick(R.id.btnShare)
    void btnShareAction(View view) {
        Intent myShareIntent;
        myShareIntent = new Intent(Intent.ACTION_SEND);
        myShareIntent.setType("image/*");
        myShareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(flickrPhotoUrl));
        startActivity(Intent.createChooser(myShareIntent, "Share Image"));
    }

}
