package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.Utils.ByteUtils;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ShiftStatusTemporaryFileManager {
    private static Logger log = LoggerFactory.getLogger(ShiftStatusTemporaryFileManager.class);
    private static ShiftStatusTemporaryFileManager instance = null;
    private File dataDir = new File(Constants.tempShiftLogFolder);
    private File storeDataDir = new File(Constants.tempShiftLogFolderStorePath);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private String extension = ".SLF";

    public static ShiftStatusTemporaryFileManager getInstance(){
        if (instance == null)
            instance = new ShiftStatusTemporaryFileManager();
        return instance;
    }

    public ShiftStatusTemporaryFileManager() {
        initial();
    }

    private void initial(){
        try{
            if (!dataDir.isDirectory())
                dataDir.mkdirs();
            if (!storeDataDir.isDirectory())
                storeDataDir.mkdirs();
        }catch (Exception ex){
            log.error( "ShiftStatusTemporaryFileManager() -->  initial():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void writeNewFile(){
        try{
            Calendar c = Calendar.getInstance();
            File myFile = new File(Constants.tempShiftLogFolder  + StateHandler.getInstance().getBusId()+"_"+ StateHandler.getInstance().getLineId() +"_" + StateHandler.getInstance().getShiftId() +"_" +
                    sdf.format(c.getTime())+ "_T" + extension);
            if (myFile.exists())
                myFile.delete();
            myFile.createNewFile();

            ArrayList<Byte> buff = new ArrayList<Byte>();
            //add data
            //add 4 byte of Shift ID
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getShiftId().intValue(), 4));

            //add 4 byte of Bus ID
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getBusId(), 4));

            //add 4 byte of Staff ID
            buff.addAll(ByteUtils.toByteArray(ReaderHandler.getInstance().getStaffID(), 4));

            //add 2 byte of Line ID
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getLineId(), 2));

            //add 4 byte of Start shift date and time
            buff.addAll(ByteUtils.dateTimeValueTo4Byte(c.getTime()));

            FileUtils.writeByteArrayToFile(myFile, ByteUtils.convertByteToPrimitive(buff));
            FileUtils.copyFileToDirectory(myFile, storeDataDir);
            myFile.delete();
        }catch (Exception ex){
            log.error( "ShiftStatusTemporaryFileManager() -->  writeNewFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }
}
