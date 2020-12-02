package com.ar.echoafcavlapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.ar.echoafcavlapplication.Data.LastShiftHandler;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.UiBeepType;
import com.ar.echoafcavlapplication.Enums.UiCommandTypeEnum;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Models.LastShiftData;
import com.ar.echoafcavlapplication.Models.LastShiftDataHelper;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutOfServiceHandler extends Service implements Runnable{
    private static Logger log = LoggerFactory.getLogger(OutOfServiceHandler.class);
    NotificationCompat.Builder mBuilder;
    private Thread runningThread;
    private  boolean stopConds = false;
    Callbacks activity;
    MainActivity mainActivity;
    private final IBinder mBinder = new OutOfServiceHandler.LocalBinder();

    public static Map<Integer, Integer> outOfServiceMap = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> preOutOfServiceMap = new HashMap<Integer, Integer>();
    private static Object lock = new Object();
    public static boolean isOutOfService = false;
    private boolean isFirstTime = true;

    private static OutOfServiceHandler instance = null;

    public static OutOfServiceHandler getInstance(){
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
        log.error( "OutOfServiceHandler service stopped...");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void registerClient(MainActivity mainActivity){
        this.activity = (OutOfServiceHandler.Callbacks)mainActivity;
        this.mainActivity = mainActivity;
    }

    public class LocalBinder extends Binder {
        public OutOfServiceHandler getServiceInstance() {
            return OutOfServiceHandler.this;
        }
    }

    @Override
    public void run() {
        while(!stopConds)
        {
            try{
                if(!outOfServiceMap.equals(preOutOfServiceMap)) {
                    if(outOfServiceMap.isEmpty()){
                        if (StateHandler.getInstance().isShiftOpen())
                            displayMsg(UiCommandTypeEnum.TAP_CARD, null, null);
                        else
                            displayMsg(UiCommandTypeEnum.CLOSE_SHIFT, null, null);
                    }else{
                        if(outOfServiceMap.containsKey(Constants.OUT_OF_SERVICE_WAIT_FOR_INITIAL) && outOfServiceMap.size() == 1){
                            /*if (isFirstTime) {
                                isFirstTime = false;
                                displayMsg(UiCommandTypeEnum.INITIALIZING, null, null);
                            }else{
                                activity.restartTheAppFromService();
                            }*/
                            displayMsg(UiCommandTypeEnum.INITIALIZING, null, null);
                        }else{
                            if(outOfServiceMap.containsKey(Constants.OUT_OF_SERVICE_WAIT_FOR_INITIAL)) {
                                removeErrorFromMap(Constants.OUT_OF_SERVICE_WAIT_FOR_INITIAL);
                            }
                            String errorList = "";
                            List<Integer> errorListObj = new ArrayList<Integer>();
                            for (Map.Entry<Integer, Integer> error : outOfServiceMap.entrySet()) {
                                if (error.getValue() > 0) {
                                    errorList = errorList + "   " + error.getKey();
                                    errorListObj.add(error.getKey());
                                }
                            }
                            displayMsg(UiCommandTypeEnum.OUT_OF_SERVICE, errorList, errorListObj);
                        }
                    }
                }

                preOutOfServiceMap.clear();
                preOutOfServiceMap.putAll(outOfServiceMap);

                if (activity != null){
                    if (!isOutOfService){
                        if (!StateHandler.getInstance().isShiftOpen() && activity.getCurrentUiPage().equals(UiCommandTypeEnum.INITIALIZING)){
                            displayMsg(UiCommandTypeEnum.CLOSE_SHIFT, null, null);
                        }
                    }
                }

                if (Calendar.getInstance().get(Calendar.YEAR) < 2020){
                    addErrorToMap(Constants.OUT_OF_SERVICE_WRONG_DATE_TIME, 1);
                }else{
                    removeErrorFromMap(Constants.OUT_OF_SERVICE_WRONG_DATE_TIME);
                }

                Thread.sleep(1000);
            }catch (Exception ex) {
                log.error( "run():" + ex.getMessage());
                log.error(ex.toString());
            }
        }
    }

    public interface Callbacks{
        void changeTemporaryMessageUi(UiCommandTypeEnum operation, Map<Integer, String> newMsg);
        void changeFixedMessageUi(UiCommandTypeEnum operation, Map<Integer, String> newMsg, UiBeepType beep);
        void closeShiftFromOutOfServiceHandler();
        UiCommandTypeEnum getCurrentUiPage();
        void restartTheAppFromService();
        void resumeShiftAfterOutOfService(LastShiftDataHelper lastShiftData);
    }

    public void changeFixedMessageUi(UiCommandTypeEnum operation, Map<Integer, String> newMsg, UiBeepType beep){
        try {
            while(activity == null)
                //if(activity == null)
                Thread.sleep(200);
            activity.changeFixedMessageUi(operation, newMsg, beep);
        }catch (Exception ex){
            log.error( "OutOfServiceHandler --> changeFixedMessageUi():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void closeShiftFromOutOfServiceHandler(){
        try {
            while(activity == null)
                Thread.sleep(200);
            activity.closeShiftFromOutOfServiceHandler();
        }catch (Exception ex){
            log.error( "OutOfServiceHandler --> closeShiftFromOutOfServiceHandler():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static void addErrorToMap(Integer errorCode, Integer value){
        try {
            boolean firstTime = false;
            synchronized (lock) {
                if (!outOfServiceMap.containsKey(errorCode)) {
                    outOfServiceMap.put(errorCode, value);
                    isOutOfService = true;
                    firstTime = true;
                }
            }
            if(firstTime){
                switch (errorCode){
                    //add log if there is any to send to server
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            log.error( "OutOfServiceHandler --> addErrorToMap():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static void removeErrorFromMap(Integer errorCode){
        try {
            boolean has = false;
            boolean con;
            boolean conGate;
            int count = 0;

            synchronized (lock) {
                if (outOfServiceMap.containsKey(errorCode)) {
                    outOfServiceMap.remove(errorCode);
                    count = outOfServiceMap.keySet().size();
                    has = true;
                }
            }
            if(has){
                if(count == 0) {
                    isOutOfService = false;
                }

                switch (errorCode){
                    //add log if there is any to send to server
                    default:
                        break;
                }
            }

        } catch (Exception ex) {
            log.error( "OutOfServiceHandler --> removeErrorFromMap():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public static boolean isThereThisErrorOnMap(int errorCode){
        try{
            synchronized (lock){
                if (outOfServiceMap.containsKey(errorCode))
                    return true;
            }
            return false;
        }catch (Exception ex){
            log.error( "OutOfServiceHandler --> isThereThisErrorOnMap():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public static List<Integer> getAllErrorCodes(){
        try{
            List<Integer> res = new ArrayList<>();
            synchronized (lock) {
                for (Integer key : outOfServiceMap.keySet()) {
                    res.add(key);
                }
            }
            return res;
        }catch (Exception ex){
            log.error( "OutOfServiceHandler --> isThereAnyEmergency():" + ex.getMessage());
            log.error(ex.toString());
            return null;
        }
    }


    public static boolean isThereAnyErrorOnErrorMap(){
        try {
            if (outOfServiceMap.size()>1)
                return true;
        } catch (Exception ex) {
            log.error( "OutOfServiceHandler --> isThereAnyReaderErrorOnErrorMap():" + ex.getMessage());
            log.error(ex.toString());
        }
        return false;
    }

    public static String getErrorCodeList() {
        String errorList = "";
        try {
            for (Map.Entry<Integer, Integer> error : outOfServiceMap.entrySet()) {
                if (error.getValue() > 0) {
                    errorList += Integer.toString(error.getKey());
                    errorList += ',';
                }
            }
            if (errorList.contains(",")) {
                errorList = errorList.substring(0, errorList.length() - 1);
            }
        } catch (Exception e) {
        }
        return errorList;
    }

    public static void stopValidation() {
        try {
            ReaderHandler.shouldRead = false;
        } catch (Exception ex) {
            log.error("OutOfServiceHandler -- > stopValidation():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static String getAllErrors(){
        try{
            if (isOutOfService){
                StringBuilder sb = new StringBuilder();
                synchronized (lock) {
                    for (Integer code : outOfServiceMap.keySet()) {
                        sb.append(code.toString() + ", ");
                    }
                }
                return sb.toString();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "--";
    }

    public static void startValidation(){
        try{
            ReaderHandler.shouldRead = true;
        }catch (Exception ex){
            log.error( "OutOfServiceHandler -- > startValidation():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public void displayMsg(UiCommandTypeEnum commandTypeEnum, String errorList, List<Integer> errorListObj){
        try{
            Map<Integer, String> msg = new HashMap<>();
            switch (commandTypeEnum){
                case TAP_CARD:
                    changeFixedMessageUi(UiCommandTypeEnum.TAP_CARD, msg, UiBeepType.NO_BEEP);
                    startValidation();
                    break;

                case OUT_OF_SERVICE:
                    msg.put(1, errorList);
                    changeFixedMessageUi(UiCommandTypeEnum.OUT_OF_SERVICE, msg, UiBeepType.ERROR);
                    closeShiftFromOutOfServiceHandler();
                    stopValidation();
                    break;

                case INITIALIZING:
                    changeFixedMessageUi(UiCommandTypeEnum.INITIALIZING, msg, UiBeepType.NO_BEEP);
                    stopValidation();
                    break;

                case CLOSE_SHIFT:
                    if (activity.getCurrentUiPage().equals(UiCommandTypeEnum.LINE_ADMIN_PAGE) ||
                            activity.getCurrentUiPage().equals(UiCommandTypeEnum.REGISTER_PAGE) ||
                            activity.getCurrentUiPage().equals(UiCommandTypeEnum.UPLOAD_DOWNLOAD_PAGE) ||
                            activity.getCurrentUiPage().equals(UiCommandTypeEnum.OPERATOR_PAGE) ||
                            activity.getCurrentUiPage().equals(UiCommandTypeEnum.HARDWARE_TEST) ||
                            activity.getCurrentUiPage().equals(UiCommandTypeEnum.MAINTENANCE) ||
                            activity.getCurrentUiPage().equals(UiCommandTypeEnum.SUMMARY_REPORT))
                        return;
                    LastShiftDataHelper lastShiftData = LastShiftHandler.getInstance().getMainShiftData();
                    if (!lastShiftData.getCardSerial().equals(-1L) && !lastShiftData.getCardSerial().equals(0L)){
                        activity.resumeShiftAfterOutOfService(lastShiftData);
                        return;
                    }
                    changeFixedMessageUi(UiCommandTypeEnum.CLOSE_SHIFT, msg, UiBeepType.ERROR);
                    stopValidation();
                    break;

            }
        }catch (Exception ex){
            log.error("OutOfServiceHandler(): --> displayMsg()" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static boolean isThereError(){
        return isOutOfService;
    }
}

