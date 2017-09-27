package com.android.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import com.android.R;
import com.android.utils.ImageUtils;
import com.google.gson.JsonObject;

import java.io.File;
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
        ImageUtils.with(this, new ImageUtils.ImageSelectCallback() {
            @Override
            public void onImageSelected(File file, Bitmap bitmap) {
                profile.setImageBitmap(bitmap);
            }
        }).show();
        loadProfileWithRxJava();
    }


    public void loadProfileWithRxJava() {
        startProgressDialog();
        HashMap<String, String> jsonbody = new HashMap<String, String>();
        jsonbody.put("userID", "33");
        retrofitClient.getFriendsList("1412720555490099","EAARZCWaCbHv8BAJMGqEXcX6tiGZAq2sfTox5C8Ub7wV1SLpaZApBVZASbjRwHA8ieIM5fLpAN2WSlVFEzxEXI1J92PikcbpVjT9hGdMDv1xKZBE3HBZB4YoJRtAHel0NUB1vBvIqgkae8RZCZCGazOk3DqMXCyrrFREZAAUkSMLWP9wZDZD",25,null).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
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
