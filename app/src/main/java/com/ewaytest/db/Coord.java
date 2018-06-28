package com.ewaytest.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tatatatat")
public class Coord {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "number")
    private String trumNumber;

    @ColumnInfo(name = "longitude")
    private Double longitude;

    @ColumnInfo(name = "latitude")
    private Double latitude;


    public Coord(String trumNumber, Double longitude, Double latitude) {
        this.trumNumber = trumNumber;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTrumNumber() {
        return trumNumber;
    }

    public void setTrumNumber(String trumNumber) {
        this.trumNumber = trumNumber;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
