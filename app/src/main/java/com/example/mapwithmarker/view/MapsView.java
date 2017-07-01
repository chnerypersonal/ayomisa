package com.example.mapwithmarker.view;

import com.example.mapwithmarker.model.PointData;

import java.util.List;

/**
 * Created by henrypriyono on 6/29/17.
 */

public interface MapsView {
    void displayPointsLocation(List<PointData> pointDataList);

    void showError(String message);

    void loadMap();

    void showLoadingDataFromNetworkStatus();
}
