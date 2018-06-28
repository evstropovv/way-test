package com.ewaytest;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ewaytest.models.vehicle.Vehicle;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TramsViewModel model;
    private GoogleMap mMap;
    private HashMap<String, List<Vehicle>> mapOfVisibleTrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapOfVisibleTrams = new HashMap<>();
        model = ViewModelProviders.of(this).get(TramsViewModel.class);
    }

    private void setMapStyle(){
        int mapResource = getResources().getIdentifier("retromap", "raw", getPackageName());
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, mapResource));
    }


    private void setListeners() {
        mMap.setOnCameraChangeListener(cameraPosition -> {
            addMarkersOnMap(mapOfVisibleTrams);
            model.setCameraPosition(cameraPosition);
        });
        mMap.setOnMarkerClickListener(marker -> {
            onMarkerClicked(marker);
            return true;
        });
    }

    private void onMarkerClicked(Marker marker) {
        for (Map.Entry<String, List<Vehicle>> entry : mapOfVisibleTrams.entrySet()) {
            List<Vehicle> list = entry.getValue();
            if ((list != null) && (list.size() > 0)) {
                for (int i = 0; i < list.size(); i++) {
                    if ((list.get(i).getLat() == marker.getPosition().latitude) &&
                            (list.get(i).getLng() == marker.getPosition().longitude)) {
                        Toast.makeText(this, "Marshrut " + entry.getKey() + " ID " + list.get(i).getId(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setMapStyle();
        setListeners();

        if (model.getCameraPosition() != null) {
            moveCamera(model.getCameraPosition());
        } else {
            LatLng lviv = new LatLng(49.841, 24.032);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lviv, 15);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lviv));
            mMap.animateCamera(cameraUpdate);
        }

        model.getTramsWithGpsInVisible().observe(this, stringListHashMap -> {
            mapOfVisibleTrams = stringListHashMap;
            addMarkersOnMap(stringListHashMap);
        });

    }

    private void moveCamera(CameraPosition cameraPosition) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cameraPosition.target));
        mMap.animateCamera(cameraUpdate);
    }

    private void addMarkersOnMap(HashMap<String, List<Vehicle>> map) {
        if ((map != null) && (map.size() > 0)) {
            mMap.clear();
            for (Map.Entry<String, List<Vehicle>> entry : new HashMap<>(map).entrySet()) {
                List<Vehicle> list = entry.getValue();
                if (list!=null){
                    for (int i = 0; i < list.size(); i++) {
                        LatLng tramMarker = new LatLng(list.get(i).getLat(), list.get(i).getLng());
                        if (isVisibleOnMap(tramMarker)) {
                            mMap.addMarker(new MarkerOptions().position(tramMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.tram)));
                        }
                    }
                }
            }
        }
    }

    public boolean isVisibleOnMap(LatLng latLng) {
        return mMap.getProjection().getVisibleRegion().latLngBounds.contains(latLng);
    }
}
