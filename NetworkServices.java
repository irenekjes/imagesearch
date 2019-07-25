package com.example.user.imagesearch;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface NetworkServices {
    @GET("v2/search/image")
    Observable<SearchResponse> getSearchImage(@QueryMap Map<String, Object> param);
}
