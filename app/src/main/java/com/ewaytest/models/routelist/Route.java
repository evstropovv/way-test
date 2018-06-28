package com.ewaytest.models.routelist;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("start_position")
    @Expose
    private String startPosition;
    @SerializedName("stop_position")
    @Expose
    private String stopPosition;
    @SerializedName("transport")
    @Expose
    private String transport;
    @SerializedName("has_gps")
    @Expose
    private long hasGps;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Route withId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Route withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }

    public Route withStartPosition(String startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public String getStopPosition() {
        return stopPosition;
    }

    public void setStopPosition(String stopPosition) {
        this.stopPosition = stopPosition;
    }

    public Route withStopPosition(String stopPosition) {
        this.stopPosition = stopPosition;
        return this;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public Route withTransport(String transport) {
        this.transport = transport;
        return this;
    }

    public long getHasGps() {
        return hasGps;
    }

    public void setHasGps(long hasGps) {
        this.hasGps = hasGps;
    }

    public Route withHasGps(long hasGps) {
        this.hasGps = hasGps;
        return this;
    }

}