package com.example.mapwithmarker.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by henrypriyono on 12/25/17.
 */

public class LocalPointData {
    @SerializedName("id")
    String id;

    @SerializedName("NamaGereja")
    String name;

    @SerializedName("AlamatGereja")
    String address;

    @SerializedName("Telp")
    String phone;

    @SerializedName("JadwalMisa")
    String schedule;

    @SerializedName("Website")
    String website;

    @SerializedName("Daerah")
    String area;

    @SerializedName("Lat")
    String latitude;

    @SerializedName("Long")
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

    public String getPhone() {
        return phone;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getWebsite() {
        return website;
    }

    public String getArea() {
        return area;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
