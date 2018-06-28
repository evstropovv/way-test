package com.ewaytest.models.vehicle;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RoutesGPS {

    @SerializedName("vehicle")
    @Expose
    private List<Vehicle> vehicle = null;

    public List<Vehicle> getVehicle() {
        return vehicle;
    }

    public void setVehicle(List<Vehicle> vehicle) {
        this.vehicle = vehicle;
    }

    public RoutesGPS withVehicle(List<Vehicle> vehicle) {
        this.vehicle = vehicle;
        return this;
    }

}
