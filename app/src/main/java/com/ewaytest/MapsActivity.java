package com.ewaytest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    TramsViewModel model;
    private GoogleMap mMap;
    private Boolean isMapReady = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        model = ViewModelProviders.of(this).get(TramsViewModel.class);


    }

    private void setCameraChangeListener() {
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();
                LatLng farLeft = visibleRegion.farLeft;
                LatLng farRight = visibleRegion.farRight;
                LatLng nearLeft = visibleRegion.nearLeft;
                LatLng nearRight = visibleRegion.nearRight;
                Toast.makeText(MapsActivity.this, "" + farLeft.latitude + " " + farLeft.longitude + "" +
                        "\n" + farRight.latitude + " " + farRight.longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        isMapReady = true;
        mMap = googleMap;
        LatLng lviv = new LatLng(49.841, 24.032);
        mMap.addMarker(new MarkerOptions().position(lviv).title("Marker in Lviv"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lviv));
        setCameraChangeListener();
//        mMap.setMyLocationEnabled(true);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lviv, 13);
        mMap.animateCamera(cameraUpdate);
        model.getRoutesList();
        model.getTramsWithGps(null).observe(this, new Observer<HashMap<String, List<Vehicle>>>() {
            @Override
            public void onChanged(@Nullable HashMap<String, List<Vehicle>> stringListHashMap) {
                for (Map.Entry<String, List<Vehicle>> entry : stringListHashMap.entrySet()) {
                    List<Vehicle> list = entry.getValue();
                    for (int i = 0; i < list.size(); i++) {
                        LatLng tramMarker = new LatLng(list.get(i).getLat(), list.get(i).getLng());
                        mMap.addMarker(new MarkerOptions().position(tramMarker).icon(BitmapDescriptorFactory.fromResource(R.drawable.tram)));
                    }
                }
            }
        });
    }
}
