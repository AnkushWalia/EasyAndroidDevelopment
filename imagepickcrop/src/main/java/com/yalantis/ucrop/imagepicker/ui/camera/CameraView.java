package com.yalantis.ucrop.imagepicker.ui.camera;

import com.yalantis.ucrop.imagepicker.model.Image;
import com.yalantis.ucrop.imagepicker.ui.common.MvpView;

import java.util.List;

/**
 * Created by hoanglam on 8/22/17.
 */

public interface CameraView extends MvpView {

    void finishPickImages(List<Image> images);
}
