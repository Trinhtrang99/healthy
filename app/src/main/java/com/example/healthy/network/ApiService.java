package com.example.healthy.network;

import com.example.healthy.network.response.AqiResponse;
import com.example.healthy.network.response.CaloriesResponse;
import com.example.healthy.network.response.Video;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=50&fields=items(snippet(title,thumbnails(high(url),default)," +
            "resourceId(videoId))),nextPageToken,pageInfo&key=AIzaSyCR9owVOuMyRJt71VtOOgWIjUI-PS18cyU")
    Single<Video> getVideo(@Query(value = "playlistId") String playID);

    ///feed/geo::lat;:lng/?token=:token
    @GET("feed/geo:{lat};{lng}/?token=f53d5c5a1b967b4de9ff611e90120fd643e8a6c7")
    Single<AqiResponse> getAqi(@Path(value = "lat") Double lat, @Path(value = "lng") Double lng);

    //https://api.edamam.com/api/nutrition-data?app_id=ab7fc976&app_key=cc119801ee9d30ee1812ece47586161e&ingr=1 apple, chicken
    @GET("https://api.edamam.com/api/nutrition-data?app_id=ab7fc976&app_key=cc119801ee9d30ee1812ece47586161e")
    Single<CaloriesResponse> getCalories(@Query(value = "ingr") String ingr);
}
