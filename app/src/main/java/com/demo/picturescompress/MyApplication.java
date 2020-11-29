package com.demo.picturescompress;

import android.app.Application;

import leakcanary.AppWatcher;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppWatcher.Config config = AppWatcher.getConfig().newBuilder()
                .watchFragmentViews(false)
                .build();
        AppWatcher.setConfig(config);
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        refWatcher = LeakCanary.install(this);

    }

//    public static RefWatcher getRefWatcher(Context context) {
//        MyApplication myApplication = (MyApplication) context.getApplicationContext();
//        return myApplication.refWatcher;
//    }
}
