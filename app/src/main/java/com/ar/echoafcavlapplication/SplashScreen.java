package com.ar.echoafcavlapplication;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.System.DeviceAdminReceiver;
import com.ar.echoafcavlapplication.System.LogBackConfigurations;
import com.ar.echoafcavlapplication.Utils.GPIOWrapper;
import com.ar.echoafcavlapplication.Utils.Utility;
import com.scsoft.libecho5.Gpio;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreen extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        startInitialOperations();
    }


    private void startInitialOperations(){
        new initial().execute("");
    }

    private class initial extends AsyncTask<String, Integer, String> {



        @Override
        protected String doInBackground(String... params) {
            initialSamAndReaderGPIO();
            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            disableLockScreen();
            new LogBackConfigurations();
            setUiComponent();
            StateHandler.getInstance().initial();
            enableKioskMode();
            Utility.setAutomaticTimeZoneOff(getApplicationContext());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }

    private void initialSamAndReaderGPIO(){
        try{
            //todo uncomment below.
            Gpio gpio = new Gpio();
            gpio.BarcodeScanner_Disable();
            gpio.Sam_Enable();
            gpio.CtlsReader_Enable();
            Thread.sleep(1000);

            GPIOWrapper gpioWrapper = new GPIOWrapper();
            gpioWrapper.Sam_Disable();
            Thread.sleep(1000);
            gpioWrapper.Sam_Enable();
            Thread.sleep(1000);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(context));
    }

    public void setUiComponent() {
        try {
            final View decorView = getWindow().getDecorView();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            // This work only for android 4.4+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(flags);
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void disableLockScreen() {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void enableKioskMode() {
        ComponentName deviceAdmin = new ComponentName(this, DeviceAdminReceiver.class);
        DevicePolicyManager mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        // First of all, to access anything you must be device owner
        if (mDpm.isDeviceOwnerApp(getPackageName())) {
            if (!mDpm.isAdminActive(deviceAdmin)) {
                Log.v("updater", "Not device admin. Asking device owner to become one.");
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "You need to be a device admin to enable kiosk mode.");
                startActivity(intent);
            } else {
                mDpm.setLockTaskPackages(deviceAdmin, new String[]{getPackageName()});
                startLockTask();
            }
        }
    }
}
