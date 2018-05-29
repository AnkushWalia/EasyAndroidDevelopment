package com.android.service.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceRectangle {

    @SerializedName("width")
    @Expose
    private Integer width;
    @SerializedName("top")
    @Expose
    private Integer top;
    @SerializedName("left")
    @Expose
    private Integer left;
    @SerializedName("height")
    @Expose
    private Integer height;

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

}
