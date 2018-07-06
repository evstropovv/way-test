package com.ewaytest.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class Routes {
    List<LatLng> pointList1;
    List<LatLng> pointList2;

    public Routes(List<LatLng> pointList1, List<LatLng> pointList2) {
        this.pointList1 = pointList1;
        this.pointList2 = pointList2;
    }

    public List<LatLng> getPointList1() {
        return pointList1;
    }

    public List<LatLng> getPointList2() {
        return pointList2;
    }
}
