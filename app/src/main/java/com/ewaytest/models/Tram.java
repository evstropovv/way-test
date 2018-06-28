package com.ewaytest.models;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Tram {
    private String id;
    private String vehicleName;
    private Marker markerOptions;


    public Tram(String id, String vehicleName, Marker markerOptions) {
        this.id = id;
        this.vehicleName = vehicleName;
        this.markerOptions = markerOptions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public Marker getMarker() {
        return markerOptions;
    }

    public void setMarker(Marker markerOptions) {
        this.markerOptions = markerOptions;
    }
}
