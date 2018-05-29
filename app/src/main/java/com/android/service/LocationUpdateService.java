package com.android.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.BuildConfig;
import com.android.R;
import com.android.retrofit.RetrofitClient;
import com.android.retrofit.repository.ApiService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class LocationUpdateService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "LocationUpdateService";
    private static final long INTERVAL = 1000 * 30; // 30 seconds
    private static Context mContext;
    private static LocationUpdateService locationService;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private boolean notifyVisible;
    private ApiService retrofitClient;

    public LocationUpdateService() {

    }

    public static void startService(Context context) {
        Intent callIntent = new Intent(context, LocationUpdateService.class);
        context.startService(callIntent);
    }

    public static void stopService(Context context) {
        Intent callIntent = new Intent(context, LocationUpdateService.class);
        context.stopService(callIntent);
    }

    protected static void log(String string) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, string);
    }

    public LocationUpdateService getInstance() {
        if (locationService == null)
            locationService = new LocationUpdateService();
        return locationService;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setSmallestDisplacement(10.0f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mContext == null)
            mContext = this;
        init();
        retrofitClient = RetrofitClient.with(this).getClient(com.android.BuildConfig.BASE_URL).create(ApiService.class);
        notifyVisible = false;
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        createLocationRequest();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        log("onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            generateNotification(1);
            stopService(mContext);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        log("Location update started ..............: ");
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        Boolean enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (enable) {
            notifyVisible = false;
        }
        return enable;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        log("Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        log("Firing onLocationChanged................    latitude=  " + location.getLatitude() + "    longitude=   " + location.getLongitude() + "  " + mLastUpdateTime);
        handleNotifications();
    }

    public void handleNotifications() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            generateNotification(1);
        } else if (!isGPSEnabled()) {
            generateNotification(2);
        }
    }

    private void generateNotification(int i) {
        if (!notifyVisible) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
//                    .setSmallIcon(R.mipmap.logo)
                    .setAutoCancel(true);
            Intent mainIntent;
            String message;
            if (i == 1) {
                mBuilder.setContentTitle(mContext.getString(R.string.app_name));
                message = mContext.getString(R.string.please_relaunch) + mContext.getString(R.string.app_name);
//                mainIntent = new Intent(mContext, SplashActivity.class);
//                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } else {
                mBuilder.setContentTitle(mContext.getString(R.string.app_name) + mContext.getString(R.string.gps));
                message = mContext.getString(R.string.check_gps);
                mainIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            }
            mBuilder.setContentText(message);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            Uri NotiSound = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(NotiSound);
            long[] vibrate = {600, 100, 100, 700};
            mBuilder.setVibrate(vibrate);
//            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 12, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) mContext
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(i, mBuilder.build());
            notifyVisible = true;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
