/*
* author: Bartlomiej Kierys
* date: 2016-11-09
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine.sample;

import android.app.Application;

import com.github.bskierys.pine.Pine;

import timber.log.Timber;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();

        // plant a pine at the beginning of application lifecycle
        if (BuildConfig.DEBUG) {
            Timber.plant(new Pine(this, "pine"));
        }
    }
}
