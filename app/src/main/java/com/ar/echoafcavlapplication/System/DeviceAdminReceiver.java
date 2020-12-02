package com.ar.echoafcavlapplication.System;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    private static final String TAG = "DeviceAdminReceiver";
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context,intent);
        Log.i(TAG, "Device Owner Enabled");
    }
}
