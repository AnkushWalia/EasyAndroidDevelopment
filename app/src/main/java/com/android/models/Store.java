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
package com.android.models;

/**
 * The store where your app is published. Settable with setStore(Store). Default is Google Play.
 *
 * @author Pietro Rampini (rampini.pietro@gmail.com)
 * @see
 */
public class Store {
    public static final Store GOOGLE_PLAY = new Store(0);
    public static final Store AMAZON = new Store(1);

    int mStore;

    public Store(int store) {
        mStore = store;
    }
}