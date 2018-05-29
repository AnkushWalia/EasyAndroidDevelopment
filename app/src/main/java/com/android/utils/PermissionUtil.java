package com.android.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;

import com.android.R;
import com.android.view.base.BaseActivity;

public final class PermissionUtil {


    private static Dialog dialog;

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, BaseActivity context, BaseActivity.PermissionCallback permCallback) {
        boolean permGrantedBool = false;
        switch (requestCode) {
            case AppConstants.REQUEST_CODE:
                for (int grantResult : grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        context.showSnackBar(context.getString(R.string.not_sufficient_permissions)
                                + context.getString(R.string.app_name)
                                + context.getString(R.string.permissions));
                        permGrantedBool = false;
                        break;
                    } else {
                        permGrantedBool = true;
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool)
                        permCallback.permGranted();
                    else {
                        showPermissionDialog(context);
                        permCallback.permDenied();
                    }
                }
                break;
        }
    }

    public static void checkWriteSettingPermission(Activity context, BaseActivity.PermissionCallback permCallback) {
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
                context.startActivityForResult(intent, AppConstants.REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, AppConstants.REQUEST_CODE);
            }
        }
    }

    private static void showPermissionDialog(BaseActivity context) {
        if (dialog != null && dialog.isShowing())
            return;
        AlertDialog.Builder alertDialogBuilder;
        alertDialogBuilder = new AlertDialog.Builder(context, R.style.DialogStyle);
        alertDialogBuilder.setTitle("Permissions Required")
                .setMessage("You have forcefully denied some of the required permissions " +
                        "for this action. Please open settings, go to permissions and allow them.")
                .setPositiveButton(Html.fromHtml("<font color='#000000'>Settings</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='#000000'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtil.dialog = null;
                        showPermissionDialog(context);
                    }
                })
                .setCancelable(false);

        dialog = alertDialogBuilder.create();
        dialog.show();
    }

}
