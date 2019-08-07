package com.example.android.favoritephotos.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.favoritephotos.FavoritesActivity;
import com.example.android.favoritephotos.R;
import com.example.android.favoritephotos.interfaces.FavoritePhotoDeleteListener;
import com.example.android.favoritephotos.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private FavoritePhotoDeleteListener favoritePhotoDeleteListener;
    private boolean multiSelect = false;
    private ArrayList<String> selectedUrls = new ArrayList<>();

    private ActionMode.Callback actionModeCallbacks = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            multiSelect = true;
            menu.add("Delete");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (selectedUrls.size() > 0) {
                favoritePhotoDeleteListener.deletePhotos(selectedUrls);
            }
            actionMode.finish();

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            multiSelect = false;
            selectedUrls.clear();
            notifyDataSetChanged();
        }
    };


    public FavoritesAdapter(@NonNull Context context, FavoritePhotoDeleteListener listener) {
        mContext = context;
        favoritePhotoDeleteListener = listener;
    }


    public class FavoritesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView_photo)
        ImageView imageViewPhoto;

        @BindView(R.id.btn_favorite)
        ImageView imageViewFavorite;

        @BindView(R.id.selectView)
        View selectView;

        public FavoritesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void selectItem(String item) {
            if (multiSelect) {
                selectView.setVisibility(View.VISIBLE);
                if (selectedUrls.contains(item)) {
                    selectedUrls.remove(item);
                    selectView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    item = "\'"+item+"\'";
                    selectedUrls.add(item);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        selectView.setBackgroundColor(Color.argb(0.5f,211.0f/255,211.0f/255,211.0f/255));
                    }
                }
            } else {
                selectView.setVisibility(View.INVISIBLE);
            }
        }

        void update(final String value) {
            if (selectedUrls.contains(value)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    selectView.setBackgroundColor(Color.argb(0.5f,211.0f/255,211.0f/255,211.0f/255));
                }
            } else {
                selectView.setBackgroundColor(Color.TRANSPARENT);
            }
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    ((AppCompatActivity)view.getContext()).startSupportActionMode(actionModeCallbacks);
                    selectItem(value);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectItem(value);
                }
            });
        }
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View mView = layoutInflater.inflate(R.layout.photo_list_item, parent, false);
        final FavoritesViewHolder viewHolder = new FavoritesViewHolder(mView);
//        int position = viewHolder.getAdapterPosition();
//        mCursor.moveToPosition(position);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder favoritesViewHolder, int position) {
        mCursor.moveToPosition(position);

        final String url_m = mCursor.getString(FavoritesActivity.INDEX_FAVORITE_URL_M);
        final String internal_url = mCursor.getString(FavoritesActivity.INDEX_FAVORITE_INTERNAL_URL);
        int width = mCursor.getInt(FavoritesActivity.INDEX_FAVORITE_MEDIA_WIDTH);
        int height = mCursor.getInt(FavoritesActivity.INDEX_FAVORITE_MEDIA_HEIGHT);
        favoritesViewHolder.imageViewPhoto.getLayoutParams().width = width;
        favoritesViewHolder.imageViewPhoto.getLayoutParams().height = height;
        favoritesViewHolder.imageViewFavorite.setVisibility(View.INVISIBLE);
        Uri uri = Uri.parse(url_m);
        String imageName = uri.getLastPathSegment();

        favoritesViewHolder.imageViewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoritePhotoDeleteListener.onPhotoClick(internal_url, url_m);
            }
        });

        File internal_url_file = ImageUtils.getImageFileDownloaded(mContext, imageName,mContext.getString(R.string.image_directory));
        Picasso.get().load(internal_url_file).into(favoritesViewHolder.imageViewPhoto);
        favoritesViewHolder.update(url_m);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) {
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

}
