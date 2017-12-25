package com.example.mapwithmarker.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapwithmarker.R;
import com.example.mapwithmarker.helper.DistanceHelper;
import com.example.mapwithmarker.model.LocalPointData;
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

import java.util.ArrayList;
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
    private String locationProviderErrorText;
    private String locationPermissionDeniedText;
    private String currentLocationInfoTitle;
    private String findCurrentLocationSuccessText;
    private String currentLocationNotRetrievedYetText;
    private String pointsNotAvailableText;
    private String searchingNearestPointText;
    private String nearestPointFoundText;

    private SupportMapFragment mapFragment;
    private TextView operationStatusTextView;
    private ImageView myLocationButton;
    private ImageView routeButton;
    private GoogleMap googleMap;
    private List<Marker> markerList = new ArrayList<>();
    private Marker currentLocationMarker;
    private Marker selectedPointMarker;

    private MapsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        initResources();
        bindViews();
        initClickListeners();
        initPresenter();
    }

    private void initResources() {
        loadingMapText = getString(R.string.loading_map_text);
        loadingPointDataText = getString(R.string.loading_point_data_text);
        searchingCurrentLocationText = getString(R.string.searching_current_location_text);
        retrievePointDataResultTemplateText = getString(R.string.retrieve_point_data_result);
        locationProviderErrorText = getString(R.string.location_provider_error_text);
        locationPermissionDeniedText = getString(R.string.location_permission_denied_text);
        currentLocationInfoTitle = getString(R.string.current_location_info_title);
        findCurrentLocationSuccessText = getString(R.string.current_location_found);
        currentLocationNotRetrievedYetText = getString(R.string.current_location_not_retrieved_yet);
        pointsNotAvailableText = getString(R.string.points_not_available_text);
        searchingNearestPointText = getString(R.string.searching_nearest_point_text);
        nearestPointFoundText = getString(R.string.nearest_point_found_text);
    }

    private void bindViews() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        operationStatusTextView = (TextView) findViewById(R.id.operation_status);
        myLocationButton = (ImageView) findViewById(R.id.my_location_button);
        routeButton = (ImageView) findViewById(R.id.route_button);
    }

    private void initClickListeners() {
        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLocationMarker != null) {
                    LatLng latLng = currentLocationMarker.getPosition();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }
            }
        });
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPointMarker != null && !isCurrentLocationMarker(selectedPointMarker)) {
                    navigateToMarker(selectedPointMarker);
                }
            }
        });
    }

    private void navigateToMarker(Marker marker) {
        LatLng position = marker.getPosition();
        String navigationUrl = "google.navigation:q=" + position.latitude + "," + position.longitude;

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigationUrl));
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
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
        operationStatusTextView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showResultMessage(String message) {
        dismissOperationStatus();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void displayLocalPointsLocation(List<LocalPointData> pointDataList) {

        markerList.clear();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (LocalPointData pointData : pointDataList) {
            Double latitude = Double.parseDouble(pointData.getLatitude());
            Double longitude = Double.parseDouble(pointData.getLongitude());
            LatLng position = new LatLng(latitude, longitude);

            Marker marker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_church))
                            .anchor(MARKER_ANCHOR_HORIZONTAL, MARKER_ANCHOR_VERTICAL)
                            .title(pointData.getName())
            );
            marker.setTag(pointData);

            markerList.add(marker);
            boundsBuilder.include(position);
        }

        //googleMap.animateCamera(
        //        CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), MARKER_BOUNDS_PADDING));

        showResultMessage(retrievePointDataResultTemplateText.replace("$1", Integer.toString(pointDataList.size())));
    }

    @Override
    public void displayPointsLocation(List<PointData> pointDataList) {

        markerList.clear();
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        for (PointData pointData : pointDataList) {
            Double latitude = Double.parseDouble(pointData.getLatitude());
            Double longitude = Double.parseDouble(pointData.getLongitude());
            LatLng position = new LatLng(latitude, longitude);

            Marker marker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(position)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_church))
                            .anchor(MARKER_ANCHOR_HORIZONTAL, MARKER_ANCHOR_VERTICAL)
                            .title(pointData.getName())
            );
            marker.setTag(pointData);

            markerList.add(marker);
            boundsBuilder.include(position);
        }

        googleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), MARKER_BOUNDS_PADDING));

        showResultMessage(retrievePointDataResultTemplateText.replace("$1", Integer.toString(pointDataList.size())));
    }

    @Override
    public void loadMap() {
        showOperationStatus(loadingMapText);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsMarkerActivity.this.googleMap = googleMap;

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_POSITION, DEFAULT_ZOOM));
                googleMap.setInfoWindowAdapter(new InfoAdapter());
                googleMap.setOnMarkerClickListener(new MarkerClickListener());
                googleMap.setOnMapClickListener(new MapClickListener());

                presenter.onMapReady();
            }
        });
    }

    @Override
    public void showLoadingDataFromNetworkStatus() {
        showOperationStatus(loadingPointDataText);
    }

    @Override
    public String getLocalPointsLocationString() {
        return getString(R.string.points_location_json_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_current_location:
                refreshCurrentLocation();
                break;
            case R.id.menu_search_nearest_point:
                searchNearestPoint();
                break;
        }
        return true;
    }

    private void refreshCurrentLocation() {
        showOperationStatus(searchingCurrentLocationText);
        findCurrentLocation(new CurrentLocationCallback() {
            @Override
            public void onLocationUpdated(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                showResultMessage(findCurrentLocationSuccessText);
                myLocationButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void searchNearestPoint() {
        if (currentLocationMarker == null) {
            showResultMessage(currentLocationNotRetrievedYetText);
            return;
        }

        if (markerList.isEmpty()) {
            showResultMessage(pointsNotAvailableText);
            return;
        }

        showOperationStatus(searchingNearestPointText);
        Marker nearestMarker = DistanceHelper.findNearestMarker(currentLocationMarker, markerList);

        onMarkerGotFocus(nearestMarker);
        showResultMessage(nearestPointFoundText);
    }

    private void findCurrentLocation(final CurrentLocationCallback callback) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            showResultMessage(locationPermissionDeniedText);
            return;
        }

        final LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        locationManager.removeUpdates(this);
                        dismissOperationStatus();
                        updateCurrentLocationMarker(location);
                        callback.onLocationUpdated(location);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {
                        locationManager.removeUpdates(this);
                        showResultMessage(locationProviderErrorText);
                    }
                }, null);
    }

    private void updateCurrentLocationMarker(Location location) {
        if (googleMap == null) {
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (currentLocationMarker == null) {
            currentLocationMarker = googleMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_position))
                            .title(currentLocationInfoTitle)
            );
        } else {
            currentLocationMarker.setPosition(latLng);
        }
    }

    private void onMarkerGotFocus(Marker marker) {
        selectedPointMarker = marker;
        marker.showInfoWindow();
        centerViewAboveMarker(marker);
        if (isCurrentLocationMarker(selectedPointMarker)) {
            hideRouteButton();
        } else {
            showRouteButton();
        }
    }

    private boolean isCurrentLocationMarker(Marker marker) {
        return marker.getTag() == null;
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

    private void showRouteButton() {
        routeButton.setVisibility(View.VISIBLE);
    }

    private void hideRouteButton() {
        routeButton.setVisibility(View.GONE);
    }

    private class MarkerClickListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(final Marker marker) {
            onMarkerGotFocus(marker);
            return true;
        }
    }

    private class MapClickListener implements GoogleMap.OnMapClickListener {
        @Override
        public void onMapClick(LatLng latLng) {
            selectedPointMarker = null;
            hideRouteButton();
        }
    }

    private class InfoAdapter implements GoogleMap.InfoWindowAdapter {

        private final View contentView;
        private TextView name;
        private TextView address;
        private TextView info;

        InfoAdapter() {
            contentView = getLayoutInflater().inflate(R.layout.info_window_layout, null);
            name = (TextView) contentView.findViewById(R.id.name);
            address = (TextView) contentView.findViewById(R.id.address);
            info = (TextView) contentView.findViewById(R.id.detailInfo);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            LocalPointData data = (LocalPointData) marker.getTag();

            if (data == null) return null;

            name.setText(data.getName());
            address.setText(data.getAddress() + "\n" + data.getArea());
            info.setText(data.getSchedule() + "\n" + data.getPhone() + "\n" + data.getWebsite());

            return contentView;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private interface CurrentLocationCallback {
        void onLocationUpdated(Location location);
    }
}
