package com.ar.echoafcavlapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.ar.echoafcavlapplication.Data.StatFileManager;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

public class TransactionsFileHandler extends Service implements Runnable {
    private static Logger log = LoggerFactory.getLogger(TransactionsFileHandler.class);
    NotificationCompat.Builder mBuilder;
    private Thread runningThread;
    private boolean stopConds = false;

    private static TransactionsFileHandler instance = null;

    Callbacks activity;
    MainActivity mainActivity;
    private final IBinder mBinder = new TransactionsFileHandler.LocalBinder();
    private int generalTimeout = 10000;
    private int sendClosedFileCounter = 0;
    private int closeCurrentFileCounter = 0;

    public static TransactionsFileHandler getInstance(){
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
        log.error("TransactionsFileHandler service stopped...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerClient(MainActivity mainActivity) {
        this.activity = (Callbacks) mainActivity;
        this.mainActivity = mainActivity;
    }

    public class LocalBinder extends Binder {
        public TransactionsFileHandler getServiceInstance() {
            return TransactionsFileHandler.this;
        }
    }

    @Override
    public void run() {
        while (!stopConds) {
            try {
                if (sendClosedFileCounter >= Constants.SEND_CLOSED_FILE_TIMEOUT) {
                    sendClosedFileCounter = 0;
                    Utility.uploadClosedFile(StateHandler.getInstance().getCcIPAddress());
                    Utility.deleteExtraArchiveFiles();
                    Utility.uploadClosedShiftLogFile(StateHandler.getInstance().getCcIPAddress());
                    Utility.deleteExtraArchiveShiftLogFiles();
                    Utility.uploadDrivesImagesFile(StateHandler.getInstance().getCcIPAddress());
                    Utility.uploadStuffActivityFiles(StateHandler.getInstance().getCcIPAddress());
                    Utility.uploadTempShiftStatusFiles(StateHandler.getInstance().getCcIPAddress());
                }

                if(Utility.getOfflineCloseFileNumber() <= Constants.NUMBER_OF_OFFLINE_FILE_LIMIT) {
                    if (closeCurrentFileCounter >= Constants.CLOSE_OPEN_FILE_TIMEOUT) {
                        if (StateHandler.getInstance().isShiftOpen()){
                            closeCurrentFileCounter = 0;
                            StatFileManager.getInstance().newFile(true);
                        }
                    }
                }else{
                    OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_MAX_LIMIT_OF_OFFLINE_FILES, 1);
                }

                if (!checkConfFile()) {
                    OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_NO_CONF_FILE, 1);
                }else{
                    OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NO_CONF_FILE);
                }
                sendClosedFileCounter += generalTimeout;
                closeCurrentFileCounter += generalTimeout;
                Thread.sleep(generalTimeout);
            } catch (Exception ex) {
                log.error("run():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
            }
        }
    }

    public interface Callbacks {
    }

    private boolean checkConfFile() {
        if (!new File(Constants.confFolder+"config.properties").exists())
            return false;
        return true;
    }

}




