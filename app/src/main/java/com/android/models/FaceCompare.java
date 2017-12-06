
package com.android.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaceCompare {

    @SerializedName("faces1")
    @Expose
    private List<Faces1> faces1 = null;
    @SerializedName("faces2")
    @Expose
    private List<Faces2> faces2 = null;
    @SerializedName("time_used")
    @Expose
    private Integer timeUsed;
    @SerializedName("thresholds")
    @Expose
    private Thresholds thresholds;
    @SerializedName("confidence")
    @Expose
    private Double confidence;
    @SerializedName("image_id2")
    @Expose
    private String imageId2;
    @SerializedName("image_id1")
    @Expose
    private String imageId1;
    @SerializedName("request_id")
    @Expose
    private String requestId;

    public List<Faces1> getFaces1() {
        return faces1;
    }

    public void setFaces1(List<Faces1> faces1) {
        this.faces1 = faces1;
    }

    public List<Faces2> getFaces2() {
        return faces2;
    }

    public void setFaces2(List<Faces2> faces2) {
        this.faces2 = faces2;
    }

    public Integer getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Integer timeUsed) {
        this.timeUsed = timeUsed;
    }

    public Thresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(Thresholds thresholds) {
        this.thresholds = thresholds;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getImageId2() {
        return imageId2;
    }

    public void setImageId2(String imageId2) {
        this.imageId2 = imageId2;
    }

    public String getImageId1() {
        return imageId1;
    }

    public void setImageId1(String imageId1) {
        this.imageId1 = imageId1;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
