
package com.ewaytest.models.todisplay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("points")
    @Expose
    private Points points;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Route withType(String type) {
        this.type = type;
        return this;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public Route withPoints(Points points) {
        this.points = points;
        return this;
    }

}
