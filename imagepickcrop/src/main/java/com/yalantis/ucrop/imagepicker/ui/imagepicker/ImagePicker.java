/*
 * Created by Nguyen Hoang Lam
 * Date: ${DATE}
 */

package com.yalantis.ucrop.imagepicker.ui.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.imagepicker.model.Config;
import com.yalantis.ucrop.imagepicker.model.Image;
import com.yalantis.ucrop.imagepicker.model.SavePath;
import com.yalantis.ucrop.imagepicker.ui.camera.CameraActivty;

import java.util.ArrayList;

/**
 * Created by hoanglam on 8/4/16.
 */
public class ImagePicker {

    protected Config config;


    public ImagePicker(Builder builder) {
        config = builder.config;
    }

    public static Builder with(Activity activity) {
        return new ActivityBuilder(activity);
    }

    public static Builder with(Fragment fragment) {
        return new FragmentBuilder(fragment);
    }

    static class ActivityBuilder extends Builder {
        private Activity activity;

        public ActivityBuilder(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        public void start() {
            Intent intent;
            if (!config.isCameraOnly()) {
                intent = new Intent(activity, ImagePickerActivity.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                activity.startActivityForResult(intent, Config.RC_PICK_IMAGES);
            } else {
                intent = new Intent(activity, CameraActivty.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                activity.overridePendingTransition(0, 0);
                activity.startActivityForResult(intent, Config.RC_PICK_IMAGES);
            }
        }
    }

    static class FragmentBuilder extends Builder {
        private Fragment fragment;

        public FragmentBuilder(Fragment fragment) {
            super(fragment);
            this.fragment = fragment;
        }

        @Override
        public void start() {
            Intent intent;
            if (!config.isCameraOnly()) {
                intent = new Intent(fragment.getActivity(), ImagePickerActivity.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                fragment.startActivityForResult(intent, Config.RC_PICK_IMAGES);
            } else {
                intent = new Intent(fragment.getActivity(), CameraActivty.class);
                intent.putExtra(Config.EXTRA_CONFIG, config);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                fragment.getActivity().overridePendingTransition(0, 0);
                fragment.startActivityForResult(intent, Config.RC_PICK_IMAGES);
            }
        }
    }

    public static abstract class Builder extends BaseBuilder {

        public Builder(Activity activity) {
            super(activity);
        }

        public Builder(Fragment fragment) {
            super(fragment.getContext());
        }

        public Builder setToolbarColor(int toolbarColor) {
            config.setToolbarColor(toolbarColor);
            return this;
        }

        public Builder setStatusBarColor(int statusBarColor) {
            config.setStatusBarColor(statusBarColor);
            return this;
        }

        public Builder setToolbarTextColor(int toolbarTextColor) {
            config.setToolbarTextColor(toolbarTextColor);
            return this;
        }

        public Builder setToolbarIconColor(int toolbarIconColor) {
            config.setToolbarIconColor(toolbarIconColor);
            return this;
        }

        public Builder setProgressBarColor(int progressBarColor) {
            config.setProgressBarColor(progressBarColor);
            return this;
        }

        public Builder setBackgroundColor(int backgroundColor) {
            config.setBackgroundColor(backgroundColor);
            return this;
        }

        public Builder setCameraOnly(boolean isCameraOnly) {
            config.setCameraOnly(isCameraOnly);
            return this;
        }

        public Builder setMultipleMode(boolean isMultipleMode) {
            config.setMultipleMode(isMultipleMode);
            return this;
        }

        public Builder setFolderMode(boolean isFolderMode) {
            config.setFolderMode(isFolderMode);
            return this;
        }

        public Builder setShowCamera(boolean isShowCamera) {
            config.setShowCamera(isShowCamera);
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }

        public Builder setDoneTitle(String doneTitle) {
            config.setDoneTitle(doneTitle);
            return this;
        }

        public Builder setFolderTitle(String folderTitle) {
            config.setFolderTitle(folderTitle);
            return this;
        }

        public Builder setImageTitle(String imageTitle) {
            config.setImageTitle(imageTitle);
            return this;
        }

        public Builder setSavePath(String path) {
            config.setSavePath(new SavePath(path, false));
            return this;
        }

        public Builder setSelectedImages(ArrayList<Image> selectedImages) {
            config.setSelectedImages(selectedImages);
            return this;
        }

        public abstract void start();

    }

    public static abstract class BaseBuilder {

        protected Config config;

        public BaseBuilder(Context context) {
            this.config = new Config();

            Resources resources = context.getResources();
            config.setCameraOnly(false);
            config.setMultipleMode(true);
            config.setFolderMode(true);
            config.setShowCamera(true);
            config.setMaxSize(Config.MAX_SIZE);
            config.setDoneTitle(resources.getString(R.string.imagepicker_action_done));
            config.setFolderTitle(resources.getString(R.string.imagepicker_title_folder));
            config.setImageTitle(resources.getString(R.string.imagepicker_title_image));
            config.setSavePath(SavePath.DEFAULT);
            config.setSelectedImages(new ArrayList<Image>());
        }
    }

}

