package com.ar.echoafcavlapplication.System;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ar.echoafcavlapplication.SplashScreen;

public class StartApplicationAtBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent activityIntent = new Intent(context, SplashScreen.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}
