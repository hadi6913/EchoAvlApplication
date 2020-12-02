package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Enums.StuffActivityType;
import com.ar.echoafcavlapplication.Utils.ByteUtils;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StuffActivityFileManager {
    private static Logger log = LoggerFactory.getLogger(StuffActivityFileManager.class);
    private static StuffActivityFileManager instance = null;
    private File dataDir = new File(Constants.stuffActivityDataFolder);
    private File storeDataDir = new File(Constants.stuffActivityStorePath);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private String extension = ".SAF";

    public static StuffActivityFileManager getInstance(){
        if (instance == null)
            instance = new StuffActivityFileManager();
        return instance;
    }

    public StuffActivityFileManager() {
        initial();
    }

    private void initial(){
        try{
            if (!dataDir.isDirectory())
                dataDir.mkdirs();
            if (!storeDataDir.isDirectory())
                storeDataDir.mkdirs();
        }catch (Exception ex){
            log.error( "StuffActivityFileManager() -->  initial():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void writeNewFile(Long cardSerial, boolean isLineAdmin, StuffActivityType activityType){
        try{
            if (StateHandler.getInstance().getBusId() == 0 || StateHandler.getInstance().getLineId() == 0)
                return;
            File myFile = new File(Constants.statFileDataFolder+StateHandler.getInstance().getBusId()+"_"+ StateHandler.getInstance().getLineId() +"_" +
                    sdf.format(Calendar.getInstance().getTime()) + extension);
            if (myFile.exists())
                myFile.delete();
            myFile.createNewFile();

            ArrayList<Byte> buff = new ArrayList<Byte>();
            //add data
            //8 byte card serial
            buff.addAll(ByteUtils.toByteArray(cardSerial, 8));
            //1 byte who is
            buff.add(isLineAdmin ? (byte) 0x01 : (byte) 0x02);
            //1 byte activity type
            buff.add((byte)activityType.getValue());

            FileUtils.writeByteArrayToFile(myFile, ByteUtils.convertByteToPrimitive(buff));
            FileUtils.copyFileToDirectory(myFile, storeDataDir);
            myFile.delete();
        }catch (Exception ex){
            log.error( "StuffActivityFileManager() -->  writeNewFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }
}
