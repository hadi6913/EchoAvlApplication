package com.ar.echoafcavlapplication.System;

import android.app.Application;
import android.content.Context;

import com.ar.echoafcavlapplication.Data.CustomMigration;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.Utils.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class MyApplication extends Application {
    public static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iranYekanBoldFaNum.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        Realm.init(instance);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Constants.DB_NAME)
                .schemaVersion(1)
                .migration(new CustomMigration())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        ReaderHandler.getInstance().stopSelf();
    }



    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
