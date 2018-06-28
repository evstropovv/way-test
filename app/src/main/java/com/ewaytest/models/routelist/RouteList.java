package com.ewaytest.models.routelist;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteList {

    @SerializedName("routesList")
    @Expose
    private RoutesList routesList;

    public RoutesList getRoutesList() {
        return routesList;
    }

    public void setRoutesList(RoutesList routesList) {
        this.routesList = routesList;
    }

    public RouteList withRoutesList(RoutesList routesList) {
        this.routesList = routesList;
        return this;
    }

}