package com.android.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thresholds {

    @SerializedName("1e-3")
    @Expose
    private Double _1e3;
    @SerializedName("1e-5")
    @Expose
    private Double _1e5;
    @SerializedName("1e-4")
    @Expose
    private Double _1e4;

    public Double get1e3() {
        return _1e3;
    }

    public void set1e3(Double _1e3) {
        this._1e3 = _1e3;
    }

    public Double get1e5() {
        return _1e5;
    }

    public void set1e5(Double _1e5) {
        this._1e5 = _1e5;
    }

    public Double get1e4() {
        return _1e4;
    }

    public void set1e4(Double _1e4) {
        this._1e4 = _1e4;
    }

}
