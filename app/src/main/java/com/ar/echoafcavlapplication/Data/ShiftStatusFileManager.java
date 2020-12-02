package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Enums.ReaderFileTagType;
import com.ar.echoafcavlapplication.Models.LastShiftData;
import com.ar.echoafcavlapplication.Models.LastShiftDataHelper;
import com.ar.echoafcavlapplication.Services.ReaderHandler;
import com.ar.echoafcavlapplication.Utils.ByteUtils;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.Utility;
import com.ar.echoafcavlapplication.Utils.ZipManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShiftStatusFileManager {
    private static Logger log = LoggerFactory.getLogger(ShiftStatusFileManager.class);

    private static final String extension = ".SLF";
    private static final String tempExtension = ".TEMP";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private Object lock = new Object();


    private BufferedOutputStream out;
    private static ShiftStatusFileManager statManager = null;
    private String currentOutFileName;

    public static ShiftStatusFileManager getInstance() {
        if (statManager == null)
            statManager = new ShiftStatusFileManager();
        return statManager;
    }

    private ShiftStatusFileManager() {
        initialize();
    }

    private void initialize()
    {
        try {
            File open = new File(Constants.openShiftLogFolder);
            if (!(open.exists() && open.isDirectory()))
                open.mkdirs();

            open = new File(Constants.closedShiftLogFolder);
            if (!(open.exists() && open.isDirectory()))
                open.mkdirs();

            open = new File(Constants.archiveShiftLogFolder);
            if (!(open.exists() && open.isDirectory()))
                open.mkdirs();


            boolean actNormal = true;
            LastShiftDataHelper shiftData = LastShiftHandler.getInstance().getMainShiftData();
            if (shiftData != null){
                if (!shiftData.getCardSerial().equals(-1L))
                    actNormal = false;
            }
            if (actNormal){
            //if (LastShiftHandler.getInstance().getShiftData().getCardSerial().equals(-1L) || LastShiftFileHandler.getInstance().getCardSerial().getCardSerial().equals(0L)){
                //no previous shift, act normal
                String[] filesList = Utility.listFile(Constants.openShiftLogFolder, tempExtension);
                if (filesList != null && filesList.length > 0) {
                    for (int i = 0; i < filesList.length; i++) {
                        log.error( "initialize():" + filesList[i]);
                        BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(Constants.openShiftLogFolder+ "/" +filesList[i], true));
                        closeFile(b, Constants.openShiftLogFolder+ "/" +filesList[i], true);
                    }
                }
            }else{
                //there is an open shift, lets resume it.
                String[] filesList = Utility.listFile(Constants.openShiftLogFolder, tempExtension);
                out = new BufferedOutputStream(new FileOutputStream(Constants.openShiftLogFolder+ "/" +filesList[0], true));
                currentOutFileName = Constants.openShiftLogFolder+ "/" +filesList[0];
            }

        }catch (Exception ex)
        {
            log.error( "initialize():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void closeFile(BufferedOutputStream out, String fileName, boolean isAuto)
    {
        try{
            if (out == null)
                return;
            ArrayList<Byte> buff = new ArrayList<Byte>();
            Calendar now = Calendar.getInstance();


            //add 4 byte of End shift date and time
            buff.addAll(ByteUtils.dateTimeValueTo4Byte(now.getTime()));

            if (isAuto)
                buff.add((byte)0x01);
            else
                buff.add((byte)0x02);

            //add 4 byte of validation count
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getValidationCount(), 4));

            out.write(ByteUtils.convertByteToPrimitive(buff));
            out.flush();
            out.close();

            //zip files here
            //move to closed folder
            String s = fileName.substring(fileName.lastIndexOf("/") + 1);
            String closeZipFileName = Constants.closedShiftLogFolder + s.substring(0, s.lastIndexOf("."))+ ".zip";
            File nFile = new File(closeZipFileName);
            if (!nFile.exists())
            {
                if(ZipManager.zip(fileName,  closeZipFileName, s.substring(0, s.lastIndexOf(".")) + extension))
                {
                    new File(fileName).delete();
                }
            }
        }catch (Exception ex)
        {
            log.error( "closeFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public List<Byte> calculateFileCRC(List<Byte> additional, String fileName){
        try{
            //File current = new File(currentOutFileName);
            File current = new File(fileName);
            if(!current.exists())
                return null;
            byte[] res = FileUtils.readFileToByteArray(current);
            List<Byte> temp = new ArrayList<>();
            temp.addAll(ByteUtils.convertPrimitiveToByte(res));
            temp.addAll(additional);
            return ByteUtils.calculateCRC32(temp);
        }catch (Exception ex){
            log.error( "calculateFileCRC():" + ex.getMessage());
            log.error(ex.toString());
            return null;
        }
    }


    public void openNewFile()
    {
        try {
            StateHandler.getInstance().incShiftId();
            ArrayList<Byte> buff = new ArrayList<Byte>();
            Calendar now = Calendar.getInstance();
            currentOutFileName = Constants.openShiftLogFolder  + StateHandler.getInstance().getBusId()+"_"+ StateHandler.getInstance().getLineId() +"_" + StateHandler.getInstance().getShiftId() +"_" +
                    sdf.format(now.getTime()) + tempExtension;
            out = new BufferedOutputStream(new FileOutputStream(currentOutFileName));
            //add 4 byte of Shift ID
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getShiftId().intValue(), 4));

            //add 4 byte of Bus ID
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getBusId(), 4));

            //add 4 byte of Staff ID
            buff.addAll(ByteUtils.toByteArray(ReaderHandler.getInstance().getStaffID(), 4));

            //add 2 byte of Line ID
            buff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getLineId(), 2));

            //add 4 byte of Start shift date and time
            buff.addAll(ByteUtils.dateTimeValueTo4Byte(now.getTime()));


            synchronized (lock) {
                out.write(ByteUtils.convertByteToPrimitive(buff));
                out.flush();
            }
        }catch (Exception ex)
        {
            log.error( "OpenNewFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }



    public void newFile()
    {
        if (StateHandler.getInstance().getLineId() != 0 && StateHandler.getInstance().getBusId() != 0) {
            if (out == null) {
                openNewFile();
            }
        }
    }

    public void closeFile(boolean isAuto){
        closeFile(out, currentOutFileName, isAuto);
        out = null;
    }
}
