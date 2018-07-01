package com.ewaytest;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TramsViewModel model;
    private GoogleMap mMap;
    private Polyline polyline1, polyline2;
    //key - уникальный ID транспорта, val - ID маршрута и Маркер на гугл карте
    private HashMap<String, Tram> markers = new HashMap<>();
    private HashMap<String, List<Vehicle>> mapOfVisibleTrams = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        model = ViewModelProviders.of(this).get(TramsViewModel.class);
    }

    private void setMapStyle() {
        int mapResource = getResources().getIdentifier("retromap", "raw", getPackageName());
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapResource));
    }

    private void updateMarkers(CameraPosition cameraPosition) {
        addMarkersOnMap(mapOfVisibleTrams);
        model.setCameraPosition(cameraPosition);
    }

    private void setListeners() {
        mMap.setOnCameraIdleListener(() -> updateMarkers(mMap.getCameraPosition()));
        mMap.setOnMarkerClickListener(marker -> {
            onMarkerClicked(marker);
            return true;
        });
    }

    private void onMarkerClicked(Marker marker) {
        if (model.isRouteShowing()) {
            clearRoute();
        } else {
            for (Map.Entry<String, Tram> entry : markers.entrySet()) {
                if (entry.getValue().getMarkerOptions().equals(marker)) {
                    model.setRouteFilter(entry.getValue().getId());
                    model.loadRouteToDisplay(String.valueOf(entry.getValue().getId()));
                }
                //TODO пересмотреть!!
                if (entry.getValue().getId() != entry.getValue().getId()) markers.remove(entry.getKey());
            }
        }
        updateMarkers(model.getCameraPosition());
    }

//    private void clearFromMarkers(long routeFilter) {
//        for (Map.Entry<String, Tram> entry : markers.entrySet()) {
//            if (entry.getValue().getId() != routeFilter) markers.remove(entry.getKey());
//        }
//    }


    private void clearRoute() {
        model.clearRouteToDisplay();
        if (polyline1 != null) polyline1.remove();
        if (polyline2 != null) polyline2.remove();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapStyle();
        setListeners();
        moveCamera();
        model.getTramsWithGps().observe(this, stringListHashMap -> {
            mapOfVisibleTrams = stringListHashMap;
            addMarkersOnMap(mapOfVisibleTrams);
        });
        model.getRouteToDisplay().observe(this, this::showRouteOnMap);
    }

    private void showRouteOnMap(RouteToDisplay route) {
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
                    .clickable(true).addAll(pointList1).color(Color.parseColor("#7eea0004")));
            polyline2 = mMap.addPolyline(new PolylineOptions()
                    .clickable(false).addAll(pointList2).color(Color.parseColor("#6b001eff")));
        }
    }

    private void moveCamera() {
        CameraPosition cameraPosition = model.getCameraPosition();
        if (cameraPosition != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition.target));
            mMap.animateCamera(cameraUpdate);
        } else {
            LatLng lviv = new LatLng(49.841, 24.032);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lviv, 15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lviv));
            mMap.animateCamera(cameraUpdate);
        }
    }

    //key - уникальный ID маршрута, value - лист с ID транспорта и их координатами
    private void addMarkersOnMap(HashMap<String, List<Vehicle>> map) {

        for (Map.Entry<String, List<Vehicle>> entry : new HashMap<>(map).entrySet()) {
            try {
                List<Vehicle> list = entry.getValue();
                for (int i = 0; i < list.size(); i++) {
                    if (model.isRouteShowing() && Long.parseLong(entry.getKey()) != model.getRouteFilter())
                        break;

                    LatLng tramMarker = new LatLng(list.get(i).getLat(), list.get(i).getLng());
                    String uniqueId = String.valueOf(list.get(i).getId());

                    boolean isInMarkers = isInMarkers(uniqueId);
                    boolean isVisibleOnMap = isVisibleOnMap(tramMarker);

                    if (isInMarkers && isVisibleOnMap) {
                        //получаем маркер и присваиваем ему новые координаты
                        MarkerAnimation.animateMarkerToICS(markers.get(uniqueId).getMarkerOptions(), tramMarker, new LatLngInterpolator.Spherical());
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

    public boolean isVisibleOnMap(LatLng latLng) {
        return mMap.getProjection().getVisibleRegion().latLngBounds.contains(latLng);
    }

    private boolean isInMarkers(String id) {
        if (markers.containsKey(id)) {
            return true;
        } else {
            return false;
        }

//        for (Map.Entry<String, Tram> entry : new HashMap<>(markers).entrySet()) {
//            if (entry.getKey().equals(id)) return true;
//        }
// return false;
    }
}
