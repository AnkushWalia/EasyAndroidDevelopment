package com.yalantis.ucrop.imagepicker.ui.camera;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.yalantis.ucrop.imagepicker.helper.ImageHelper;
import com.yalantis.ucrop.imagepicker.model.Config;

import java.io.File;
import java.io.Serializable;

/**
 * Created by hoanglam on 8/18/17.
 */

public class DefaultCameraModule implements CameraModule, Serializable {

    protected String imagePath;


    @Override
    public Intent getCameraIntent(Context context, Config config) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new ImageHelper().createImageFile(config.getSavePath());
        if (imageFile != null) {
            Context appContext = context.getApplicationContext();
            // String providerName = String.format(Locale.ENGLISH, "%s%s", appContext.getPackageName());
            Uri uri = getUriForFile(appContext, imageFile);
            imagePath = imageFile.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            ImageHelper.grantAppPermission(context, intent, uri);
            return intent;
        }
        return null;
    }

    private static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                String packageId = context.getPackageName();
                return FileProvider.getUriForFile(context, packageId, file);
            } catch (IllegalArgumentException e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    throw new SecurityException();
                } else {
                    return Uri.fromFile(file);
                }
            }
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public void getImage(final Context context, Intent intent, final OnImageReadyListener imageReadyListener) {
        if (imageReadyListener == null) {
            throw new IllegalStateException("OnImageReadyListener must not be null");
        }

        if (imagePath == null) {
            imageReadyListener.onImageReady(null);
            return;
        }

        final Uri imageUri = Uri.parse(imagePath);
        if (imageUri != null) {
            MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{imageUri.getPath()}
                    , null
                    , new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            if (path != null) {
                                path = imagePath;
                            }
                            imageReadyListener.onImageReady(ImageHelper.singleListFromPath(path));
                            ImageHelper.revokeAppPermission(context, imageUri);
                        }
                    });
        }
    }
}
