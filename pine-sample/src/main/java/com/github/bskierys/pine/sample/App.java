/*
* author: Bartlomiej Kierys
* date: 2016-11-09
* email: bskierys@gmail.com
*/
package com.github.bskierys.pine.sample;

import android.app.Application;

import com.github.bskierys.pine.Pine;

import java.util.Locale;

import timber.log.Timber;

public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();

        // plant a pine at the beginning of application lifecycle
        if (BuildConfig.DEBUG) {
            Timber.plant(new Pine.Builder()
                                 .setPackageReplacePattern(getPackageName(), "PINE")
                                 .setTagStrategy(Pine.LogInfo::packageName)
                                 .setMessageStrategy(this::formatMessage)
                                 .grow());
        }
    }

    private String formatMessage(Pine.MessageInfo info) {
        return String.format(Locale.getDefault(),
                             "%s#%s, %d ----> %s",
                             info.className(),
                             info.methodName(),
                             info.lineNumber(),
                             info.message());
    }
}
