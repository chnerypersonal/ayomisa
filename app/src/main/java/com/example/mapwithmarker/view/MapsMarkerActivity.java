package com.example.mapwithmarker.view;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapwithmarker.R;
import com.example.mapwithmarker.model.PointData;
import com.example.mapwithmarker.presenter.MapsPresenter;
import com.example.mapwithmarker.presenter.MapsPresenterImpl;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsMarkerActivity extends AppCompatActivity implements MapsView {

    private static final LatLng DEFAULT_POSITION = new LatLng(-6.175993, 106.824461);
    private static final int DEFAULT_ZOOM = 15;
    public static final float MARKER_ANCHOR_HORIZONTAL = 0.4375f;
    public static final float MARKER_ANCHOR_VERTICAL = 0.84375f;
    public static final int MARKER_BOUNDS_PADDING = 100;

    private String loadingMapText;
    private String loadingPointDataText;
    private String searchingCurrentLocationText;
    private String retrievePointDataResultTemplateText;

    private SupportMapFragment mapFragment;
    private TextView operationStatusTextView;
    private GoogleMap googleMap;

    private MapsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initResources();
        bindViews();
        initPresenter();
    }

    private void initResources() {
        loadingMapText = getString(R.string.loading_map_text);
        loadingPointDataText = getString(R.string.loading_point_data_text);
        searchingCurrentLocationText = getString(R.string.searching_current_location_text);
        retrievePointDataResultTemplateText =  getString(R.string.retrieve_point_data_result);
    }

    private void bindViews() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        operationStatusTextView = (TextView) findViewById(R.id.operation_status);
    }

    private void initPresenter() {
        presenter = new MapsPresenterImpl(this);
        presenter.onCreate();
    }

    private void showOperationStatus(String textStatus) {
        operationStatusTextView.setText(textStatus);
        operationStatusTextView.setVisibility(View.VISIBLE);
    }

    public void dismissOperationStatus() {
        operationStatusTextView.setVisibility(View.GONE);
    }

    private void showResultMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayPointsLocation(List<PointData> pointDataList) {
        dismissOperationStatus();

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (PointData pointData : pointDataList) {
            Double latitude = Double.parseDouble(pointData.getLatitude());
            Double longitude = Double.parseDouble(pointData.getLongitude());
            LatLng position = new LatLng(latitude, longitude);

            googleMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_church))
                            .anchor(MARKER_ANCHOR_HORIZONTAL, MARKER_ANCHOR_VERTICAL)
                            .title(pointData.getName())
            ).setTag(pointData);

            boundsBuilder.include(position);
        }

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), MARKER_BOUNDS_PADDING));

        showResultMessage(retrievePointDataResultTemplateText.replace("$1", Integer.toString(pointDataList.size())));
    }

    @Override
    public void showError(String message) {
        dismissOperationStatus();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void loadMap() {
        showOperationStatus(loadingMapText);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsMarkerActivity.this.googleMap = googleMap;

                googleMap.setMyLocationEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_POSITION, DEFAULT_ZOOM));
                googleMap.setInfoWindowAdapter(new InfoAdapter());
                googleMap.setOnMarkerClickListener(new MarkerClickListener());

                presenter.onMapReady();
            }
        });
    }

    @Override
    public void showLoadingDataFromNetworkStatus() {
        showOperationStatus(loadingPointDataText);
    }

    private class MarkerClickListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            marker.showInfoWindow();
            centerViewAboveMarker(marker);
            return true;
        }

        private void centerViewAboveMarker(Marker marker) {
            View mapContainer = findViewById(R.id.map);
            int mapHeight = mapContainer.getHeight();

            Projection projection = googleMap.getProjection();

            LatLng markerLatLng = new LatLng(marker.getPosition().latitude,
                    marker.getPosition().longitude);
            Point markerScreenPosition = projection.toScreenLocation(markerLatLng);
            Point pointQuarterScreenAbove = new Point(markerScreenPosition.x,
                    markerScreenPosition.y - (mapHeight / 4));

            LatLng aboveMarkerLatLng = projection
                    .fromScreenLocation(pointQuarterScreenAbove);

            CameraUpdate center = CameraUpdateFactory.newLatLng(aboveMarkerLatLng);
            googleMap.animateCamera(center, 500, null);
        }
    }

    private class InfoAdapter implements GoogleMap.InfoWindowAdapter {

        private final View contentView;
        private TextView name;
        private TextView address;
        private TextView daySchedule;
        private TextView weekSchedule;

        InfoAdapter() {
            contentView = getLayoutInflater().inflate(R.layout.info_window_layout, null);
            name = (TextView) contentView.findViewById(R.id.name);
            address = (TextView) contentView.findViewById(R.id.address);
            daySchedule = (TextView) contentView.findViewById(R.id.day_schedule);
            weekSchedule = (TextView) contentView.findViewById(R.id.week_schedule);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            PointData data = (PointData) marker.getTag();

            if (data == null) return null;

            name.setText(data.getName());
            address.setText(data.getAddress());
            daySchedule.setText("Misa Harian : \n" + data.getDaySchedule());
            weekSchedule.setText("Misa Mingguan : \n" + data.getWeekSchedule());

            return contentView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }
}
