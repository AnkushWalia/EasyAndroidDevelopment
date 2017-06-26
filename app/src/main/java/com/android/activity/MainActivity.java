package com.android.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.android.R;
import com.android.utils.ImageUtils;
import com.android.utils.LocationUtil;

import java.io.File;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

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

        ImageUtils.with(this, new ImageUtils.ImageSelectCallback() {
            @Override
            public void onImageSelected(File file, Bitmap bitmap) {

            }
        }).show();
        loadProfileWithRxJava();
    }


    public void loadProfileWithRxJava() {
        startProgressDialog();
        HashMap<String, String> jsonbody = new HashMap<String, String>();
        jsonbody.put("userID", "33");
        retrofitClient.userInfo(jsonbody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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

}
