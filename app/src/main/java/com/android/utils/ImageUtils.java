package com.android.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;


import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private final int GALLERY_REQ = 0x2222;
    private final int CAMERA_REQ = 0x3333;
    private static final int IMAGE_QUALITY = 80; //Max 100
    private final long MIN_COMPRESS_SIZE = 100;
    private Activity activity;
    private static ImageUtils imageUtils;
    private ImageSelectCallback imageSelectCallback;
    private boolean onlyCamera;
    private boolean onlyGallery;
    private boolean doCrop = true;
    private boolean isImageCompress = true;
    private int width;
    private int height;
    private Uri cropUri;
    private Uri cameraUri;


    public static ImageUtils with(Activity activity, ImageSelectCallback imageSelectCallback) {
        if (imageUtils == null)
            imageUtils = new ImageUtils();
        imageUtils.setCallBacks(activity, imageSelectCallback);
        return imageUtils;
    }

    private void setCallBacks(Activity activity, ImageSelectCallback imageSelectCallback) {
        this.activity = activity;
        this.imageSelectCallback = imageSelectCallback;
    }

    ImageUtils onlyCamera(boolean onlyCamera) {
        this.onlyCamera = onlyCamera;
        return this;
    }

    ImageUtils onlyGallery(boolean onlyGallery) {
        this.onlyGallery = onlyGallery;
        return this;
    }

    ImageUtils doCrop(boolean isCrop) {
        this.doCrop = isCrop;
        return this;
    }

    ImageUtils doImageCompress(boolean isImageCompress) {
        this.isImageCompress = isImageCompress;
        return this;
    }

    ImageUtils cropAspectRatio(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public void show() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "Write External storage Permission not specified", Toast.LENGTH_SHORT).show();
            return;
        }
        if (onlyCamera) {
            captureCameraImage();
        } else if (onlyGallery) {
            selectGalleryImage();
        } else {
            selectImageDialog();
        }
    }


    public interface ImageSelectCallback {
        void onImageSelected(File file, Bitmap bitmap);

    }

    private void selectImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose Image Resource");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        selectGalleryImage();
                        break;
                    case 1:
                        captureCameraImage();
                        break;
                }
            }
        });
        builder.show();
    }

    private void captureCameraImage() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(activity.getCacheDir(), "camera_image.jpg");
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            cameraUri = getUriForFile(activity, imageFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            activity.startActivityForResult(takePictureIntent, CAMERA_REQ);
        }
    }

    private void selectGalleryImage() {
        try {
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent1.setType("image/*");
            activity.startActivityForResult(intent1, GALLERY_REQ);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(activity, "No app found to perform this action", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(activity, "No app found to perform this action", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (imageUtils != null)
            imageUtils.setResults(requestCode, resultCode, data);
    }

    private void setResults(int requestCode, int resultCode, Intent data) {
        if (requestCode == GALLERY_REQ && resultCode == Activity.RESULT_OK) {
            if (doCrop) {
                Crop(data.getData(), getUriForFile(activity, new File(activity.getCacheDir(), "gallery_crop_image.jpg")));
            } else {
                imageCompressFromPath(getRealPath(data.getData(), activity));
            }
        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK) {
            if (doCrop) {
                Crop(cameraUri, getUriForFile(activity, new File(activity.getCacheDir(), "camera_crop_image.jpg")));
            } else {
                imageCompressFromPath(getRealPath(cameraUri, activity));
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK)
            imageCompressFromPath(getRealPath(cropUri, activity));
        else if (resultCode == UCrop.RESULT_ERROR)
            Toast.makeText(activity, "image corrupted please select another one", Toast.LENGTH_SHORT).show();

    }

    private void Crop(Uri inputUri, Uri outputUri) {
        this.cropUri = outputUri;
        if (width != 0) {
            UCrop.of(inputUri, outputUri)
                    .withAspectRatio(width, height)
                    .start(activity);
        } else {
            UCrop.of(inputUri, outputUri)
                    .start(activity);
        }
    }

    static File bitmapToFile(Bitmap bitmap, Context activity) {
        File f = new File(activity.getCacheDir(), "CompressedImage.jpg");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return f;
    }

    void imageCompressFromPath(String picturePath) {
        File actualFile = new File(picturePath);
        if (!isImageCompress || actualFile.length() / 1024 <= MIN_COMPRESS_SIZE) {
            Log.e("---NothingCompress--", actualFile.length() / 1024 + " KB");
            imageSelectCallback.onImageSelected(actualFile, fileToBitmap(actualFile));
        } else {
            Log.e("---ActualFileSize-- ", actualFile.length() / 1024 + " KB");
            Bitmap bitmap = imageCompress(picturePath, 816.0f, 612.0f);
            File compressedFile = bitmapToFile(bitmap, activity);
            Log.e("-CompressedFileSize-- ", compressedFile.length() / 1024 + " KB");
            imageSelectCallback.onImageSelected(compressedFile, bitmap);
        }
    }

    static Bitmap fileToBitmap(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    static String getRealPath(Uri uri, Context activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(activity, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(activity, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(activity, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            else if (isCachePhotoUri(uri))
                return activity.getCacheDir() + File.separator + uri.getLastPathSegment();
            else if (isExternalStoragePath(uri))
                return Environment.getExternalStorageDirectory() + File.separator + uri.getPath().split("/external_files/")[1];
            else {
                if (getDataColumn(activity, uri, null, null) != null)
                    return getDataColumn(activity, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }

    private static boolean isExternalStoragePath(Uri uri) {
        return uri.getPath().contains("external_files/");
    }

    private static boolean isCachePhotoUri(Uri uri) {
        return uri.getPath().contains("cache/");
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (Exception ignored) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static Bitmap imageCompress(String picturePath, float maxHeight, float maxWidth) {

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bmp = BitmapFactory.decodeFile(picturePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        } else {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeFile(picturePath);
            bitmap = Bitmap.createScaledBitmap(bitmap, actualWidth, actualHeight, true);
            return bitmap;
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;

        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(picturePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);


        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        bmp.recycle();
        ExifInterface exif;
        try {
            exif = new ExifInterface(picturePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int heightRatio = Math.round((float) height / (float) reqHeight);
            int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        float totalPixels = width * height;
        float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Bitmap image, Context context) {

        float BITMAP_SCALE = 0.4f;
        float BLUR_RADIUS = 5f;
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = null;
        theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

}
