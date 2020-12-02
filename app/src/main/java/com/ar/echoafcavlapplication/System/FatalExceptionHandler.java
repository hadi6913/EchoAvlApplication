package com.ar.echoafcavlapplication.System;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ar.echoafcavlapplication.Services.LocationHandler;
import com.ar.echoafcavlapplication.Services.LogHandler;
import com.ar.echoafcavlapplication.Services.OutOfServiceHandler;
import com.ar.echoafcavlapplication.Services.ParameterHandler;
import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.Services.SleepHandler;
import com.ar.echoafcavlapplication.Services.TransactionsFileHandler;
import com.ar.echoafcavlapplication.SplashScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FatalExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static Logger log = LoggerFactory.getLogger(FatalExceptionHandler.class);
    private Activity activity;

    public FatalExceptionHandler(Activity a){
        activity = a;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Intent intent = new Intent(activity, SplashScreen.class);
        intent.putExtra("crashed", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivities(MyApplication.getInstance().getBaseContext(), 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) MyApplication.getInstance().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pendingIntent);
        log.error("FatalExceptionHandler : " + "Exception occurred, go to restart / " + throwable.getMessage()+"/"+throwable.getLocalizedMessage());
        StringWriter errors = new StringWriter();
        throwable.printStackTrace(new PrintWriter(errors));
        log.error("FatalExceptionHandler", errors.toString());

        //Try to stop Reader handler
        if (ReaderHandler.getInstance() != null)
            ReaderHandler.getInstance().stopSelf();
        //Try to stop LocationHandler
        if (LocationHandler.getInstance() != null)
            LocationHandler.getInstance().stopSelf();
        //Try to stop ParameterHandler
        if (ParameterHandler.getInstance() != null)
            ParameterHandler.getInstance().stopSelf();
        //Try to stop SleepHandler
        if (SleepHandler.getInstance() != null)
            SleepHandler.getInstance().stopSelf();
        //Try to stop TransactionsFileHandler
        if (TransactionsFileHandler.getInstance() != null)
            TransactionsFileHandler.getInstance().stopSelf();
        //Try to stop OutOfServiceHandler
        if (OutOfServiceHandler.getInstance() != null)
            OutOfServiceHandler.getInstance().stopSelf();
        //Try to stop LogHandler-
        if (LogHandler.getInstance() != null)
            LogHandler.getInstance().stopSelf();

        activity.finish();
        System.exit(2);
    }
}

