
package com.ewaytest.models.todisplay;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Points {

    @SerializedName("point")
    @Expose
    private List<Point> point = null;

    public List<Point> getPoint() {
        return point;
    }

    public void setPoint(List<Point> point) {
        this.point = point;
    }

    public Points withPoint(List<Point> point) {
        this.point = point;
        return this;
    }

}
