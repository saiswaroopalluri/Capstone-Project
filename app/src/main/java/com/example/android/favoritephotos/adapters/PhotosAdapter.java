package com.example.android.favoritephotos.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.favoritephotos.R;
import com.example.android.favoritephotos.interfaces.PhotoItemClickListener;
import com.example.android.favoritephotos.models.FlickrPhoto;
import com.example.android.favoritephotos.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private Context mContext;
    private List<FlickrPhoto> mFlickrPhotosList;
    private PhotoItemClickListener photoItemClickListener;

    public PhotosAdapter(Context context, PhotoItemClickListener photoClickListener) {
        mContext = context;
        photoItemClickListener = photoClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView_photo)
        ImageView imageViewPhoto;

        @BindView(R.id.btn_favorite)
        ImageView imageViewFavorite;

        @BindView(R.id.card_view)
        CardView cardView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View mView = layoutInflater.inflate(R.layout.photo_list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoItemClickListener.onPhotoClick(mFlickrPhotosList.get(viewHolder.getAdapterPosition()));
            }
        });

        viewHolder.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlickrPhoto flickrPhoto = mFlickrPhotosList.get(viewHolder.getAdapterPosition());
                DBPhotoAdapter dbPhotoAdapter = new DBPhotoAdapter(mContext);
                boolean isFavorite = dbPhotoAdapter.isFavorite(flickrPhoto.getUrl_m());
                changeFavoriteImage(viewHolder, !isFavorite);
                String imageFile= "";
                if (!isFavorite) {
                    Uri uri = Uri.parse(flickrPhoto.getUrl_m());
                    String imageName = uri.getLastPathSegment();
                    ContextWrapper contextWrapper = new ContextWrapper(mContext);
                    File directory = contextWrapper.getDir(mContext.getString(R.string.image_directory), Context.MODE_PRIVATE);
                    imageFile = new File(directory, imageName).getAbsolutePath();

                    Picasso.get().load(flickrPhoto.getUrl_m()).into(ImageUtils.storePicassoImageTarget(mContext, mContext.getString(R.string.image_directory), imageName));
                } else {
                    ImageUtils.deleteFileDownloaded(mContext, flickrPhoto.getUrl_m(), mContext.getString(R.string.image_directory));
                }


                photoItemClickListener.onFavoriteClick(flickrPhoto, imageFile, isFavorite);
                dbPhotoAdapter.closeDB();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FlickrPhoto flickrPhoto = mFlickrPhotosList.get(position);
        viewHolder.imageViewPhoto.getLayoutParams().width = flickrPhoto.getWidth_m();
        viewHolder.imageViewPhoto.getLayoutParams().height = flickrPhoto.getHeight_m();
        Picasso.get().load(flickrPhoto.getUrl_m()).into(viewHolder.imageViewPhoto);
        DBPhotoAdapter dbPhotoAdapter = new DBPhotoAdapter(mContext);
//        Log.d("Favorite:",String.valueOf(dbPhotoAdapter.isFavorite(flickrPhoto.getUrl_m())));
        boolean isFavorite = dbPhotoAdapter.isFavorite(flickrPhoto.getUrl_m());
        changeFavoriteImage(viewHolder, isFavorite);
        dbPhotoAdapter.closeDB();
    }

    private void changeFavoriteImage(@NonNull ViewHolder viewHolder, boolean isFavorite) {
        int favoriteResource;
        if (isFavorite) {
            favoriteResource = R.drawable.ic_favorite;
        } else {
            favoriteResource = R.drawable.ic_unfavorite;
        }
        viewHolder.imageViewFavorite.setImageResource(favoriteResource);
    }

    @Override
    public int getItemCount() {
        return mFlickrPhotosList.size();
    }

    public void swapData(List<FlickrPhoto> photos) {
        mFlickrPhotosList = photos;
        notifyDataSetChanged();
    }

}
