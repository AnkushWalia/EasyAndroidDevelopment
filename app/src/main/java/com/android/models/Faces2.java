
package com.android.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Faces2 {

    @SerializedName("face_rectangle")
    @Expose
    private FaceRectangle_ faceRectangle;
    @SerializedName("face_token")
    @Expose
    private String faceToken;

    public FaceRectangle_ getFaceRectangle() {
        return faceRectangle;
    }

    public void setFaceRectangle(FaceRectangle_ faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    public String getFaceToken() {
        return faceToken;
    }

    public void setFaceToken(String faceToken) {
        this.faceToken = faceToken;
    }

}
