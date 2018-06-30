
package com.ewaytest.models.todisplay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Point {

    @SerializedName("@attributes")
    @Expose
    private Attributes attributes;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("direction")
    @Expose
    private long direction;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("la t")
    @Expose
    private String laT;

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Point withAttributes(Attributes attributes) {
        this.attributes = attributes;
        return this;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Point withLat(String lat) {
        this.lat = lat;
        return this;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public Point withLng(String lng) {
        this.lng = lng;
        return this;
    }

    public long getDirection() {
        return direction;
    }

    public void setDirection(long direction) {
        this.direction = direction;
    }

    public Point withDirection(long direction) {
        this.direction = direction;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Point withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getLaT() {
        return laT;
    }

    public void setLaT(String laT) {
        this.laT = laT;
    }

    public Point withLaT(String laT) {
        this.laT = laT;
        return this;
    }

}
