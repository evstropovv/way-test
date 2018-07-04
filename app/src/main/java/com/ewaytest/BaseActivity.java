package com.ewaytest;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;

import com.ewaytest.models.Tram;
import com.ewaytest.models.todisplay.RouteToDisplay;
import com.ewaytest.models.vehicle.Vehicle;
import com.ewaytest.viewmodels.TramsViewModel;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

abstract class BaseActivity extends FragmentActivity {

    protected TramsViewModel model;

    protected Polyline polyline1, polyline2;

    //key - уникальный ID транспорта, val - ID маршрута и Маркер на гугл карте

    protected HashMap<String, Tram> markers = new HashMap<>();

    protected HashMap<String, HashSet<Vehicle>> mapOfVisibleTrams = new HashMap<>();

    abstract boolean isVisibleOnMap(LatLng latLng);

    abstract void showRouteOnMap(RouteToDisplay route);

    abstract void addMarkersOnMap(HashMap<String, HashSet<Vehicle>> map);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = ViewModelProviders.of(this).get(TramsViewModel.class);
    }

    protected void onMarkerClicked(Marker marker) {
        if (model.isRouteShowing()) {
            clearRoute();
        } else {
            for (Map.Entry<String, Tram> entry : markers.entrySet()) {
                if (entry.getValue().getMarkerOptions().equals(marker)) {
                    model.loadRouteToDisplay(String.valueOf(entry.getValue().getId()));
                }
            }
        }
        updateMarkers(model.getCameraPosition());
    }

    abstract void updateMarkers(CameraPosition cameraPosition);

    protected void clearRoute() {
        model.clearRouteToDisplay();
        if (polyline1 != null) polyline1.remove();
        if (polyline2 != null) polyline2.remove();
    }

    protected boolean isInMarkers(String id) {
        return markers.containsKey(id);
    }
}
