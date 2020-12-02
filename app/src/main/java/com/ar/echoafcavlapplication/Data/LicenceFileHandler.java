package com.ar.echoafcavlapplication.Data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import com.ar.echoafcavlapplication.Enums.LicenceFileTag;
import com.ar.echoafcavlapplication.Enums.RegisterStatus;
import com.ar.echoafcavlapplication.Models.LicenceModel;
import com.ar.echoafcavlapplication.Utils.ByteUtils;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LicenceFileHandler {
    private static Logger log = LoggerFactory.getLogger(LicenceFileHandler.class);
    private static LicenceFileHandler instance = null;
    private File directory = new File(Constants.BaseAddress+"secureData/");
    private File secFile = new File(Constants.BaseAddress+"secureData/"+"secIds.pok");
    private File secFileBackUp = new File(Constants.BaseAddress+"secureData/"+"secIds_backup.pok");
    private final String DC_LIC_TAG = "dc_lic";
    private final String DC_LIC_STATUS_TAG = "dc_lic_status";
    private final String BV_ONE_LIC_TAG = "bv1_lic";
    private final String BV_ONE_LIC_STATUS_TAG = "bv1_lic_status";
    private final String BV_TWO_LIC_TAG = "bv2_lic";
    private final String BV_TWO_LIC_STATUS_TAG = "bv2_lic_status";
    private final String BV_THREE_LIC_TAG = "bv3_lic";
    private final String BV_THREE_LIC_STATUS_TAG = "bv3_lic_status";
    private LicenceModel licenceModel = null;
    private String runTimeLicence = null;

    public static LicenceFileHandler getInstance(){
        if (instance == null)
            instance = new LicenceFileHandler();
        return instance;
    }

    public LicenceFileHandler() {
        initial();
        runTimeLicence = generateEncryptedUniqueId();
    }

    public void createLicenceFile(){
        try{
            if (!directory.isDirectory())
                directory.mkdirs();
            if (secFile.exists())
                secFile.delete();
            secFile.createNewFile();
            StringBuilder builder = new StringBuilder();
            builder.append(DC_LIC_TAG+":0\n");
            builder.append(DC_LIC_STATUS_TAG+":"+ RegisterStatus.NOT_REGISTERED.getValue()+"\n");

            FileUtils.writeStringToFile(secFile, builder.toString());
            licenceModel = new LicenceModel();
            licenceModel.setDcLic("");
            licenceModel.setDcStatus(RegisterStatus.NOT_REGISTERED);
            getBackUp();
        }catch (Exception ex){
            log.error("LicenceFileHandler --> initial():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void getBackUp(){
        try{
            if (secFileBackUp.exists())
                secFileBackUp.delete();
            secFileBackUp.createNewFile();
            FileUtils.copyFile(secFile, secFileBackUp);
        }catch (Exception ex){
            log.error("LicenceFileHandler --> getBackUp():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public boolean restoreBackupFile(){
        try {
            if (secFileBackUp.exists()){
                FileUtils.copyFile(secFileBackUp, secFile);
                return true;
            }
        }catch (Exception ex){
            log.error("LicenceFileHandler --> restoreBackupFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }

    public boolean checkFile(boolean isBackUp){
        try{
            if (!directory.isDirectory())
                return false;
            if (!secFile.exists())
                return false;
            List<String> file = FileUtils.readLines(secFile);
            if (file.isEmpty() && !isBackUp) {
                restoreBackupFile();
                return checkFile(true);
            }
            if (file.size() == 2)
                return true;
        }catch (Exception ex){
            log.error("LicenceFileHandler --> checkFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }

    public void initial() {
        try{
            if (!checkFile(false))
                createLicenceFile();
            else
                loadLicenceFile();
        }catch (Exception ex){
            log.error("LicenceFileHandler --> initial():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void loadLicenceFile() {
        try{
            licenceModel = new LicenceModel();
            if (!secFile.exists())
                return;
            List<String> file = FileUtils.readLines(secFile);
            if (file == null)
                return;
            if (file.size() != 2)
                return;
            for (String record : file){
                String[] parts = record.split(":");
                String tag = parts[0];
                String val = parts[1];
                switch (tag){
                    case DC_LIC_TAG:
                        this.licenceModel.setDcLic(val);
                        break;

                    case DC_LIC_STATUS_TAG:
                        this.licenceModel.setDcStatus(RegisterStatus.registerStatusValueOf(Integer.parseInt(val)));
                        break;
                }
            }
        }catch (Exception ex){
            log.error("LicenceFileHandler --> loadLicenceFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void changeFile(LicenceFileTag tag, String val){
        try{
            if (!secFile.exists())
                return;
            String tagStr = "";
            switch (tag){
                case DC_LIC:
                    tagStr = DC_LIC_TAG;
                    break;
                case DC_STATUS:
                    tagStr = DC_LIC_STATUS_TAG;
                    break;
            }
            List<String> file = FileUtils.readLines(secFile);
            if (file.isEmpty())
                return;
            boolean changed = false;
            for (String line : file){
                String[] part = line.split(":");
                if (part[0].trim().equals(tagStr)){
                    part[1] = val;
                    file.set(file.indexOf(line), part[0]+":"+part[1]);
                    changed = true;
                }
            }
            if (changed){
                Collection<String> s = new ArrayList<>(file);
                FileUtils.writeLines(secFile, s);
                getBackUp();
            }
        }catch (Exception ex){
            log.error("LicenceFileHandler --> changeFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public String encryptSHA256(String id){
        try{
            String toEnc = Constants.LIC_KEY.toString()+id;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(toEnc.getBytes("iso-8859-1"), 0, toEnc.length());
            byte[] sha1hash = md.digest();
            return ByteUtils.ByteArrToHex(sha1hash).toLowerCase().replace(" ","");
        }catch (Exception ex){
            log.error("LicenceFileHandler --> encryptSHA256():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return "";
    }

    public boolean areTheSame(String id, String idToCompare){
        try{
            if (id == null || idToCompare == null)
                return false;
            String newEn = encryptSHA256(id);
            if (idToCompare.equals(newEn))
                return true;
        }catch (Exception ex){
            log.error("LicenceFileHandler --> areTheSame():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }


    public String getIDFromOS(){
        try{
            TelephonyManager manager = (TelephonyManager) Utility.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(Utility.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            return manager.getDeviceId(0)+manager.getDeviceId(1);
            /*String macAddress = FileUtils.readFileToString(new File("/sys/class/net/eth0/address"));
            if (macAddress != null){
                if (macAddress.contains("\n")){
                    return macAddress.split("\n")[0];
                }else
                    return macAddress;
            }*/
        }catch (Exception ex){
            log.error("LicenceFileHandler --> generateUniqueId():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return "";
    }

    public String generateEncryptedUniqueId(){
        return encryptSHA256(getIDFromOS());
    }



    public String getDCLic(){
        if (licenceModel != null)
            return licenceModel.getDcLic();
        return "";
    }


    public RegisterStatus getDCStatus(){
        if (licenceModel != null)
            return licenceModel.getDcStatus();
        return null;
    }

    public void setDCLic(String lic){
        if (licenceModel != null){
            licenceModel.setDcLic(lic);
            changeFile(LicenceFileTag.DC_LIC, lic);
        }
    }

    public void setDCStatus(RegisterStatus status){
        if (licenceModel != null){
            licenceModel.setDcStatus(status);
            changeFile(LicenceFileTag.DC_STATUS, String.valueOf(status.getValue()));
        }
    }

    public String getRunTimeLicence() {
        try {
            if (runTimeLicence.equals(encryptSHA256(""))) {
                //we dont have proper IMEI
                if (Utility.getContext() != null) {
                    runTimeLicence = generateEncryptedUniqueId();
                }
            }
        }catch (Exception ex){
            log.error("LicenceFileHandler --> getRunTimeLicence():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return runTimeLicence;
    }
}

