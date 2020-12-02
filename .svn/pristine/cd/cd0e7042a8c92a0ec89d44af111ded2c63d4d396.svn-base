package com.ar.echoafcavlapplication.Services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.ar.echoafcavlapplication.Communication.OnlineLocationSender;
import com.ar.echoafcavlapplication.Data.DataBaseHandler;
import com.ar.echoafcavlapplication.Data.LicenceFileHandler;
import com.ar.echoafcavlapplication.Data.LocationData;
import com.ar.echoafcavlapplication.Data.LocationDto;
import com.ar.echoafcavlapplication.Data.RealmDataWrapper;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.RegisterStatus;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.Models.MessageProtocol;
import com.ar.echoafcavlapplication.System.MyApplication;
import com.ar.echoafcavlapplication.Utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LocationHandler extends Service implements Runnable {
    private static Logger log = LoggerFactory.getLogger(LocationHandler.class);
    NotificationCompat.Builder mBuilder;
    private Thread runningThread;
    private boolean stopConds = false;
    Callbacks activity;
    MainActivity mainActivity;
    private final IBinder mBinder = new LocalBinder();
    private LocationListener locationListener = null;

    private LocationManager locationManager = null;
    private Date lastDataDate = null;
    private Location lastLocation = null;
    private Integer interval = 15000;
    private Long currentLat = 0L;
    private Long currentLon = 0L;
    private Integer currentStopCode = 0;
    private Integer zeroSpeedCounter = 0;
    private boolean isGPSFix = false;

    private Date lastPacketOfDataTime = new Date();

    private static LocationHandler instance = null;

    public static LocationHandler getInstance() {
        return instance;
    }


    @Override
    public void onCreate() {
        instance = this;
        init();
    }



    private boolean temp = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private void initListener() {
        try{
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    lastPacketOfDataTime = new Date();
                    if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.REGISTERED_CONFIRMED)) {
                        handleStopStartEvent(location, new Date());
                    }
                    if (activity == null)
                        return;
                    activity.makeGPSIconGetSignal();
                    setLastLocation(location);
                    lastDataDate = Calendar.getInstance().getTime();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                    //nothing
                }

                @Override
                public void onProviderEnabled(String s) {
                    if (activity != null)
                        activity.makeGPSIconEnable();
                }

                @Override
                public void onProviderDisabled(String s) {
                    if (activity != null) {
                        activity.makeGPSIconDisable();
                    }
                }
            };
        }catch (Exception ex){
            log.error("LocationHandler --> initListener():" + ex.getMessage());
            log.error(ex.toString());
        }
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
        if (locationManager != null){
            locationManager.removeUpdates(locationListener);
        }
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
        public LocationHandler getServiceInstance() {
            return LocationHandler.this;
        }
    }

    @Override
    public void run() {
        try {
            while (!stopConds) {
                if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.REGISTERED_CONFIRMED)) {
                    sendLastRecordToServer(lastLocation, lastDataDate);
                }
                if (Utility.isMidnightForIDLE() && !StateHandler.getInstance().isShiftOpen())
                    Thread.sleep(1*60*1000);
                else
                    Thread.sleep(15*1000);
            }
        } catch (Exception ex) {
            log.error("run():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }

    }

    public interface Callbacks {
        void makeGPSIconDisable();
        void makeGPSIconEnable();
        void makeGPSIconGetSignal();
        void turnGPSOn();
        void turnGPSOff();
    }



    public void init() {
        try {
            initListener();
            lastDataDate = Calendar.getInstance().getTime();
            locationManager = (LocationManager) getSystemService(MyApplication.getInstance().LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if (locationListener != null)
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        } catch (Exception ex) {
            log.error("LocationHandler --> init():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    private Integer counter = 0;
    private Object lock = new Object();
    SimpleDateFormat newSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public void sendLastRecordToServer(Location location, final Date date) {
        synchronized (lock) {
            try {
                if (location == null || date == null) {
                    return;
                }
                if (Utility.getStandardSpeed(location.getSpeed()).equals(0)) {
                    zeroSpeedCounter++;
                    if(zeroSpeedCounter>=10) {
                        zeroSpeedCounter = 0;
                    }else{
                        return;
                    }
                }

                boolean isOnline = OnlineLocationSender.getInstance().isOnline();
                DataBaseHandler db = DataBaseHandler.getInstance();
                MessageProtocol cmd = new MessageProtocol(MessageProtocol.SendStatusCommand, new Date(), Utility.generateParamForLocation(location, date));
                log.error("M-OHDATA isOnline: " + isOnline);
                if (isOnline) {
                    //try to send
                    if (!db.isEmpty()) {
                        //firstly, we should archive current record.
                        db.write(new RealmDataWrapper(location, date));

                        Long recordsCount = db.getRecordsCount();
                        if (recordsCount == -1L)
                            return;
                        log.error("RecordCount: " + recordsCount);
                        //secondly, try to send archived data
                        List<LocationData> records = db.ReadAll();
                        if (records == null)
                            return;
                        if (records.isEmpty())
                            return;
                        int index = records.size();
                        int i = 0;
                        List<LocationData> itemToRemove = new ArrayList<>();
                        for (LocationData data : records) {

                            String res = OnlineLocationSender.getInstance().sendInquiry(new MessageProtocol(MessageProtocol.SendStatusCommand, new Date(), LocationDto.toParamForLocation(data)));
                            if ((res != null && res.length() > 0 && res.startsWith("ok"))) {
                                //successfully send archive record, lets delete it
                                itemToRemove.add(data);
                                counter = counter - 1;
                            } else
                                break;

                        }

                        if (!itemToRemove.isEmpty()){
                            db.remove(itemToRemove);
                        }
                    } else {
                        //no remaining files send current record
                        String res = OnlineLocationSender.getInstance().sendInquiry(cmd);
                        if (!(res != null && res.length() > 0 && res.startsWith("ok"))) {
                            //didn't get OK from server, archive this one
                            db.write(new RealmDataWrapper(location, date));
                        } else {
                            log.error("M-OHDATA online file > " + newSDF.format(date));
                        }
                    }
                } else {
                    //add to file
                    counter = counter + 1;
                    log.error("M-OHDATA archive file index 2: " + counter + "  > " + newSDF.format(date));
                    db.write(new RealmDataWrapper(location, date));
                }
            } catch (Exception ex) {
                log.error("LocationHandler --> sendLastRecordToServer():" + ex.getMessage());
                log.error(ex.toString());
            } finally {
                if (location != null) {
                    lastLocation = null;
                    lastDataDate = null;
                }
            }
        }
    }


    public void handleStopStartEvent(Location newLocation, Date date) {
        try {
            if (lastLocation == null)
                return;
            if (Utility.getStandardSpeed(newLocation.getSpeed()) > 9 && Utility.getStandardSpeed(lastLocation.getSpeed()) <= 9){
                //start to move
                log.error("M-OHDATA  start to move");
                sendLastRecordToServer(newLocation, date);
            }else if(Utility.getStandardSpeed(newLocation.getSpeed()) <= 4 && Utility.getStandardSpeed(lastLocation.getSpeed()) > 4){
                //going to stop
                log.error("M-OHDATA  going to stop");
                sendLastRecordToServer(newLocation, date);
            }
        } catch (Exception ex) {
            log.error("LocationHandler --> handleStopStartEvent():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public Long getCurrentLat() {
        return currentLat;
    }

    public Long getCurrentLon() {
        return currentLon;
    }

    public void setLastLocation(Location lastLocation) {
        try {
            this.lastLocation = lastLocation;
            this.currentLat = (long) (lastLocation.getLatitude() * 1000000);
            this.currentLon = (long) (lastLocation.getLongitude() * 1000000);
        } catch (Exception ex) {
            log.error("LocationHandler --> setLastLocation():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public Integer getCurrentStopCode() {
        return currentStopCode;
    }

    public void setCurrentStopCode(Integer currentStopCode) {
        this.currentStopCode = currentStopCode;
    }

    public Date getLastPacketOfDataTime() {
        return lastPacketOfDataTime;
    }

    public void setLastPacketOfDataTime(Date lastPacketOfDataTime) {
        this.lastPacketOfDataTime = lastPacketOfDataTime;
    }

    public boolean getGPSStatus(){
        try{
            if (Math.abs(new Date().getTime() - lastPacketOfDataTime.getTime()) < 5*60*1000)
                return true;
        }catch (Exception ex){
            log.error("LocationHandler --> getGPSStatus():" + ex.getMessage());
            log.error(ex.toString());
        }
        return false;
    }
}

