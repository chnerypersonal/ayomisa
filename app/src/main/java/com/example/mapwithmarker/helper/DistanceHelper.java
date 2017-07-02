package com.example.mapwithmarker.helper;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by henrypriyono on 7/2/17.
 */

public class DistanceHelper {
    public static Marker findNearestMarker(Marker currentPositionMarker,
                                           List<Marker> destinationMarkerList) {

        float minDistance = -1;
        float[] results = new float[1];
        Marker nearestMarker = null;

        LatLng currentPosition = currentPositionMarker.getPosition();

        for (Marker marker : destinationMarkerList) {
            LatLng markerPosition = marker.getPosition();

            Location.distanceBetween(currentPosition.latitude, currentPosition.longitude,
                    markerPosition.latitude, markerPosition.longitude, results);

            float distance = results[0];

            if (minDistance < 0 || distance < minDistance) {
                minDistance = distance;
                nearestMarker = marker;
            }
        }

        return nearestMarker;
    }
}
