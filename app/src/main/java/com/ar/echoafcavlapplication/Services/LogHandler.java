package com.ar.echoafcavlapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.ar.echoafcavlapplication.Communication.OnlineLocationSender;
import com.ar.echoafcavlapplication.Data.LicenceFileHandler;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.RegisterStatus;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Models.MessageProtocol;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.ParameterUtils;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

public class LogHandler extends Service implements Runnable {
    private static Logger log = LoggerFactory.getLogger(LogHandler.class);
    NotificationCompat.Builder mBuilder;
    private Thread runningThread;
    private boolean stopConds = false;
    Callbacks activity;
    MainActivity mainActivity;
    private final IBinder mBinder = new LocalBinder();

    private static LogHandler instance = null;

    public static LogHandler getInstance(){
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
        log.error("LocationHandler service stopped...");
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
        public LogHandler getServiceInstance() {
            return LogHandler.this;
        }
    }

    @Override
    public void run() {
        try {
            while (!stopConds) {
                if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.REGISTERED_CONFIRMED)) {
                    sendConsoleLog();
                }else if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.REGISTERED_NOT_CONFIRMED)){
                    sendCheckRegisterRequest();
                }
                Thread.sleep(60*1000);
            }
        } catch (Exception ex) {
            log.error("run():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }

    }

    public interface Callbacks {
        void changeLineId(String name);
        void changeBusId(final String id);
    }


    private void sendConsoleLog(){
        try{
            MessageProtocol cmd = new MessageProtocol(MessageProtocol.SendGeneralSystemStatusCommand, new Date(), Utility.generateParamForGeneralStatus());
            String res = OnlineLocationSender.getInstance().sendInquiry(cmd);
            if (res != null){
                MessageProtocol result = new MessageProtocol(res);
                switch (result.getType()){
                    case MessageProtocol.OKCommand:

                    case MessageProtocol.BadRequestCommand:
                        //check date
                        checkDateWithServer(result.getDateTime());
                        break;

                    case MessageProtocol.SetLineBusIdCommand:
                        //change line and bus id
                        if(result.getParams().containsKey(MessageProtocol.BusCodeAttribute)) {
                            StateHandler.getInstance().setBusId(Integer.parseInt(result.getParams().get(MessageProtocol.BusCodeAttribute)));
                            if (activity != null)
                                activity.changeBusId(String.valueOf(StateHandler.getInstance().getBusId()));
                        }
                        if(result.getParams().containsKey(MessageProtocol.lineCode)) {
                            StateHandler.getInstance().setLineId(Integer.parseInt(result.getParams().get(MessageProtocol.lineCode)));
                            if (activity != null)
                                activity.changeLineId(ParameterUtils.getInstance().getLineName());
                        }
                        checkDateWithServer(result.getDateTime());
                        break;

                    case MessageProtocol.WrongLicenceCommand:

                    case MessageProtocol.InvalidRegisterCommand:
                        LicenceFileHandler.getInstance().setDCStatus(RegisterStatus.NOT_REGISTERED);
                        checkDateWithServer(result.getDateTime());
                        break;
                }
            }
        }catch (Exception ex){
            log.error("LogHandler --> sendConsoleLog():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    private void checkDateWithServer(Date fromServer){
        try{
            if ((Math.abs(Calendar.getInstance().getTimeInMillis() - fromServer.getTime()) > 10000)){
                //we are not sync with server, set the time
                Utility.setSystemDateAndTimeDirectly(fromServer);
            }
        }catch (Exception ex){
            log.error("LogHandler --> checkDateWithServer():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    private void sendCheckRegisterRequest() {
        try {
            MessageProtocol cmd = new MessageProtocol(MessageProtocol.SendRegisterStatusCommand, new Date(), Utility.generateParamForRegisterCommand());
            String res = OnlineLocationSender.getInstance().sendInquiry(cmd);
            if (res != null) {
                MessageProtocol msg = new MessageProtocol(res);
                switch (msg.getType()) {
                    case MessageProtocol.SendRegisterStatusCommand:
                        if (msg.getParams().containsKey(MessageProtocol.dcRegisterValue)) {
                            RegisterStatus registerStatus = RegisterStatus.registerStatusValueOf(Integer.valueOf(msg.getParams().get(MessageProtocol.dcRegisterValue)));
                            if (registerStatus.equals(RegisterStatus.REGISTERED_CONFIRMED)){
                               OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NOT_CONFIRMED);
                                OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NOT_REGISTERED);
                                if (!registerStatus.equals(LicenceFileHandler.getInstance().getDCStatus())) {
                                    LicenceFileHandler.getInstance().setDCStatus(registerStatus);
                                    if (LicenceFileHandler.getInstance().getDCLic() != null) {
                                        if (!LicenceFileHandler.getInstance().getDCLic().equals(LicenceFileHandler.getInstance().getRunTimeLicence())) {
                                            LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                                        }
                                    } else {
                                        LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                                    }
                                }
                            }
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            log.error("LogHandler --> sendCheckRegisterRequest():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

}



