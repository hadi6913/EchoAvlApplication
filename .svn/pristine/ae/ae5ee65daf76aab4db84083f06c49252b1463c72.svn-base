package com.ar.echoafcavlapplication.Data;

import android.content.Intent;

import com.ar.echoafcavlapplication.Utils.Constants;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ShiftDataTextFileUtility {
    private static Logger log = LoggerFactory.getLogger(ShiftDataTextFileUtility.class);
    private static ShiftDataTextFileUtility shiftDataTextFileUtility = null;
    private Object lock = new Object();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    File backupShiftDataFile = new File(Constants.shiftFileDataFolder + "shiftData_backup.txt");
    File shiftFile = new File(Constants.shiftFileDataFolder+"shiftData.txt");
    private Long shiftId = 0L;


    public ShiftDataTextFileUtility() {
        initial();
    }

    public static ShiftDataTextFileUtility getInstance(){
        if(shiftDataTextFileUtility == null)
            shiftDataTextFileUtility = new ShiftDataTextFileUtility();
        return shiftDataTextFileUtility;
    }


    public void initial(){
        try {
            File statDir = new File(Constants.shiftFileDataFolder);
            if (!shiftFile.exists()) {
                statDir.mkdirs();
                createStatFile();
            }else{
                if(!checkFileStructure()){
                    shiftFile.delete();
                    createStatFile();
                }
            }
            createBackupFile();
            shiftId = getShiftIdFromFile();
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  initial():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public void createBackupFile(){
        try{
            if(!backupShiftDataFile.exists())
                backupShiftDataFile.createNewFile();
            if(shiftFile.exists())
                FileUtils.copyFile(shiftFile, backupShiftDataFile);
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  createBackupFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public boolean checkFileStructure(){
        try {
            synchronized (lock) {
                List<String> ret = FileUtils.readLines(shiftFile);
                if (ret.isEmpty())
                    return false;
                if (ret.size() != 2)
                    return false;
                if (ret.get(1).length() != 8)
                    return false;
                return true;
            }
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  checkFileStructure():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }


    public void restoreBackupFile(){
        try{
            if(backupShiftDataFile.exists())
                FileUtils.copyFile(backupShiftDataFile, shiftFile);
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  restoreBackupFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void getBackupFile(){
        try{
            if(backupShiftDataFile.exists() && shiftFile.exists())
                FileUtils.copyFile(shiftFile, backupShiftDataFile);
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  getBackupFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public void createStatFile(){
        try{
            shiftFile.createNewFile();
            StringBuilder builder = new StringBuilder();
            builder.append("0"+"\n");
            builder.append(simpleDateFormat.format(Calendar.getInstance().getTime())+"\n");
            FileUtils.writeStringToFile(shiftFile, builder.toString());
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  createStatFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public Long getShiftIdFromFile() {
        Long ret = -1L;
        try {
            synchronized (lock) {
                if (!shiftFile.exists())
                    return ret;
                List<String> lines = FileUtils.readLines(shiftFile);
                if (lines.size() == 2){
                    Long sh = Long.parseLong(lines.get(0));
                    if (sh >= shiftIDLimit){
                        lines.set(0, "0");
                        lines.set(1, simpleDateFormat.format(new Date()));
                        FileUtils.writeLines(shiftFile, lines);
                        return 0L;
                    }else{
                        return Long.parseLong(lines.get(0));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("ShiftDataFileUtility() -->  getShiftId():" + ex.getMessage());
            log.error(ex.toString());
        } finally {
            if (!checkFileStructure())
                restoreBackupFile();
        }
        return ret;
    }


    /*public Long getShiftIdFromFile() {
        Long ret = -1L;
        try {
            synchronized (lock) {
                if (!shiftFile.exists())
                    return ret;
                List<String> lines = FileUtils.readLines(shiftFile);
                if (lines.size() == 2){
                    Date recDate = simpleDateFormat.parse(lines.get(1));
                    Calendar b = Calendar.getInstance();
                    b.setTime(recDate);
                    Calendar n = Calendar.getInstance();
                    if (n.get(Calendar.DAY_OF_MONTH) != b.get(Calendar.DAY_OF_MONTH)){
                        lines.set(0, "0");
                        lines.set(1, simpleDateFormat.format(n.getTime()));
                        FileUtils.writeLines(shiftFile, lines);
                        return 0L;
                    }else{
                        return Long.parseLong(lines.get(0));
                    }
                }
            }
        } catch (Exception ex) {
            log.error("ShiftDataFileUtility() -->  getShiftId():" + ex.getMessage());
            log.error(ex.toString());
        } finally {
            if (!checkFileStructure())
                restoreBackupFile();
        }
        return ret;
    }*/


    /*public void incShiftId() {
        try {
            synchronized (lock) {
                if (!shiftFile.exists())
                    return;
                List<String> lines = FileUtils.readLines(shiftFile);
                if (lines.size() == 2){
                    Date recDate = simpleDateFormat.parse(lines.get(1));
                    Calendar b = Calendar.getInstance();
                    b.setTime(recDate);
                    Calendar n = Calendar.getInstance();
                    if (n.get(Calendar.DAY_OF_MONTH) != b.get(Calendar.DAY_OF_MONTH)){
                        lines.set(0, "0");
                        shiftId = 0;
                    }else{
                        Integer s = (shiftId+1);
                        lines.set(0, s.toString());
                        shiftId = s;
                    }
                    lines.set(1, simpleDateFormat.format(n.getTime()));
                    FileUtils.writeLines(shiftFile, lines);
                }
            }
        } catch (Exception ex) {
            log.error("ShiftDataFileUtility() -->  incShiftId():" + ex.getMessage());
            log.error(ex.toString());
        } finally {
            if (!checkFileStructure())
                restoreBackupFile();
            else
                getBackupFile();
        }
    }*/

    private Long shiftIDLimit = (long)Integer.MAX_VALUE*2;
    public void incShiftId() {
        try {
            synchronized (lock) {
                log.error("moh_shift: "+"in incShiftId()");
                if (!shiftFile.exists())
                    return;
                List<String> lines = FileUtils.readLines(shiftFile);
                if (lines.size() == 2){
                    if (shiftId >= shiftIDLimit){
                        lines.set(0, "0");
                        shiftId = 0L;
                    }else{
                        Long s = (shiftId+1);
                        lines.set(0, s.toString());
                        shiftId = s;
                    }
                    lines.set(1, simpleDateFormat.format(new Date()));
                    FileUtils.writeLines(shiftFile, lines);
                }
            }
        } catch (Exception ex) {
            log.error("ShiftDataFileUtility() -->  incShiftId():" + ex.getMessage());
            log.error(ex.toString());
        } finally {
            if (!checkFileStructure())
                restoreBackupFile();
            else
                getBackupFile();
        }
    }



    public Long getShiftId() {
        return shiftId;
    }
}
