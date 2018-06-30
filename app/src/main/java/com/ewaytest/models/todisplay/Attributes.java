
package com.ewaytest.models.todisplay;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attributes {

    @SerializedName("is_stop")
    @Expose
    private String isStop;
    @SerializedName("is_st op")
    @Expose
    private String isStOp;

    public String getIsStop() {
        return isStop;
    }

    public void setIsStop(String isStop) {
        this.isStop = isStop;
    }

    public Attributes withIsStop(String isStop) {
        this.isStop = isStop;
        return this;
    }

    public String getIsStOp() {
        return isStOp;
    }

    public void setIsStOp(String isStOp) {
        this.isStOp = isStOp;
    }

    public Attributes withIsStOp(String isStOp) {
        this.isStOp = isStOp;
        return this;
    }

}
