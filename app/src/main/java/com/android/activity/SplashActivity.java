package com.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.R;
import com.android.utils.CheckAppUpdate;

public class SplashActivity extends BaseActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(runnable, 3000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            CheckAppUpdate.with(SplashActivity.this).appHasUpdateVersion(new ActionListener() {
//                @Override
//                public void onActionResult() {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
//                }
//            });
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
