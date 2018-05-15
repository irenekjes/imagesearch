package com.example.user.imagesearch;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Activity mActivity;
    private Singleton mSingleton;
    private Retrofit mRetrofit;
    private NetworkServices mNetworkService;

    private int mCurrentPage;
    private List<SearchResult> mSearchResultList;

    private AppCompatEditText mEdtSearch;
    private AppCompatButton mBtnSearch;
    private RecyclerView mRecycler;

    private MainImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    protected void init() {
        initData();
    }

    protected void initData() {
        mActivity = this;

        mSingleton = Singleton.getInstance();
        mRetrofit = mSingleton.buildRetrofit(mActivity);
        mNetworkService = mRetrofit.create(NetworkServices.class);

        mSearchResultList = new ArrayList<>();

        mAdapter = new MainImageAdapter(mActivity, mSearchResultList);
        mCurrentPage = 1;

        initView();
    }

    protected void initView() {
        mEdtSearch = findViewById(R.id.mainSearchEdt);
        mBtnSearch = findViewById(R.id.mainSearchBtn);
        mRecycler = findViewById(R.id.mainImageRecycler);

        initAction();
    }

    protected void initAction() {
        mBtnSearch.setOnClickListener(this);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainSearchBtn:
                mCurrentPage = 1;
                searchAction();
                break;
        }
    }

    private boolean checkValid() {
        if(mEdtSearch.getText().toString().trim().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void searchAction() {
        if(checkValid()) {
            Map<String, Object> param = new HashMap<>();
            param.put("query", mEdtSearch.getText().toString().trim());
            param.put("page", mCurrentPage);
            param.put("size", 20);

            Log.d(TAG, "searchAction() : " + param);
            search(param);
        } else {
            Log.d(TAG, "searchAction() : checkValid().else");
        }
    }

    private void search(Map<String, Object> param) {
        Observable<SearchResponse> call = mNetworkService.getSearchImage(param);
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SearchResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe");
                    }

                    @Override
                    public void onNext(SearchResponse searchResponse) {
                        if(searchResponse != null) {
                            if(searchResponse.getmResultList().size() == 0) {

                            } else {
                                setResultList(searchResponse.getmResultList());
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private void setResultList(List<SearchResult> list) {
        if(mCurrentPage == 1) {
            // 새로검색
            mAdapter.changeItemList(list);
        } else {
            // 페이징
            mAdapter.addItemList(list);
        }
    }
}