package com.example.nish.keepit;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Nishant on 3/15/2017.
 */

public class KeepIt extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);

    }
}
