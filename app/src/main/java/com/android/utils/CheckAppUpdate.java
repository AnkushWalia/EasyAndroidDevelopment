/*
 * Copyright (C) 2014 Pietro Rampini - PiKo Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

import com.android.R;
import com.android.activity.BaseActivity;
import com.android.models.Store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.params.BasicHttpParams;
import cz.msebera.android.httpclient.params.HttpConnectionParams;
import cz.msebera.android.httpclient.params.HttpParams;


/**
 * Heart of the library. Check if an update is available for download parsing the desktop Play Store/Amazon App Store page of your app.
 *
 * @author Pietro Rampini (rampini.pietro@gmail.com)
 */
public class CheckAppUpdate extends AsyncTask<String, Integer, Integer> {
    private static final String PLAY_STORE_ROOT_WEB = "https://play.google.com/store/apps/details?id=";
    private static final String PLAY_STORE_HTML_TAGS_TO_GET_RIGHT_POSITION = "itemprop=\"softwareVersion\"> ";
    private static final String PLAY_STORE_HTML_TAGS_TO_REMOVE_USELESS_CONTENT = "  </div> </div>";
    private static final String PLAY_STORE_PACKAGE_NOT_PUBLISHED_IDENTIFIER = "We're sorry, the requested URL was not found on this server.";
    private static final String AMAZON_STORE_ROOT_WEB = "http://www.amazon.com/gp/mas/dl/android?p=";
    private static final String AMAZON_STORE_HTML_TAGS_TO_GET_RIGHT_LINE = "<li><strong>Version:</strong>";
    private static final String AMAZON_STORE_PACKAGE_NOT_PUBLISHED_IDENTIFIER = "<title>Amazon.com: Apps for Android</title>";

    private static final int VERSION_DOWNLOADABLE_FOUND = 0;
    private static final int MULTIPLE_APKS_PUBLISHED = 1;
    private static final int NETWORK_ERROR = 2;
    private static final int PACKAGE_NOT_PUBLISHED = 3;
    private static final int STORE_ERROR = 4;
    private BaseActivity.ActionListener actionListener;

    private Store mStore = Store.GOOGLE_PLAY;
    private BaseActivity mContext;
    private String mVersionDownloadable;
    private static CheckAppUpdate checkAppUpdate;

    public static CheckAppUpdate with(BaseActivity activity) {
        if (checkAppUpdate == null)
            checkAppUpdate = new CheckAppUpdate();
        checkAppUpdate.mContext = activity;
        return checkAppUpdate;
    }


    public void appHasUpdateVersion(Store store, BaseActivity.ActionListener actionListener) {
        this.actionListener = actionListener;
        this.mStore = store;
        checkAppUpdate.execute();
    }

    public void appHasUpdateVersion(BaseActivity.ActionListener actionListener) {
        this.actionListener = actionListener;
        checkAppUpdate.execute();
    }

