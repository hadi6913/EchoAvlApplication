package com.ar.echoafcavlapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.ParameterUtils;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class ParameterHandler extends Service implements Runnable {
    private static Logger log = LoggerFactory.getLogger(ParameterHandler.class);

    private Thread runningThread;
    private boolean stopConds = false;
    public static boolean parameterLoadStatus = false;
    public static boolean operatorsLoadStatus = false;
    String currentSoftVersion = "";
    Callbacks activity;
    private final IBinder mBinder = new LocalBinder();
    public static Integer validationCount = 0;

    private static ParameterHandler instance = null;

    public static ParameterHandler getInstance(){
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
        log.error("ParameterHandler service stopped...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerClient(MainActivity mainActivity) {
        this.activity = mainActivity;
    }

    public class LocalBinder extends Binder {
        public ParameterHandler getServiceInstance() {
            return ParameterHandler.this;
        }
    }

    @Override
    public void run() {
        while (!stopConds) {
            try {
                if (ParameterUtils.getInstance().isLoadFirstTime()) {
                    boolean paramDlStatus = ParameterUtils.getInstance().checkForParameterUpdate();
                    if (paramDlStatus) {
                        ParameterUtils.getInstance().getNextParamVersionAndLoadDateFromVersionFile(new File(Constants.paramFolder + "version.sis"));
                    }
                    boolean loadDateStatus = ParameterUtils.getInstance().dailyCheckerForLocalParamUpdate();
                    if (parameterLoadStatus || loadDateStatus || paramDlStatus){
                        if (activity != null) {
                            activity.changeParamVersion(ParameterUtils.getInstance().getCurrentParamVersion());
                            activity.changeLineId(ParameterUtils.getInstance().getLineName());
                        }
                    }
                }
                ParameterUtils.getInstance().checkSoftUpdate();
                ParameterUtils.getInstance().checkForOperatorsUpdate();
                Thread.sleep(2*60*1000);
            } catch (Exception ex) {
                log.error("run():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
            }
        }
    }

    public interface Callbacks {
        public void changeParamVersion(String version);
        public void changeLineId(String name);
    }
}



