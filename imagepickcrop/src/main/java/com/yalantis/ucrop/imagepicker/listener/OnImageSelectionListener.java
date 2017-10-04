package com.yalantis.ucrop.imagepicker.listener;

import com.yalantis.ucrop.imagepicker.model.Image;

import java.util.List;

/**
 * Created by hoanglam on 8/18/17.
 */

public interface OnImageSelectionListener {
    void onSelectionUpdate(List<Image> images);
}
