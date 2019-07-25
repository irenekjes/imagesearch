package com.example.user.imagesearch;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class SearchResponse {
    @SerializedName("documents")
    private List<SearchResult> mResultList;

    public List<SearchResult> getmResultList() {
        return mResultList;
    }

    /*
    *   HTTP/1.1 200 OK
        Content-Type: application/json;charset=UTF-8
        {
          "meta": {
            "total_count": 422583,
            "pageable_count": 3854,
            "is_end": false
          },
          "documents": [
            {
              "collection": "news",
              "thumbnail_url": "https://search2.kakaocdn.net/argon/130x130_85_c/36hQpoTrVZp",
              "image_url": "http://t1.daumcdn.net/news/201706/21/kedtv/20170621155930292vyyx.jpg",
              "width": 540,
              "height": 457,
              "display_sitename": "한국경제TV",
              "doc_url": "http://v.media.daum.net/v/20170621155930002",
              "datetime": "2017-06-21T15:59:30.000+09:00"
            },
            ...
          ]
        }
    * */
}
