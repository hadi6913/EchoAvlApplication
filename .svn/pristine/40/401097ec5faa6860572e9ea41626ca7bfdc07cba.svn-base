package com.ar.echoafcavlapplication.Services;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.ar.echoafcavlapplication.Communication.OnlineLocationSender;
import com.ar.echoafcavlapplication.Data.LastShiftHandler;
import com.ar.echoafcavlapplication.Data.LicenceFileHandler;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.RegisterStatus;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

public class SleepHandler extends Service implements Runnable {
    private static Logger log = LoggerFactory.getLogger(SleepHandler.class);

    private Thread runningThread;
    private boolean stopConds = false;

    private static SleepHandler instance = null;

    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    public static Integer validationCount = 0;

    public static SleepHandler getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        if (instance == null)
            instance = this;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runningThread = new Thread(this);
        runningThread.start();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        stopConds = true;
        log.error("SleepHandler service stopped...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerClient(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    public class LocalBinder extends Binder {
        public SleepHandler getServiceInstance() {
            return SleepHandler.this;
        }
    }

    @Override
    public void run() {
        int i=0;
        while (!stopConds) {
            try {
                if(i==0) {
                    checkNetworkAvailableOrNot();
                    keepScreenUp();
                    killCameraProcess();
                    if (Utility.isMidnightForReboot()) {
                        Utility.rebootOS();
                    }
                    if (Utility.isMidnightForShiftClose() && StateHandler.getInstance().isShiftOpen()) {
                        activity.closeShift(0L, true);
                    }
                    checkBusPermission();
                    //handleShiftTransactionBackUp();
                }
                handleShiftTransaction();
                i++;
                if(i>=5)
                    i=0;
                Thread.sleep(1000);
            } catch (Exception ex) {
                log.error("run():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
            }
        }
    }



    public interface Callbacks {
        void disableLock();
        void showToast(String str);
        void closeShift(Long cardSerial, boolean isSystem);
        void changeNetStatus(boolean isOnline);
        boolean isInitialTaskCheckedShiftStatus();
    }

    private void checkNetworkAvailableOrNot() {
        if (OnlineLocationSender.getInstance().isOnline()) {
            if (activity != null)
                activity.changeNetStatus(true);
        }else{
            if (activity != null)
                activity.changeNetStatus(false);
        }
    }

    private void checkBusPermission(){
        try{
            if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.NOT_REGISTERED)){
                OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_NOT_REGISTERED, 1);
                OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NOT_CONFIRMED);
            }else if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.REGISTERED_NOT_CONFIRMED)){
                OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_NOT_CONFIRMED, 1);
                OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NOT_REGISTERED);
            }else{
                //remove all our of service
                OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NOT_CONFIRMED);
                OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NOT_REGISTERED);
            }
        }catch (Exception ex){
            log.error("SleepHandler > checkBusPermission():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    public void keepScreenUp(){
        try{
            if (activity != null) {
                PowerManager.WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                        PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                screenLock.acquire();
                screenLock.release();
                activity.disableLock();
            }

        }catch (Exception ex){
            log.error("SleepHandler > keepScreenUp():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    public void killCameraProcess(){
        try
        {
            if (isCameraAppOnForeground()) {
                ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
                am.killBackgroundProcesses(Constants.CAMERA_PACKAGE_NAME);
                if (activity != null)
                    activity.showToast("بهینه سازی موقعیت مکانی...");
            }
        }
        catch (Exception ex)
        {
            log.error("SleepHandler > killProcess():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    private boolean shouldTakeBackUp = false;
    public void handleShiftTransaction(){
        try
        {
            if (!ReaderHandler.getInstance().getTransactionCountForUI().equals(LastShiftHandler.getInstance().getMainShiftData().getValidationCount())){
                if (activity != null){
                    if (activity.isInitialTaskCheckedShiftStatus()){
                        shouldTakeBackUp = true;
                        LastShiftHandler.getInstance().writeShiftData(LastShiftHandler.getInstance().getMainShiftData().getCardSerial(),
                                ReaderHandler.getInstance().getTransactionCountForUI(),
                                null);
                    }
                }
            }
        }
        catch (Exception ex)
        {
            log.error("SleepHandler > handleShiftTransaction():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    public boolean isCameraAppOnForeground() {
        try {
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
            if (appProcesses == null) {
                return false;
            }
            final String packageName = Constants.CAMERA_PACKAGE_NAME;
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(packageName)) {
                    return true;
                }
            }
        }catch (Exception ex){
            log.error("SleepHandler > isCameraAppOnForeground():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }
}



