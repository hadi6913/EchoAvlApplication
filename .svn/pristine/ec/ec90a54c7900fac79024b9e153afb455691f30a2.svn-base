package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Enums.RegisterStatus;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

public class StateHandler {
    private static Logger log = LoggerFactory.getLogger(StateHandler.class);
    private static final String EQP_ID_STR = "equipment_id";
    private static final String LINE_ID_STR = "line_id";
    private static final String BUS_ID_STR = "bus_id";
    private static final String SOFT_FTP_USER_STR = "soft_ftp_user";
    private static final String SOFT_FTP_PASS_STR = "soft_ftp_pass";
    private static final String STAT_FTP_USER_STR = "stat_ftp_user";
    private static final String STAT_FTP_PASS_STR = "stat_ftp_pass";
    private static final String CC_IP_STR = "CC_ip_address";

    private boolean configFileHasIssue = false;
    private static StateHandler stateHandler = null;
    private boolean shiftOpen = false;
    private int equipmentId=0;
    private int lineId = 0;
    private int busId=0;
    private String softFtpUser = "";
    private String softFtpPass = "";
    private String statFtpUser = "";
    private String statFtpPass = "";
    private String ccIPAddress = "";

    private Integer validationCount = 0;
    private Long shiftId = 0L;

    private Object lock = new Object();


    public File confFile = new File(Constants.confFolder + "config.properties");

    private StateHandler() {
        initial();
    }

