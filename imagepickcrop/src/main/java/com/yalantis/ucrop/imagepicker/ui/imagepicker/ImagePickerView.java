package com.yalantis.ucrop.imagepicker.ui.imagepicker;

import com.yalantis.ucrop.imagepicker.model.Folder;
import com.yalantis.ucrop.imagepicker.model.Image;
import com.yalantis.ucrop.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/17/17.
 */

public interface ImagePickerView extends MvpView {

    void showLoading(boolean isLoading);

    void showFetchCompleted(List<Image> images, List<Folder> folders);

    void showError(Throwable throwable);

    void showEmpty();

    void showCapturedImage();

    void finishPickImages(List<Image> images);

}