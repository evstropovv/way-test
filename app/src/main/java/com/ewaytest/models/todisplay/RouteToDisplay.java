
package com.ewaytest.models.todisplay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteToDisplay {

    @SerializedName("route")
    @Expose
    private Route route;

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public RouteToDisplay withRoute(Route route) {
        this.route = route;
        return this;
    }

}