    public void initial() {
        try {
            if (confFile.exists())
                initialAttrs();
        }catch (Exception ex){
            log.error("StateHandler", "initial():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    private void initialAttrs() {
        if (confFile.exists()) {
            Properties prop = new Properties();
            InputStream input = null;
            OutputStream out = null;
            try {
                input = new FileInputStream(Constants.confFolder + "config.properties");
                // load a properties file
                prop.load(input);
                if (prop.getProperty(SOFT_FTP_USER_STR) != null)
                    softFtpUser = prop.getProperty(SOFT_FTP_USER_STR);
                else {
                    softFtpUser = "ecappuser";
                    configFileHasIssue = true;
                }
                if (prop.getProperty(SOFT_FTP_PASS_STR) != null)
                    softFtpPass = prop.getProperty(SOFT_FTP_PASS_STR);
                else {
                    softFtpPass = "ecapp1";
                    configFileHasIssue = true;
                }
                if (prop.getProperty(STAT_FTP_USER_STR) != null)
                    statFtpUser = prop.getProperty(STAT_FTP_USER_STR);
                else {
                    statFtpUser = "ecstatuser";
                    configFileHasIssue = true;
                }
                if (prop.getProperty(STAT_FTP_PASS_STR) != null)
                    statFtpPass = prop.getProperty(STAT_FTP_PASS_STR);
                else {
                    statFtpPass = "ecstat1";
                    configFileHasIssue = true;
                }
                if (prop.getProperty(CC_IP_STR) != null)
                    ccIPAddress = prop.getProperty(CC_IP_STR).trim();
                else{
                    ccIPAddress = "afc.qom.ir";
                    configFileHasIssue = true;
                }


                if (prop.getProperty(LINE_ID_STR) != null)
                    lineId = Integer.valueOf(prop.getProperty(LINE_ID_STR));
                else {
                    lineId = 0;
                    configFileHasIssue = true;
                }

                if (prop.getProperty(EQP_ID_STR) != null)
                    equipmentId = Integer.valueOf(prop.getProperty(EQP_ID_STR));
                else {
                    equipmentId = 0;
                    configFileHasIssue = true;
                }

                if (prop.getProperty(BUS_ID_STR) != null)
                    busId = Integer.valueOf(prop.getProperty(BUS_ID_STR));
                else {
                    busId = 0;
                    configFileHasIssue = true;
                }

                if (configFileHasIssue){
                    configFileHasIssue = false;
                    if (confFile.exists())
                        confFile.delete();
                    createConfigFile();
                    initialAttrs();
                }

            } catch (Exception ex) {
                log.error("StateHandler initial():" + ex.getMessage());
                log.error(ex.toString());
            } finally {
                try {
                    if (input != null)
                        input.close();
                    if (out != null)
                        out.close();
                } catch (Exception e) {
                    //do nothing.
                }
            }
        } else {
            return;
        }
    }

    private boolean changeValue(String attr, String val) {
        synchronized (lock) {
            Properties prop = null;
            FileOutputStream out = null;
            try {
                //load a properties file
                prop = new Properties();
                prop.load(new FileReader(Constants.confFolder + "config.properties"));

                out = new FileOutputStream(Constants.confFolder + "config.properties");
                prop.setProperty(attr, val);
                prop.store(out, null);
                return true;
            } catch (Exception ex) {
                log.error("StateHandler", "changeValue():" + ex.getMessage());
                log.error(ex.toString());
                return false;
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception ex) {
                    //do nothing.
                }
            }
        }
    }


    public boolean changeAllValue(Map<String, String> params) {
        synchronized (lock) {
            Properties prop = null;
            FileOutputStream out = null;
            try {
                // load a properties file
                prop = new Properties();
                prop.load(new FileReader(Constants.confFolder + "config.properties"));

                out = new FileOutputStream(Constants.confFolder + "config.properties");
                for (Map.Entry<String, String> param : params.entrySet()) {
                    prop.setProperty(param.getKey(), param.getValue());
                }
                prop.store(out, null);
                return true;
            } catch (Exception ex) {
                log.error("StateHandler", "changeAllValue():" + ex.getMessage());
                log.error(ex.toString());
                return false;
            } finally {
                try {
                    if (out != null)
                        out.close();
                } catch (Exception ex) {
                    //do nothing.
                }
            }
        }
    }


    public void createConfigFile(){
        try{
            confFile.createNewFile();
            StringBuilder sb = new StringBuilder();
            sb.append(SOFT_FTP_USER_STR+"=ecappuser\n");
            sb.append(SOFT_FTP_PASS_STR+"=ecapp1\n");
            sb.append(STAT_FTP_USER_STR+"=ecstatuser\n");
            sb.append(STAT_FTP_PASS_STR+"=ecstat1\n");
            sb.append(CC_IP_STR+"=afc.qom.ir\n");
            sb.append(BUS_ID_STR+"=0\n");
            sb.append(LINE_ID_STR+"=0\n");
            sb.append(EQP_ID_STR+"=0\n");
            FileUtils.writeStringToFile(confFile, sb.toString());
            StateHandler.getInstance().initial();
        }catch (Exception ex){
            log.error("StateHandler", "createConfigFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static StateHandler getInstance() {
        if (stateHandler == null)
            stateHandler = new StateHandler();
        return stateHandler;
    }

    public boolean isShiftOpen() {
        return shiftOpen;
    }

    public void setShiftOpen(boolean shiftOpen) {
        this.shiftOpen = shiftOpen;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
        changeValue(EQP_ID_STR, String.valueOf(equipmentId));
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
        changeValue(LINE_ID_STR, String.valueOf(lineId));
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        if (busId != this.busId) {
            LicenceFileHandler.getInstance().setDCStatus(RegisterStatus.NOT_REGISTERED);
            LicenceFileHandler.getInstance().setDCLic("0");
        }
        this.busId = busId;
        changeValue(BUS_ID_STR, String.valueOf(busId));
    }

    public String getSoftFtpUser() {
        return softFtpUser;
    }

    public void setSoftFtpUser(String softFtpUser) {
        this.softFtpUser = softFtpUser;
        changeValue(SOFT_FTP_USER_STR, String.valueOf(softFtpUser));
    }

    public String getSoftFtpPass() {
        return softFtpPass;
    }

    public void setSoftFtpPass(String softFtpPass) {
        this.softFtpPass = softFtpPass;
        changeValue(SOFT_FTP_PASS_STR, String.valueOf(softFtpPass));
    }

    public String getStatFtpUser() {
        return statFtpUser;
    }

    public void setStatFtpUser(String statFtpUser) {
        this.statFtpUser = statFtpUser;
        changeValue(STAT_FTP_USER_STR, String.valueOf(statFtpUser));
    }

    public String getStatFtpPass() {
        return statFtpPass;
    }

    public void setStatFtpPass(String statFtpPass) {
        this.statFtpPass = statFtpPass;
        changeValue(STAT_FTP_PASS_STR, String.valueOf(statFtpPass));
    }

    public String getCcIPAddress() {
        return ccIPAddress;
    }

    public void setCcIPAddress(String ccIPAddress) {
        this.ccIPAddress = ccIPAddress;
        changeValue(CC_IP_STR, String.valueOf(ccIPAddress));
    }

    public Integer getValidationCount() {
        return validationCount;
    }

    public void setValidationCount(Integer validationCount) {
        this.validationCount = validationCount;
    }

    public Long getShiftId() {
        //this.shiftId = ShiftDataFileUtility.getInstance().getShiftId();
        this.shiftId = ShiftDataTextFileUtility.getInstance().getShiftId();
        return shiftId;
    }

    public void incShiftId() {
        //ShiftDataFileUtility.getInstance().incShiftId();
        ShiftDataTextFileUtility.getInstance().incShiftId();
        this.shiftId += 1;
    }
}
