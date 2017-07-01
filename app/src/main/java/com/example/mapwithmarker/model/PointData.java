package com.example.mapwithmarker.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by henrypriyono on 6/29/17.
 */

public class PointData {
    @SerializedName("id")
    String id;

    @SerializedName("nama")
    String name;

    @SerializedName("alamat")
    String address;

    @SerializedName("jadwal_harian")
    String daySchedule;

    @SerializedName("jadwal_mingguan")
    String weekSchedule;

    @SerializedName("latitude")
    String latitude;

    @SerializedName("longitude")
    String longitude;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getDaySchedule() {
        return daySchedule;
    }

    public String getWeekSchedule() {
        return weekSchedule;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
