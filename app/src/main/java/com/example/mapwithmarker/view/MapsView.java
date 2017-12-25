package com.example.mapwithmarker.view;

import com.example.mapwithmarker.model.LocalPointData;
import com.example.mapwithmarker.model.PointData;

import java.util.List;

/**
 * Created by henrypriyono on 6/29/17.
 */

public interface MapsView {
    void displayPointsLocation(List<PointData> pointDataList);

    void displayLocalPointsLocation(List<LocalPointData> pointDataList);

    void showResultMessage(String message);

    void loadMap();

    void showLoadingDataFromNetworkStatus();

    String getLocalPointsLocationString();
}
