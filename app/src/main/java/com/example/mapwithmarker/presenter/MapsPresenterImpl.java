package com.example.mapwithmarker.presenter;

import com.example.mapwithmarker.api.PointsDataApi;
import com.example.mapwithmarker.model.LocalPointData;
import com.example.mapwithmarker.model.PointData;
import com.example.mapwithmarker.model.PointResponse;
import com.example.mapwithmarker.view.MapsMarkerActivity;
import com.example.mapwithmarker.view.MapsView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by henrypriyono on 6/29/17.
 */

public class MapsPresenterImpl implements MapsPresenter {

    private MapsView view;
    private PointsDataApi pointsDataApi;

    public MapsPresenterImpl(MapsView view) {
        this.view = view;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PointsDataApi.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        pointsDataApi = retrofit.create(PointsDataApi.class);
    }

    @Override
    public void onCreate() {
        view.loadMap();
    }

    @Override
    public void onMapReady() {
        getPointsLocationFromLocal();
    }

    private void getPointsLocationFromLocal() {
        String jsonData = view.getLocalPointsLocationString();
        List<LocalPointData> localPointDataList
                = new Gson().fromJson(jsonData, new TypeToken<List<LocalPointData>>(){}.getType());
        view.displayLocalPointsLocation(localPointDataList);
    }

    private void getPointsLocationFromNetwork() {
        view.showLoadingDataFromNetworkStatus();
        pointsDataApi.getPointsData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PointResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PointResponse response) {
                        view.displayPointsLocation(response.getPointDataList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showResultMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
