package com.ewaytest.models.vehicle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("direction")
    @Expose
    private long direction;
    @SerializedName("data_relevance")
    @Expose
    private long dataRelevance;
    @SerializedName("handicapped")
    @Expose
    private long handicapped;
    @SerializedName("wifi")
    @Expose
    private long wifi;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Vehicle withId(long id) {
        this.id = id;
        return this;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Vehicle withLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Vehicle withLng(double lng) {
        this.lng = lng;
        return this;
    }

    public long getDirection() {
        return direction;
    }

    public void setDirection(long direction) {
        this.direction = direction;
    }

    public Vehicle withDirection(long direction) {
        this.direction = direction;
        return this;
    }

    public long getDataRelevance() {
        return dataRelevance;
    }

    public void setDataRelevance(long dataRelevance) {
        this.dataRelevance = dataRelevance;
    }

    public Vehicle withDataRelevance(long dataRelevance) {
        this.dataRelevance = dataRelevance;
        return this;
    }

    public long getHandicapped() {
        return handicapped;
    }

    public void setHandicapped(long handicapped) {
        this.handicapped = handicapped;
    }

    public Vehicle withHandicapped(long handicapped) {
        this.handicapped = handicapped;
        return this;
    }

    public long getWifi() {
        return wifi;
    }

    public void setWifi(long wifi) {
        this.wifi = wifi;
    }

    public Vehicle withWifi(long wifi) {
        this.wifi = wifi;
        return this;
    }

}