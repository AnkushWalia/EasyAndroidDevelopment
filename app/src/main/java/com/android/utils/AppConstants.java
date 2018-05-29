/*
 * Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://mindorks.com/license/apache-v2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.utils;

import com.android.BuildConfig;

/**
 * Created by amitshekhar on 08/01/17.
 */

public final class AppConstants {
    public static final String DISPLAY_MESSAGE_ACTION = BuildConfig.APPLICATION_ID + ".DISPLAY_MESSAGE";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1234;
    public static final String STATUS_CODE_SUCCESS = "success";
    public static final String STATUS_CODE_FAILED = "failed";

    public static final int API_STATUS_CODE_LOCAL_ERROR = 0;

    public static final String TIMESTAMP_FORMAT = "yyyyMMdd_HHmmss";

    public static final int REQUEST_CODE = 99;


    private AppConstants() {
        // This utility class is not publicly instantiable
    }
}
