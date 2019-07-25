package com.example.user.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = ImageActivity.class.getSimpleName();

    private Activity mActivity;

    private AppCompatImageView mImageView;

    private String mUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_image);

        init();
    }

    protected void init() {
        initData();
    }

    protected void initData() {
        mActivity = this;

        mUri = getIntent().getStringExtra("photo");

        initView();
    }

    protected void initView() {
        mImageView = findViewById(R.id.imgView);

        initAction();
    }

    protected void initAction() {
        Glide.with(mActivity).load(mUri).into(mImageView);
    }
}

