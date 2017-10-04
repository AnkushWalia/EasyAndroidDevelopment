package com.yalantis.ucrop.imagepicker.ui.camera;


import com.yalantis.ucrop.imagepicker.model.Image;

import java.util.List;

public interface OnImageReadyListener {
    void onImageReady(List<Image> images);
}
