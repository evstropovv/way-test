package com.ewaytest.models;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Tram {

    //ID маршрута
    private long id;
    private Marker markerOptions;

    public Tram(long id, Marker markerOptions) {
        this.id = id;
        this.markerOptions = markerOptions;
    }
    public Marker getMarkerOptions() {
        return markerOptions;
    }
    public long getId() {
        return id;
    }

}
