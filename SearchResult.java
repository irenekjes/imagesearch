package com.example.user.imagesearch;

import com.google.gson.annotations.SerializedName;

public class SearchResult {
    @SerializedName("thumbnail_url")
    private final String thumbUrl;

    @SerializedName("image_url")
    private final String photoUrl;

    public SearchResult(String thumb, String photo) {
        this.thumbUrl = thumb;
        this.photoUrl = photo;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
