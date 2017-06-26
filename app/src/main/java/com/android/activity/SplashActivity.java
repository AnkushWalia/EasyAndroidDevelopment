package com.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.R;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
