package com.ewaytest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.ewaytest.models.Tram;
import com.ewaytest.models.todisplay.Point;
import com.ewaytest.models.todisplay.RouteToDisplay;
import com.ewaytest.models.vehicle.Vehicle;
import com.ewaytest.utils.LatLngInterpolator;
import com.ewaytest.utils.MarkerAnimation;
import com.ewaytest.viewmodels.TramsViewModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    protected Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initNoInternetSnackbar();

    }

    private void initNoInternetSnackbar() {
        snackbar = Snackbar.make(findViewById(R.id.frame), getResources().getString(R.string.no_internet_text), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.no_internet_button), view -> model.getRoutesList())
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
        model.isOnline().observe(this, aBoolean -> {
            if (!aBoolean) snackbar.show();
        });
    }

    private void initListeners() {
        mMap.setOnCameraIdleListener(() -> updateMarkers(mMap.getCameraPosition()));
        mMap.setOnMarkerClickListener(marker -> {
            onMarkerClicked(marker);
            return true;
        });
    }


    @Override
    void updateMarkers(CameraPosition cameraPosition) {
        addMarkersOnMap(mapOfVisibleTrams);
        model.setCameraPosition(cameraPosition);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapStyle();
        initListeners();
        moveCamera();
        model.getTramsWithGps().observe(this, stringListHashMap -> {
            mapOfVisibleTrams = stringListHashMap;
            addMarkersOnMap(mapOfVisibleTrams);
        });
        model.getRouteToDisplay().observe(this, this::showRouteOnMap);
    }

    @Override
    //key - унікальний ID маршрута, value - лист с ID транспорта з координатами
    protected void addMarkersOnMap(HashMap<String, HashSet<Vehicle>> map) {
        for (Map.Entry<String, HashSet<Vehicle>> entry : new HashMap<>(map).entrySet()) {
            try {
                for (Vehicle vehicle : entry.getValue()) {
                    LatLng tramMarker = new LatLng(vehicle.getLat(), vehicle.getLng());
                    String uniqueId = String.valueOf(vehicle.getId());

                    boolean isInMarkers = isInMarkers(uniqueId);
                    boolean isVisibleOnMap = isVisibleOnMap(tramMarker);

                    if (isInMarkers && isVisibleOnMap) {
                        //отримуємо маркер та виконуємо його анімацію
                        MarkerAnimation.animateMarker(markers.get(uniqueId).getMarkerOptions(), tramMarker,  mMap);
                        model.setVisibleRouteId(entry.getKey());
                    } else if (isVisibleOnMap) {
                        markers.put(uniqueId, new Tram(Long.parseLong(entry.getKey()), mMap.addMarker(new MarkerOptions().position(tramMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.tram)))));
                        model.setVisibleRouteId(entry.getKey());
                    } else if (isInMarkers) {
                        markers.get(uniqueId).getMarkerOptions().remove();
                        markers.remove(uniqueId);
                        model.removeVisibleRouteId(entry.getKey());
                    }
                }
            } catch (NullPointerException e) {
            }
        }
    }

    @Override
    void showRouteOnMap(RouteToDisplay route) {
        if (route != null) {
            List<Point> points = route.getRoute().getPoints().getPoint();
            List<LatLng> pointList1 = new ArrayList<>();
            List<LatLng> pointList2 = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i).getDirection() == 1) {
                    pointList1.add(new LatLng(Double.parseDouble(points.get(i).getLat()), Double.parseDouble(points.get(i).getLng())));
                } else {
                    pointList2.add(new LatLng(Double.parseDouble(points.get(i).getLat()), Double.parseDouble(points.get(i).getLng())));
                }
            }
            polyline1 = mMap.addPolyline(new PolylineOptions()
                    .clickable(true).addAll(pointList1).width(5f).color(Color.parseColor("#7eea0004")));
            polyline2 = mMap.addPolyline(new PolylineOptions()
                    .clickable(false).addAll(pointList2).width(5f).color(Color.parseColor("#6b001eff")));
        }
    }

    //пересуваємо камеру на теж місце при повороті екрані
    private void moveCamera() {
        CameraPosition cameraPosition = model.getCameraPosition();
        if (cameraPosition != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition.target));
            mMap.animateCamera(cameraUpdate);
        } else {
            //при першому запуску відображаємо центр Львова
            LatLng lviv = new LatLng(49.841, 24.032);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lviv, 15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lviv));
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    protected boolean isVisibleOnMap(LatLng latLng) {
        return mMap.getProjection().getVisibleRegion().latLngBounds.contains(latLng);
    }

    private void setMapStyle() {
        int mapResource = getResources().getIdentifier("retromap", "raw", getPackageName());
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapResource));
    }
}
