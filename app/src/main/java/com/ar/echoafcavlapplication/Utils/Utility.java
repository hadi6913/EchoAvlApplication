package com.ar.echoafcavlapplication.Utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Window;

import androidx.core.app.ActivityCompat;

import com.ar.echoafcavlapplication.Communication.FTPSClientUtility;
import com.ar.echoafcavlapplication.Data.LicenceFileHandler;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Models.MessageProtocol;
import com.ar.echoafcavlapplication.Services.LocationHandler;
import com.ar.echoafcavlapplication.Services.OutOfServiceHandler;
import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.SplashScreen;
import com.ar.echoafcavlapplication.System.MyApplication;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utility {
    private static Logger log = LoggerFactory.getLogger(Utility.class);
    private static Context context;
    private static Window window;

    public static Window getWindow() { return window; }
    public static void setWindow(Window window) { Utility.window = window; }

    public static Context getContext() {
        return context.getApplicationContext();
    }
    public static void setContext(Context context) {
        Utility.context = context;
    }

    public static String[] listFile(String folder, String ext) {
        GenericExtFilter filter = new GenericExtFilter(ext);
        File dir = new File(folder);
        // list out all the file name and filter by the extension
        return dir.list(filter);
    }


    public static void dirChecker(String path) {
        File f = new File(path);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    public static String getAppVersionName() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getContext().getPackageName(), 0);
            return info.versionName;
        } catch (Exception ex) {
            log.error("Utility --> getAppVersionName():" + ex.getMessage());
            log.error(ex.toString());
            return "";
        }
    }


    public static int getAppVersionCode() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getContext().getPackageName(), 0);
            return info.versionCode;
        } catch (Exception ex) {
            log.error("Utility --> getAppVersionCode():" + ex.getMessage());
            log.error(ex.toString());
            return 0;
        }
    }

    public static Map<String, String> generateParamForRegisterCommand() {
        Map<String, String> params = new HashMap<>();
        try {
            params.put(MessageProtocol.busCode, String.valueOf(StateHandler.getInstance().getBusId()));
            params.put(MessageProtocol.DC_LIC_TAG, LicenceFileHandler.getInstance().getRunTimeLicence());
            params.put(MessageProtocol.DC_ID_TAG, LicenceFileHandler.getInstance().getIDFromOS());
        } catch (Exception ex) {
            log.error("Utility --> generateParamForRegisterCommand():" + ex.getMessage());
            log.error(ex.toString());
        }
        return params;
    }

    public static void rebootOS(){
        try{
            PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
            pm.reboot(null);
        }catch (Exception ex){
            log.error("Utility --> rebootOS():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public static Integer getStandardSpeed(Float speed){
        try {
            return (int)(speed*3.6);
        }catch (Exception ex){
            log.error("Utility --> getStandardSpeed():" + ex.getMessage());
            log.error(ex.toString());
        }
        return 0;
    }

    public static String calculateCRCForTransactionFiles(File file) {
        try {
            if (file.exists()) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] fileByte = FileUtils.readFileToByteArray(file);
                md.update(fileByte, 0, fileByte.length);
                return ByteUtils.ByteArrToHex(md.digest()).replace(" ", "").toLowerCase();
                //return new String(md.digest()).replace("=","_").toLowerCase();
            }
        } catch (Exception ex) {
            log.error("Utility --> calculateCRCForTransactionFiles():" + ex.getMessage());
            log.error(ex.toString());
        }
        return null;
    }


    public static boolean uploadClosedFile(String server) {
        try {
            boolean res = false;
            File cf = new File(Constants.closedFolder);
            if (cf.exists() && cf.isDirectory()) {
                File[] files = cf.listFiles();
                if (files.length == 0)
                    return res;
                res = FTPSClientUtility.getInstance().uploadListOfFile(server,
                        Constants.FTP_PORT_NUMBER,
                        StateHandler.getInstance().getStatFtpUser(),
                        StateHandler.getInstance().getStatFtpPass(),
                        files, true);
            }
            return res;
        } catch (Exception ex) {
            log.error("Utility --> uploadClosedFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public static boolean uploadStuffActivityFiles(String server) {
        try {
            boolean res = false;
            File cf = new File(Constants.stuffActivityStorePath);
            if (cf.exists() && cf.isDirectory()) {
                File[] files = cf.listFiles();
                if (files.length == 0)
                    return res;
                res = FTPSClientUtility.getInstance().uploadListOfFile(server,
                        Constants.FTP_PORT_NUMBER,
                        StateHandler.getInstance().getStatFtpUser(),
                        StateHandler.getInstance().getStatFtpPass(),
                        files, false);
            }
            return res;
        } catch (Exception ex) {
            log.error("Utility --> uploadClosedFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }



    public static boolean uploadTempShiftStatusFiles(String server) {
        try {
            boolean res = false;
            File cf = new File(Constants.tempShiftLogFolderStorePath);
            if (cf.exists() && cf.isDirectory()) {
                File[] files = cf.listFiles();
                if (files.length == 0)
                    return res;
                res = FTPSClientUtility.getInstance().uploadListOfFile(server,
                        Constants.FTP_PORT_NUMBER,
                        StateHandler.getInstance().getStatFtpUser(),
                        StateHandler.getInstance().getStatFtpPass(),
                        files, false);
            }
            return res;
        } catch (Exception ex) {
            log.error("Utility --> uploadClosedFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }



    public static void uploadDrivesImagesFile(String server) {
        try {
            File ss = new File(Constants.startShiftImagesFolder);
            if (ss.exists() && ss.isDirectory()) {
                File[] files = ss.listFiles();
                if (files.length == 0)
                    return;
                FTPSClientUtility.getInstance().uploadListOfImageFile(server,
                        Constants.FTP_PORT_NUMBER,
                        StateHandler.getInstance().getStatFtpUser(),
                        StateHandler.getInstance().getStatFtpPass(),
                        files);
            }

            File es = new File(Constants.endShiftImagesFolder);
            if (es.exists() && es.isDirectory()) {
                File[] files = es.listFiles();
                if (files.length == 0)
                    return;
                FTPSClientUtility.getInstance().uploadListOfImageFile(StateHandler.getInstance().getCcIPAddress(),
                        Constants.FTP_PORT_NUMBER,
                        StateHandler.getInstance().getStatFtpUser(),
                        StateHandler.getInstance().getStatFtpPass(),
                        files);
            }
        } catch (Exception ex) {
            log.error("Utility --> uploadClosedFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public static boolean uploadClosedShiftLogFile(String server) {
        try {
            boolean res = false;
            File cf = new File(Constants.closedShiftLogFolder);
            if (cf.exists() && cf.isDirectory()) {
                File[] files = cf.listFiles();
                if (files.length == 0)
                    return res;
                res = FTPSClientUtility.getInstance().uploadListOfShiftLogFile(server,
                        Constants.FTP_PORT_NUMBER,
                        StateHandler.getInstance().getStatFtpUser(),
                        StateHandler.getInstance().getStatFtpPass(),
                        files, false);
            }
            return res;
        } catch (Exception ex) {
            log.error("Utility --> uploadClosedShiftLogFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public static void initialFolders(){
        try{
            File file;
            file = new File(Constants.BaseAddress);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.paramFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.currentParamFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.nextParamFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.newAppFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.archiveFolder);
            if(!file.exists())
                file.mkdirs();

            file = new File(Constants.openFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.closedFolder);
            if(!file.exists())
                file.mkdirs();


            file = new File(Constants.archiveShiftLogFolder);
            if(!file.exists())
                file.mkdirs();

            file = new File(Constants.openShiftLogFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.closedShiftLogFolder);
            if(!file.exists())
                file.mkdirs();



            file = new File(Constants.confFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.tempFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.tempUpdateFolder);
            if(!file.exists())
                file.mkdirs();
            file = new File(Constants.uploadFolder);
            if(!file.exists())
                file.mkdirs();
        }catch (Exception ex){
            log.error( "initialFolders():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static Map<String, String> generateParamForLocation(Location location, Date date) {
        Map<String, String> params = new HashMap<>();
        try {
            SimpleDateFormat newSDF = new SimpleDateFormat("yyyyMMddHHmmss");
            params.put("bc", String.valueOf(StateHandler.getInstance().getBusId()));
            params.put("c_dt", newSDF.format(date));
            params.put("gskh", String.valueOf(getStandardSpeed(location.getSpeed())));
            params.put("gs", String.valueOf(location.getSpeed()));
            params.put("lat", String.valueOf(location.getLatitude()));
            params.put("lng", String.valueOf(location.getLongitude()));
            params.put("lc", String.valueOf(StateHandler.getInstance().getLineId()));
            params.put("ac", String.valueOf(location.getAccuracy()));
            params.put("av", String.valueOf(Utility.getAppVersionCode()));


        } catch (Exception ex) {
            log.error("Utility --> generateParamForLocation():" + ex.getMessage());
            log.error(ex.toString());
        }
        return params;
    }

    public static boolean isMidnightForIDLE(){
        try{
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, 22);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);

            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 5);
            end.set(Calendar.MINUTE, 0);
            end.set(Calendar.SECOND, 0);
            end.add(Calendar.DATE, 1);

            Calendar now = Calendar.getInstance();

            if ((start.getTimeInMillis() <= now.getTimeInMillis()) && (now.getTimeInMillis() <= end.getTimeInMillis()))
                return true;
        }catch (Exception ex){
            log.error("Utility --> isMidnightForIDLE():" + ex.getMessage());
            log.error(ex.toString());
        }
        return false;
    }


    public static boolean isMidnightForReboot(){
        try{
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, 2);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);

            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 2);
            end.set(Calendar.MINUTE, 0);
            end.set(Calendar.SECOND, 30);

            Calendar now = Calendar.getInstance();

            if ((start.getTimeInMillis() <= now.getTimeInMillis()) && (now.getTimeInMillis() <= end.getTimeInMillis()))
                return true;
        }catch (Exception ex){
            log.error("Utility --> isMidnightForReboot():" + ex.getMessage());
            log.error(ex.toString());
        }
        return false;
    }


    public static boolean isMidnightForShiftClose(){
        try{
            Calendar start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, 2);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);

            Calendar end = Calendar.getInstance();
            end.set(Calendar.HOUR_OF_DAY, 2);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 0);

            Calendar now = Calendar.getInstance();

            if ((start.getTimeInMillis() <= now.getTimeInMillis()) && (now.getTimeInMillis() <= end.getTimeInMillis()))
                return true;
        }catch (Exception ex){
            log.error("Utility --> isMidnightForShiftClose():" + ex.getMessage());
            log.error(ex.toString());
        }
        return false;
    }


    public static Map<String, String> generateParamForGeneralStatus() {
        Map<String, String> params = new HashMap<>();
        try {
            SimpleDateFormat newSDF = new SimpleDateFormat("yyyyMMddHHmmss");
            //DC
            params.put(MessageProtocol.dcClientDate, newSDF.format(Calendar.getInstance().getTime()));
            params.put(MessageProtocol.dcSoftwareVersion, getAppVersionName());
            params.put(MessageProtocol.dcCurrentParamVersion, ParameterUtils.getInstance().getCurrentParamVersion());
            params.put(MessageProtocol.dcNextParamVersion, ParameterUtils.getInstance().getNextParamVersion());
            params.put(MessageProtocol.dcParamLoadDate, ParameterUtils.getInstance().getLoadDate());
            params.put(MessageProtocol.dcBlackListVersion, "");
            params.put(MessageProtocol.dcOperatorVersion, "");
            String dcClientEnabled = OutOfServiceHandler.isThereError() ? "0" : "1";
            params.put(MessageProtocol.dcClientEnabled, dcClientEnabled);
            params.put(MessageProtocol.dcValidationCount, ReaderHandler.getInstance().getTransactionCountForUI().toString());
            String gpsStatus = LocationHandler.getInstance().getGPSStatus() ? "1" : "0";
            params.put(MessageProtocol.gpsModuleStatus, gpsStatus);
            params.put(MessageProtocol.dcRemainingFileSequence, String.valueOf(getOfflineCloseFileNumber()));
            params.put(MessageProtocol.dcClientTripCode, String.valueOf(StateHandler.getInstance().getLineId()));
            params.put(MessageProtocol.dcEquipmentCode, String.valueOf(StateHandler.getInstance().getBusId()));
            params.put(MessageProtocol.dcErrorCode, OutOfServiceHandler.getErrorCodeList());
            String shiftStatus = StateHandler.getInstance().isShiftOpen() ? "1" : "0";
            params.put(MessageProtocol.shiftOpenStatus, shiftStatus);
            params.put(MessageProtocol.busCode, String.valueOf(StateHandler.getInstance().getBusId()));
            params.put(MessageProtocol.lineCode, String.valueOf(StateHandler.getInstance().getLineId()));
            params.put(MessageProtocol.availableInternalStorage, Utility.getAvailableInternalMemorySize());
            params.put(MessageProtocol.totalInternalStorage, Utility.getTotalInternalMemorySize());
            params.put(MessageProtocol.availablePhoneStorage, Utility.getAvailableExternalMemorySize());
            params.put(MessageProtocol.totalPhoneStorage, Utility.getTotalExternalMemorySize());
            params.put(MessageProtocol.DC_ID_TAG, LicenceFileHandler.getInstance().getIDFromOS());
            params.put(MessageProtocol.DC_LIC_TAG, LicenceFileHandler.getInstance().getDCLic());
            params.put(MessageProtocol.BV_ONE_LIC_TAG, "0");
            params.put(MessageProtocol.BV_TWO_LIC_TAG, "0");
            params.put(MessageProtocol.BV_THREE_LIC_TAG, "0");

        } catch (Exception ex) {
            log.error("Utility --> generateParamForLocation():" + ex.getMessage());
            log.error(ex.toString());
        }
        return params;
    }

    public static String imei = "";
    public static String getIMEIFromOS(){
        try{
            if (!imei.equals(""))
                return imei;
            TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            imei = manager.getDeviceId(0);
            return imei;
        }catch (Exception ex){
            log.error("Utility --> getIMEIFromOS():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return "";
    }


    public static int getOfflineCloseFileNumber(){
        try{
            File cf = new File(Constants.closedFolder);
            if(cf.exists() && cf.isDirectory()){
                File[] files = cf.listFiles();
                return files.length;
            }
            return 0;
        }catch (Exception ex){
            log.error( "Utility --> getOfflineCloseFileNumber():" + ex.getMessage());
            log.error(ex.toString());
            return 0;
        }
    }


    public static void deleteExtraArchiveFiles(){
        try{
            File archDir = new File(Constants.archiveFolder);
            if(archDir.exists()){
                if(archDir.isDirectory()){
                    Integer count = archDir.listFiles().length;
                    if(count > Constants.NUMBER_OF_ARCHIVE_FILE_LIMIT){
                        File [] files = archDir.listFiles();
                        Arrays.sort( files, new Comparator()
                        {
                            public int compare(Object o1, Object o2) {
                                if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                                    return -1;
                                } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                                    return +1;
                                } else {
                                    return 0;
                                }
                            }
                        });

                        for(int i = 0; i < Math.abs(Constants.NUMBER_OF_ARCHIVE_FILE_LIMIT - count); i++){
                            files[i].delete();
                        }
                    }
                }
            }
        }catch (Exception ex){
            log.error( "Utility --> deleteExtraArchiveFiles():" + ex.getMessage());
            log.error(ex.toString());
        }
    }



    public static void deleteExtraArchiveShiftLogFiles(){
        try{
            File archDir = new File(Constants.archiveShiftLogFolder);
            if(archDir.exists()){
                if(archDir.isDirectory()){
                    Integer count = archDir.listFiles().length;
                    if(count > Constants.NUMBER_OF_ARCHIVE_FILE_LIMIT){
                        File [] files = archDir.listFiles();
                        Arrays.sort( files, new Comparator()
                        {
                            public int compare(Object o1, Object o2) {
                                if (((File)o1).lastModified() > ((File)o2).lastModified()) {
                                    return -1;
                                } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
                                    return +1;
                                } else {
                                    return 0;
                                }
                            }
                        });

                        for(int i = 0; i < Math.abs(Constants.NUMBER_OF_ARCHIVE_FILE_LIMIT - count); i++){
                            files[i].delete();
                        }
                    }
                }
            }
        }catch (Exception ex){
            log.error( "Utility --> deleteExtraArchiveShiftLogFiles():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public static void setSystemDateAndTimeDirectly(Date d){
        try{
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            AlarmManager am = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            am.setTime(c.getTimeInMillis());
        }catch (Exception ex){
            log.error( "Utility --> setSystemDateAndTimeDirectly():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static void setAutomaticTimeZoneOff(Context context){
        try{
            int time_format = 0;
            try {
                time_format = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE);
            } catch (Settings.SettingNotFoundException e) {
                log.error( "Utils --> setAutomaticTimeZoneOff(): --> auto_time_zone_setting_not_found" + e.getMessage());
            }
            if(time_format != 0)
                Settings.Global.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0);
        }catch (Exception ex){
            log.error( "Utility --> setAutomaticTimeZoneOff():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public static void setSystemTimeZone(Context context, String timeZone){
        try{
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            am.setTimeZone(timeZone);
        }catch (Exception ex){
            log.error( "Utility --> setSystemTimeZone():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public static boolean externalMemoryAvailable() {
        try {
            return android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED);
        }catch (Exception ex){
            log.error( "Utility --> externalMemoryAvailable():" + ex.getMessage());
            log.error(ex.toString());
        }
        return false;
    }

    public static String getAvailableInternalMemorySize() {
        try {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
        }catch (Exception ex){
            log.error( "Utility --> getAvailableInternalMemorySize():" + ex.getMessage());
            log.error(ex.toString());
        }
        return "";
    }

    public static String getTotalInternalMemorySize() {
        try{
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return formatSize(totalBlocks * blockSize);
        }catch (Exception ex){
            log.error( "Utility --> getTotalInternalMemorySize():" + ex.getMessage());
            log.error(ex.toString());
        }
        return "";
    }

    public static String getAvailableExternalMemorySize() {
        try {
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSizeLong();
                long availableBlocks = stat.getAvailableBlocksLong();
                return formatSize(availableBlocks * blockSize);
            } else {
                return "حافظه خارجی یافت نشد";
            }
        }catch (Exception ex){
            log.error( "Utility --> getAvailableExternalMemorySize():" + ex.getMessage());
            log.error(ex.toString());
        }
        return "";
    }

    public static String getTotalExternalMemorySize() {
        try{
            if (externalMemoryAvailable()) {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSizeLong();
                long totalBlocks = stat.getBlockCountLong();
                return formatSize(totalBlocks * blockSize);
            } else {
                return "حافظه خارجی یافت نشد";
            }
        }catch (Exception ex){
            log.error( "Utility --> getTotalExternalMemorySize():" + ex.getMessage());
            log.error(ex.toString());
        }
        return "";
    }

    public static String formatSize(long size) {
        try {
            String suffix = null;

            if (size >= 1024) {
                suffix = "KB";
                size /= 1024;
                if (size >= 1024) {
                    suffix = "MB";
                    size /= 1024;
                }
            }

            StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

            int commaOffset = resultBuffer.length() - 3;
            while (commaOffset > 0) {
                resultBuffer.insert(commaOffset, ',');
                commaOffset -= 3;
            }

            if (suffix != null) resultBuffer.append(suffix);
            return resultBuffer.toString();
        }catch (Exception ex){
            log.error( "Utility --> formatSize():" + ex.getMessage());
            log.error(ex.toString());
        }
        return "";
    }

}
class GenericExtFilter implements FilenameFilter {

    private String ext;

    public GenericExtFilter(String ext) {
        this.ext = ext;
    }

    public boolean accept(File dir, String name) {
        return (name.endsWith(ext));
    }
}
