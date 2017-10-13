package com.yalantis.ucrop.imagepicker.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/11/17.
 */


public class Config implements Parcelable {

    public static final String EXTRA_CONFIG = "ImagePickerConfig";
    public static final String EXTRA_IMAGES = "ImagePickerImages";


    public static final int RC_PICK_IMAGES = 100;
    public static final int RC_CAPTURE_IMAGE = 101;
    public static final int RC_WRITE_EXTERNAL_STORAGE_PERMISSION = 102;
    public static final int RC_CAMERA_PERMISSION = 103;


    public static final int MAX_SIZE = Integer.MAX_VALUE;
    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel source) {
            return new Config(source);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };
    private int toolbarColor;
    private int statusBarColor;
    private int toolbarTextColor;
    private int toolbarIconColor;
    private int progressBarColor;
    private int backgroundColor;
    private boolean isCameraOnly;
    private boolean isMultipleMode;
    private boolean isFolderMode;
    private boolean isShowCamera;
    private int maxSize;
    private String doneTitle;
    private String folderTitle;
    private String imageTitle;
    private SavePath savePath;
    private ArrayList<Image> selectedImages;


    public Config() {
    }

    protected Config(Parcel in) {
        this.toolbarColor = in.readInt();
        this.statusBarColor = in.readInt();
        this.toolbarTextColor = in.readInt();
        this.toolbarIconColor = in.readInt();
        this.progressBarColor = in.readInt();
        this.backgroundColor = in.readInt();
        this.isCameraOnly = in.readByte() != 0;
        this.isMultipleMode = in.readByte() != 0;
        this.isFolderMode = in.readByte() != 0;
        this.isShowCamera = in.readByte() != 0;
        this.maxSize = in.readInt();
        this.doneTitle = in.readString();
        this.folderTitle = in.readString();
        this.imageTitle = in.readString();
        this.savePath = in.readParcelable(SavePath.class.getClassLoader());
        this.selectedImages = in.createTypedArrayList(Image.CREATOR);
    }

    public int getToolbarColor() {
        return toolbarColor;
    }

    public void setToolbarColor(int toolbarColor) {
        this.toolbarColor = toolbarColor;
    }

    public int getStatusBarColor() {

        return statusBarColor;
    }

    public void setStatusBarColor(int statusBarColor) {
        this.statusBarColor = statusBarColor;
    }

    public int getToolbarTextColor() {
        return toolbarTextColor;
    }

    public void setToolbarTextColor(int toolbarTextColor) {
        this.toolbarTextColor = toolbarTextColor;
    }

    public int getToolbarIconColor() {
        return toolbarIconColor;
    }

    public void setToolbarIconColor(int toolbarIconColor) {
        this.toolbarIconColor = toolbarIconColor;
    }

    public int getProgressBarColor() {

        return progressBarColor;
    }

    public void setProgressBarColor(int progressBarColor) {
        this.progressBarColor = progressBarColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isCameraOnly() {
        return isCameraOnly;
    }

    public void setCameraOnly(boolean cameraOnly) {
        isCameraOnly = cameraOnly;
    }

    public boolean isMultipleMode() {
        return isMultipleMode;
    }

    public void setMultipleMode(boolean multipleMode) {
        isMultipleMode = multipleMode;
    }

    public boolean isFolderMode() {
        return isFolderMode;
    }

    public void setFolderMode(boolean folderMode) {
        isFolderMode = folderMode;
    }

    public boolean isShowCamera() {
        return isShowCamera;
    }

    public void setShowCamera(boolean showCamera) {
        isShowCamera = showCamera;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getDoneTitle() {
        return doneTitle;
    }

    public void setDoneTitle(String doneTitle) {
        this.doneTitle = doneTitle;
    }

    public String getFolderTitle() {
        return folderTitle;
    }

    public void setFolderTitle(String folderTitle) {
        this.folderTitle = folderTitle;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public SavePath getSavePath() {
        return savePath;
    }

    public void setSavePath(SavePath savePath) {
        this.savePath = savePath;
    }

    public ArrayList<Image> getSelectedImages() {
        return selectedImages;
    }

    public void setSelectedImages(ArrayList<Image> selectedImages) {
        this.selectedImages = selectedImages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.toolbarColor);
        dest.writeInt(this.statusBarColor);
        dest.writeInt(this.toolbarTextColor);
        dest.writeInt(this.toolbarIconColor);
        dest.writeInt(this.progressBarColor);
        dest.writeInt(this.backgroundColor);
        dest.writeByte(this.isCameraOnly ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isMultipleMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFolderMode ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isShowCamera ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSize);
        dest.writeString(this.doneTitle);
        dest.writeString(this.folderTitle);
        dest.writeString(this.imageTitle);
        dest.writeParcelable(this.savePath, flags);
        dest.writeTypedList(this.selectedImages);
    }
}

