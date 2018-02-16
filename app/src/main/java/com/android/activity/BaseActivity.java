package com.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.BuildConfig;
import com.android.R;
import com.android.retrofit.ApiService;
import com.android.retrofit.RetrofitClient;
import com.android.utils.Const;
import com.android.utils.LocationUtil;
import com.android.utils.NetworkUtil;
import com.android.utils.PrefStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.util.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.HttpException;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static NetworksBroadcast networksBroadcast;
    public LayoutInflater inflater;
    public PrefStore store;
    public PermissionCallback permCallback;
    public ApiService retrofitClient;
    public static Picasso picasso;
    private Toast toast;
    private Dialog progressDialog;
    private InputMethodManager inputMethodManager;
    private Snackbar networkSnackbar;
    private static Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        (BaseActivity.this).overridePendingTransition(R.anim.slide_in,
                R.anim.slide_out);
        retrofitClient = RetrofitClient.with(this).getClient(BuildConfig.API_BASE_URL).create(ApiService.class);
        inputMethodManager = (InputMethodManager) this
                .getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
        store = new PrefStore(this);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        strictModeThread();
        transitionSlideInHorizontal();
        initializeProgressDialog();
        initializeNetworkBroadcast();
        initializePicassoDownloader();
    }

    private void initializePicassoDownloader() {
        if (picasso != null)
            return;
        OkHttpClient okHttpClient = new OkHttpClient();
        File customCacheDirectory = new File(getCacheDir(), "PicassoCache");
        okHttpClient.setCache(new Cache(customCacheDirectory, 10 * 1024 * 1024)); //10 MB
        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
        picasso = new Picasso.Builder(this).downloader(okHttpDownloader).build();
    }


    public void setActionBarTitleInCenter(String title) {
        View view = inflater.inflate(R.layout.custom_action_bar, null);
        TextView titleTV = (TextView) view.findViewById(R.id.titleTV);
        titleTV.setText(title);

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(view, params);


    }

    private void initializeNetworkBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        if (networksBroadcast == null)
            networksBroadcast = new NetworksBroadcast();
        registerReceiver(networksBroadcast, intentFilter);
    }

    private void showNetworkAlert(final String status) {
        showSnackBar(status, getString(R.string.retry), new ActionClickListener() {
            @Override
            public void onActionClicked(Snackbar snackbar) {
                snackbar.dismiss();
                if (!isNetworkAvailable()) {
                    showNetworkAlert(status);
                }
            }
        });
    }

    public String changeDateFormat(String dateString, String sourceDateFormat, String targetDateFormat) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }
        SimpleDateFormat inputDateFromat = new SimpleDateFormat(sourceDateFormat, Locale.getDefault());
        Date date = new Date();
        try {
            date = inputDateFromat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(targetDateFormat, Locale.getDefault());
        return outputDateFormat.format(date);
    }

    public String changeDateFormatFromDate(Date sourceDate, String targetDateFormat) {
        if (sourceDate == null || targetDateFormat == null || targetDateFormat.isEmpty()) {
            return "";
        }
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(targetDateFormat, Locale.getDefault());
        return outputDateFormat.format(sourceDate);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Const.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                log(getString(R.string.this_device_is_not_supported));
                finish();
            }
            return false;
        }
        return true;
    }

    public void checkWriteSettingPermission(Activity context, PermissionCallback permCallback) {
        this.permCallback = permCallback;
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            permCallback.permGranted();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, 123);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, 99);
            }
        }
    }

    public void checkSelfPermission(String[] perms, PermissionCallback permCallback) {
        this.permCallback = permCallback;
        ActivityCompat.requestPermissions(this, perms, 99);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permGrantedBool = false;
        switch (requestCode) {
            case 99:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, getString(R.string.not_sufficient_permissions)
                                + getString(R.string.app_name)
                                + getString(R.string.permissions), Toast.LENGTH_SHORT).show();
                        permGrantedBool = false;
                        break;
                    } else {
                        permGrantedBool = true;
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool)
                        permCallback.permGranted();
                    else
                        permCallback.permDenied();
                }
                break;
        }
    }

    interface PermissionCallback {
        void permGranted();

        void permDenied();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageUtils.onActivityResult(requestCode, resultCode, data);
        LocationUtil.onActivityResult(requestCode, resultCode);
        if (requestCode == 123) {
            // ---------------------------- Write Setting  ---------------------
        }
    }

    public void exitFromApp() {
        finish();
    }

    @SuppressLint("HardwareIds")
    public String getUniqueDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public boolean hideSoftKeyboard() {
        try {
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isValidMail(String email) {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!&^%$#@()=*/.+_-])(?=\\S+$).{8,}$");
    }

    public void keyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:>>>>>>>>>>>>>>>", "" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void log(String string) {
        if (BuildConfig.DEBUG)
            Log.e(getString(R.string.app_name), string);
    }

    private void initializeProgressDialog() {
        progressDialog = new Dialog(this, R.style.transparent_dialog_borderless);
        View view = View.inflate(this, R.layout.progress_dialog, null);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // txtMsgTV = (TextView) view.findViewById(R.id.txtMsgTV);
        progressDialog.setCancelable(false);
    }

    public void showSnackBar(String message, String action, ActionClickListener actionClickListener) {
        if (networkSnackbar != null && networkSnackbar.isShowing())
            networkSnackbar.dismiss();
        if (message == null)
            return;
        networkSnackbar = Snackbar.with(getApplicationContext()) // context
                .text(message) // text to be displayed
                .type(SnackbarType.MULTI_LINE)
                .swipeToDismiss(false)
                .position(Snackbar.SnackbarPosition.TOP)
                .dismissOnActionClicked(false)
                .textColor(Color.WHITE) // change the text color
                .textTypeface(Typeface.DEFAULT) // change the text font
                .animation(true)
//                        .color(Color.BLUE) // change the background color
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .actionLabel(action) // action button label
                .actionColor(Color.RED) // action button label color
                .actionLabelTypeface(Typeface.DEFAULT_BOLD) // change the action button font
                .actionListener(actionClickListener);// action button's ActionClickListener
        SnackbarManager.show(networkSnackbar, this); // activity where it is displayed

    }

    public void showSnackBar(String message) {
        if (message == null)
            return;
        SnackbarManager.show(
                Snackbar.with(getApplicationContext()) // context
                        .text(message) // text to be displayed
                        .type(SnackbarType.MULTI_LINE)
                        .swipeToDismiss(true)
                        .position(Snackbar.SnackbarPosition.BOTTOM)
                        .actionLabel(null)
                        .textColor(Color.WHITE) // change the text color
                        .textTypeface(Typeface.DEFAULT) // change the text font
                        .animation(true)
//                        .color(Color.BLUE) // change the background color
                        .duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                , this); // activity where it is displayed
    }

    public void startProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            try {
                progressDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showToast(String msg) {
        toast.setText(msg);
        toast.show();
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

    private void unregisterNetworkBroadcast() {
        try {
            if (networksBroadcast != null) {
                unregisterReceiver(networksBroadcast);
            }
        } catch (IllegalArgumentException e) {
            networksBroadcast = null;
        }
    }

    public Uri getUriForFile(Context context, File file) {
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkBroadcast();
    }


    public interface RetryClickListener {
        void onActionClicked();
    }

    public interface ActionListener {
        void onActionResult();
    }

    public class NetworksBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtil.getConnectivityStatusString(context);
            showNetworkAlert(status);
        }
    }

    public static String getStringFromArray(ArrayList<String> strings) {
        return gson.toJson(strings);
    }

    public static ArrayList<String> getArrayFromString(String time) {
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        return gson.fromJson(time, type);
    }

    public void handleError(Throwable throwable, final BaseActivity.RetryClickListener retryClickListener) {
        if (throwable instanceof HttpException) {
            HttpException response = (HttpException) throwable;
            int code = response.code();
            ResponseBody body = response.response().errorBody();
            Converter<ResponseBody, Error> errorConverter =
                    RetrofitClient.retrofit.responseBodyConverter(Error.class, new Annotation[0]);
            if (code == 403) {
                showSnackBar(throwable.getMessage());
                //---------------------------------------------------------------  Go TO Login Page Intent ---------------------------------------
            } else if (errorConverter != null && body != null) {
                try {
                    Error error = errorConverter.convert(body);
                    showSnackBar(error.getMessage());
                } catch (IOException e1) {
                    showSnackBar(throwable.getMessage());
                }
            } else
                showSnackBar(throwable.getMessage());
        } else if (throwable instanceof UnknownHostException || throwable instanceof SocketException) {
            showSnackBar("Internet unreachable. Please try after sometime.", "Retry", new ActionClickListener() {  //connection unavailable
                @Override
                public void onActionClicked(Snackbar snackbar) {
                    snackbar.dismiss();
                    if (retryClickListener != null)
                        retryClickListener.onActionClicked();
                }
            });
        } else {
            showSnackBar(throwable.getMessage());
        }
        stopProgressDialog();
    }
}
