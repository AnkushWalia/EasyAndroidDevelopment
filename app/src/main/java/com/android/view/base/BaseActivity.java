package com.android.view.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.R;
import com.android.retrofit.RetrofitErrorHandle;
import com.android.retrofit.repository.ProjectRepository;
import com.android.utils.AppConstants;
import com.android.utils.CommonUtils;
import com.android.utils.KeyboardUtils;
import com.android.utils.LocationUtil;
import com.android.utils.NetworkUtils;
import com.android.utils.PermissionUtil;
import com.android.utils.PrefStore;
import com.android.utils.SnackBarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.yalantis.ucrop.util.ImageUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, BaseFragment.Callback {

    public PrefStore store;
    public PermissionCallback permCallback;
    public ProjectRepository projectRepository;
    private final NetworkErrorReceiver networkErrorReceiver = new NetworkErrorReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (BaseActivity.this).overridePendingTransition(R.anim.slide_in,
                R.anim.slide_out);
        projectRepository = ProjectRepository.getInstance(this);
        store = new PrefStore(this);
        strictModeThread();
        transitionSlideInHorizontal();
        initializeNetworkBroadcast();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void showLoading() {
        CommonUtils.showProgressDialog(this);
    }

    public void hideLoading() {
        CommonUtils.hideProgressDialog();
    }


    public void showSnackBar(String message) {
        SnackBarManager.showSnackBar(message, this);
    }

    public void showSnackBar(String message, String actionLabel, ActionClickListener actionClickListener) {
        SnackBarManager.showSnackBar(message, actionLabel, actionClickListener, this);
    }

    public void onError(String message) {
        if (message != null) {
            showSnackBar(message);
        }
    }

    public void onError(@StringRes int resId) {
        onError(getString(resId));
    }

    public void showMessage(String message) {
        CommonUtils.showToast(this, message);
    }

    public void showMessage(@StringRes int resId) {
        showMessage(getString(resId));
    }

    public boolean isNetworkConnected() {
        return NetworkUtils.isNetworkConnected(getApplicationContext());
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    public void hideKeyboard() {
        KeyboardUtils.hideSoftInput(this);
    }

    public void hideKeyboard(Dialog dialog) {
        KeyboardUtils.hideSoftInput(dialog);
    }

    public void openActivityOnTokenExpire() {
        //   startActivity(LoginActivity.getStartIntent(this));
        finish();
    }

    protected abstract void initUI();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageUtils.onActivityResult(requestCode, resultCode, data);
        LocationUtil.onActivityResult(requestCode, resultCode);
        if (requestCode == AppConstants.REQUEST_CODE) {
            // ---------------------------- Write Setting  ---------------------
        }
    }

    public void checkSelfPermission(String[] perms, PermissionCallback permCallback) {
        this.permCallback = permCallback;
        ActivityCompat.requestPermissions(this, perms, AppConstants.REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this, permCallback);
    }

    public void log(String message) {
        CommonUtils.log(this, message);
    }

    public void exitFromApp() {
        finish();
    }

    private void strictModeThread() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitAll().build());
    }

    public void transitionSlideInHorizontal() {
        this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {

    }

    public void handleError(Throwable throwable, ActionClickListener actionClickListener) {
        RetrofitErrorHandle.handleError(BaseActivity.this, throwable, actionClickListener);
    }


    public interface PermissionCallback {
        void permGranted();

        void permDenied();
    }

    private void initializeNetworkBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(networkErrorReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkErrorReceiver);
    }

    public class NetworkErrorReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtils.getConnectivityStatusString(context);
            showNetworkAlert(status);
        }
    }

    public void showNetworkAlert(String status) {
        showSnackBar(status, getString(R.string.retry), snackbar -> {
            snackbar.dismiss();
            if (!NetworkUtils.isNetworkConnected(this)) {
                showNetworkAlert(status);
            }
        });
    }
}
