package com.example.mapwithmarker.api;

import com.example.mapwithmarker.model.PointData;
import com.example.mapwithmarker.model.PointResponse;

import java.util.List;


import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

/**
 * Created by henrypriyono on 6/29/17.
 */

public interface PointsDataApi {
    static final String BASE_URL = "https://ayomisa.000webhostapp.com/api.php/";

    @GET("Gereja?transform=1")
    Observable<PointResponse> getPointsData();
}
