package com.ewaytest.models.routelist;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutesList {

    @SerializedName("route")
    @Expose
    private List<Route> route = null;

    public List<Route> getRoute() {
        return route;
    }

    public void setRoute(List<Route> route) {
        this.route = route;
    }

    public RoutesList withRoute(List<Route> route) {
        this.route = route;
        return this;
    }

}