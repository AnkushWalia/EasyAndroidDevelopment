package com.android.utils;

import android.content.Context;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * Created by WaliaAnkush on 4/30/2018.
 */

public final class PicassoUtils {

    private static Picasso picasso;

    private static Picasso with(Context context) {
        if (picasso != null)
            return picasso;
        OkHttpClient okHttpClient = new OkHttpClient();
        File customCacheDirectory = new File(context.getCacheDir(), "PicassoCache");
        okHttpClient.setCache(new Cache(customCacheDirectory, 10 * 1024 * 1024)); //10 MB
        OkHttpDownloader okHttpDownloader = new OkHttpDownloader(okHttpClient);
        picasso = new Picasso.Builder(context).downloader(okHttpDownloader).build();
        return picasso;
    }

}
