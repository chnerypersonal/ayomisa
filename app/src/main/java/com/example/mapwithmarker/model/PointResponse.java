package com.example.mapwithmarker.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by henrypriyono on 6/29/17.
 */

public class PointResponse {
    @SerializedName("Gereja")
    List<PointData> pointDataList;

    public List<PointData> getPointDataList() {
        return pointDataList;
    }
}
