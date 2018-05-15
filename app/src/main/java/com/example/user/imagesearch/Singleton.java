package com.example.user.imagesearch;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Singleton extends Application {
    public static final String TAG = Singleton.class.getSimpleName();

    public static Singleton mInstance;
    private Retrofit retrofit;
    public static Context mGlobalContext;

    public static synchronized Singleton getInstance() {
        if(mInstance == null)
            mInstance = new Singleton();

        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGlobalContext = this;
    }

    public Retrofit buildRetrofit(Activity activity) {
        final String token = activity.getString(R.string.kakao_app_key);
        Log.d(TAG, "buildRetrofit() : " + token);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json;charset=UTF-8")
                                .header("Authorization", "KakaoAK " + token)
                                .method(original.method(), original.body())
                                .build();

                        Response response = chain.proceed(request);

                        return response;
                    }
                }).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://dapi.kakao.com/")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
}