    @Override
    protected Integer doInBackground(String... notused) {
        if (mContext.store.getBoolean("DON'T SHOW AGAIN", false)) {
            return NETWORK_ERROR;
        } else if (mContext.isNetworkAvailable()) {
            try {
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params, 4000);
                HttpConnectionParams.setSoTimeout(params, 5000);
                HttpClient client = new DefaultHttpClient(params);
                if (mStore == Store.GOOGLE_PLAY) {
                    HttpGet request = new HttpGet(PLAY_STORE_ROOT_WEB + mContext.getPackageName()); // Set the right Play Store page by getting package name.
                    HttpResponse response = client.execute(request);
                    InputStream is = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(PLAY_STORE_HTML_TAGS_TO_GET_RIGHT_POSITION)) { // Obtain HTML line contaning version available in Play Store
                            String containingVersion = line.substring(line.lastIndexOf(PLAY_STORE_HTML_TAGS_TO_GET_RIGHT_POSITION) + 28);  // Get the String starting with version available + Other HTML tags
                            String[] removingUnusefulTags = containingVersion.split(PLAY_STORE_HTML_TAGS_TO_REMOVE_USELESS_CONTENT); // Remove useless HTML tags
                            mVersionDownloadable = removingUnusefulTags[0]; // Obtain version available
                        } else if (line.contains(PLAY_STORE_PACKAGE_NOT_PUBLISHED_IDENTIFIER)) { // This packages has not been found in Play Store
                            return PACKAGE_NOT_PUBLISHED;
                        }
                    }
                    if (mVersionDownloadable == null) {
                        return STORE_ERROR;
                    } else if (containsNumber(mVersionDownloadable)) {
                        return VERSION_DOWNLOADABLE_FOUND;
                    } else {
                        return MULTIPLE_APKS_PUBLISHED;
                    }
                } else if (mStore == Store.AMAZON) {
                    HttpGet request = new HttpGet(AMAZON_STORE_ROOT_WEB + mContext.getPackageName()); // Set the right Amazon App Store page by getting package name.
                    HttpResponse response = client.execute(request);
                    InputStream is = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains(AMAZON_STORE_HTML_TAGS_TO_GET_RIGHT_LINE)) { // Obtain HTML line contaning version available in Amazon App Store
                            String versionDownloadableWithTags = line.substring(38); // Get the String starting with version available + Other HTML tags
                            mVersionDownloadable = versionDownloadableWithTags.substring(0, versionDownloadableWithTags.length() - 5); // Remove useless HTML tags
                            return VERSION_DOWNLOADABLE_FOUND;
                        } else if (line.contains(AMAZON_STORE_PACKAGE_NOT_PUBLISHED_IDENTIFIER)) { // This packages has not been found in Amazon App Store
                            return PACKAGE_NOT_PUBLISHED;
                        }
                    }
                }
            } catch (IOException connectionError) {
                //   Network.logConnectionError();
                return NETWORK_ERROR;
            }
        } else {
            return NETWORK_ERROR;
        }
        return null;
    }


    @Override
    protected void onPostExecute(Integer result) {
        mContext.log("onPostExecute: " + result + "     " + mVersionDownloadable);
        if (result == VERSION_DOWNLOADABLE_FOUND) {
            try {
                if (Integer.parseInt(mVersionDownloadable.replace(".", "")) > Integer.parseInt(getVersionName().replace(".", "")))
                    showDialogUpdateApp();
                else if (actionListener != null) actionListener.onActionResult();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (actionListener != null) actionListener.onActionResult();
    }

    private String getVersionName() throws PackageManager.NameNotFoundException {
        return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
    }

    private void showDialogUpdateApp() {
        SpannableStringBuilder sb = new SpannableStringBuilder("New update available!");
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(bss, 0, "New update available!".length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        new AlertDialog.Builder(mContext).setTitle(sb.toString()).setMessage("Update " + mVersionDownloadable + " is available to download.Downloading the latest update you will get the latest features,improvements and bug fixes of " + mContext.getString(R.string.app_name)).setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mStore == Store.GOOGLE_PLAY)
                    gotoUpdatePlaystoreApp();
                else
                    gotoUpdateAmazonApp();
                dialog.dismiss();
                mContext.finish();
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (actionListener != null) actionListener.onActionResult();
            }
        }).setNeutralButton("DON'T SHOW AGAIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mContext.store.setBoolean("DON'T SHOW AGAIN", true);
                dialog.dismiss();
                if (actionListener != null) actionListener.onActionResult();
            }
        }).setCancelable(false).show();
    }


    private boolean containsNumber(String string) {
        return string.matches(".*[0-9].*");
    }

    private void gotoUpdatePlaystoreApp() {
        final String appPackageName = mContext.getPackageName();
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void gotoUpdateAmazonApp() {
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("amzn://apps/android?p=" + mContext.getPackageName())));
        } catch (android.content.ActivityNotFoundException anfe) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + mContext.getPackageName())));
        }

    }
}
