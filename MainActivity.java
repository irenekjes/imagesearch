package com.example.user.imagesearch;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private Activity mActivity;
    private Singleton mSingleton;
    private Retrofit mRetrofit;
    private NetworkServices mNetworkService;

    private int mCurrentPage;
    private int mCurrentTab = 0;
    private List<SearchResult> mSearchResultList;
    private List<SearchResult> mSelectedList = new ArrayList<>();

    private TextView mTab1;
    private TextView mTab2;
    private RelativeLayout mRlSearch;
    private AppCompatEditText mEdtSearch;
    private AppCompatButton mBtnSearch;
    private RecyclerView mRecycler;
    private TextView mTvNoItem;

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

        mCurrentPage = 1;

        initView();
    }

    protected void initView() {
        mTab1 = findViewById(R.id.main_tab_1);
        mTab2 = findViewById(R.id.main_tab_2);
        mRlSearch = findViewById(R.id.search_box);
        mEdtSearch = findViewById(R.id.search_edt);
        mBtnSearch = findViewById(R.id.search_btn);
        mRecycler = findViewById(R.id.search_image_recycler);
        mTvNoItem = findViewById(R.id.tv_no_item);

        initAction();
    }

    protected void initAction() {
        mTab1.setOnClickListener(this);
        mTab2.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);

        if(mSearchResultList.size() > 0) {
            mTvNoItem.setVisibility(View.GONE);
            mRecycler.setVisibility(View.VISIBLE);
        } else {
            mTvNoItem.setVisibility(View.VISIBLE);
            mRecycler.setVisibility(View.GONE);
        }

        if(mCurrentTab == 0) {
            setSearchTab();
        } else if(mCurrentTab == 1) {
            setResultTab();
        }
    }

    private void setSearchTab() {
        mRlSearch.setVisibility(View.VISIBLE);
        mCurrentTab = 0;
        mCurrentPage = 1;

        mAdapter = new MainImageAdapter(mActivity, mSearchResultList, true);
        mRecycler.setAdapter(mAdapter);

        mRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(!mRecycler.canScrollVertically(-1)) {

                } else if(!mRecycler.canScrollVertically(1)) {
                    mCurrentPage++;
                    searchAction();
                } else {

                }
            }
        });
    }

    private void setResultTab() {
        mRlSearch.setVisibility(View.GONE);
        mEdtSearch.setText(null);
        mCurrentTab = 1;
        mCurrentPage = 1;

        mRecycler.setOnScrollListener(null);

        if(mAdapter != null) {
            mSelectedList.addAll(mAdapter.getSelectedList());
        }

        if(mSelectedList.size() > 0) {
            mRecycler.setVisibility(View.VISIBLE);
            mTvNoItem.setVisibility(View.GONE);

            mAdapter = new MainImageAdapter(mActivity, mSelectedList, false);
            mRecycler.setAdapter(mAdapter);
        } else {
            mRecycler.setVisibility(View.GONE);
            mTvNoItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                mCurrentPage = 1;
                hideSoftKeyboard();
                searchAction();
                break;
            case R.id.main_tab_1:
                setSearchTab();
                break;
            case R.id.main_tab_2:
                setResultTab();
                break;
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEdtSearch.getWindowToken(), 0);
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
                                mTvNoItem.setVisibility(View.VISIBLE);
                                mRecycler.setVisibility(View.GONE);
                            } else {
                                mTvNoItem.setVisibility(View.GONE);
                                mRecycler.setVisibility(View.VISIBLE);

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
        mSearchResultList = list;
        if(mCurrentPage == 1) {
            // 새로검색
            mAdapter.changeItemList(mSearchResultList);
        } else {
            // 페이징
            mAdapter.addItemList(mSearchResultList);
        }
    }
}