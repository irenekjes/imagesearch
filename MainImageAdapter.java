package com.example.user.imagesearch;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MainImageAdapter.class.getSimpleName();

    private Activity mActivity = null;
    private List<SearchResult> mItemList = null;
    private List<SearchResult> mSelectedList = new ArrayList<>();
    private boolean mIsSearch = true;

    public MainImageAdapter(Activity activity, List<SearchResult> itemList, boolean isSearch) {
        this.mActivity = activity;
        this.mItemList = itemList;
        this.mIsSearch = isSearch;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_image, parent, false);
        return new MainImageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MainImageAdapter.ViewHolder mHolder = (MainImageAdapter.ViewHolder) holder;

        final SearchResult item = mItemList.get(position);
        final String thumbUri = item.getThumbUrl();
        final String photoUri = item.getPhotoUrl();

        Glide.with(mActivity).load(thumbUri).into(mHolder.mImageView);

        if(!mIsSearch) {
            mHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, ImageActivity.class);
                    intent.putExtra("photo", photoUri);
                    mActivity.startActivity(intent);
                }
            });
        } else {
            mHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectedList.add(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void changeItemList(List<SearchResult> list) {
        mItemList = list;
        notifyDataSetChanged();
    }

    public void addItemList(List<SearchResult> list) {
        mItemList.addAll(list);
        notifyDataSetChanged();
    }

    public List<SearchResult> getSelectedList() {
        return mSelectedList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private AppCompatImageView mImageView;

        public ViewHolder(View v) {
            super(v);

            mImageView = v.findViewById(R.id.listRowImage);
        }
    }
}