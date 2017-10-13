package com.android.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.android.R;
import com.google.gson.JsonObject;
import com.yalantis.ucrop.imagepicker.model.Image;
import com.yalantis.ucrop.util.ImageUtils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        checkSelfPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
//            @Override
//            public void permGranted() {
//                LocationUtil.with(MainActivity.this, new LocationUtil.LocationUpdateListener() {
//                    @Override
//                    public void onLocationChanged(Location location) {
//                        Toast.makeText(MainActivity.this, "location : " + location.getLatitude(), Toast.LENGTH_SHORT).show();
//                    }
//                }).doContinuousLocation(true);
//            }
//
//            @Override
//            public void permDenied() {
//
//            }
//        });
        profile = (ImageView) findViewById(R.id.profile);

        //loadProfileWithRxJava();
    }


    public void loadProfileWithRxJava() {
        startProgressDialog();
        HashMap<String, String> jsonbody = new HashMap<String, String>();
        jsonbody.put("userID", "33");
        retrofitClient.getFriendsList("", "", 25, null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull JsonObject soAnswersResponse) {
                        stopProgressDialog();
                        log(soAnswersResponse + "");
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        handleError(e, new RetryClickListener() {
                            @Override
                            public void onActionClicked() {
                                loadProfileWithRxJava();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void clickEvent(View view) {
        checkSelfPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionCallback() {
            @Override
            public void permGranted() {
                ImageUtils.with(MainActivity.this, getString(R.string.app_name), new ImageUtils.ImageSelectCallback() {
                    @Override
                    public void onImageSelected(ArrayList<Image> imageData) {
                        profile.setImageBitmap(imageData.get(0).getBitmap());
                    }
                }).setToolbarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)).show();
            }

            @Override
            public void permDenied() {

            }
        });

    }
}
