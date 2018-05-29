package com.android.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.android.R;
import com.android.view.base.BaseActivity;

public class SplashActivity extends BaseActivity {
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this, MvvmActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void initUI() {
//        CheckAppUpdate.with(SplashActivity.this).appHasUpdateVersion(new CheckAppUpdate.ActionListener() {
//            @Override
//            public void onActionResult() {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }


}
