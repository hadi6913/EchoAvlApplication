package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Enums.ReaderFileTagType;
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

public class StatFileManager {
    private static Logger log = LoggerFactory.getLogger(StatFileManager.class);

    private static final String extension = ".BQM";
    private static final String tempExtension = ".TEMP";
    private static final String sequenceNumber = "FILE_SEQ_NUMBER";
    private static final String messageSequnce = "MSQNO";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    private Object lock = new Object();

    public int afsid = 0;
    public int messageSequenceNumber = 0;
    private BufferedOutputStream out;
    private static StatFileManager statManager = null;
    private int transactionCount = 0;
    private String currentOutFileName;

    public static StatFileManager getInstance() {
        if (statManager == null)
            statManager = new StatFileManager();
        return statManager;
    }

    private StatFileManager() {
        loadFileSequenceID();
        loadMessageSequenceNumber();
        loadValidationCount();
        initialize();
    }

    private void initialize()
    {
        try {
            File open = new File(Constants.openFolder);
            if (!(open.exists() && open.isDirectory()))
                open.mkdirs();

            open = new File(Constants.closedFolder);
            if (!(open.exists() && open.isDirectory()))
                open.mkdirs();

            open = new File(Constants.archiveFolder);
            if (!(open.exists() && open.isDirectory()))
                open.mkdirs();


            String[] filesList = Utility.listFile(Constants.openFolder, tempExtension);
            if (filesList != null && filesList.length > 0) {
                for (int i = 0; i < filesList.length; i++) {
                    log.error( "initialize():" + filesList[i]);
                    BufferedOutputStream b = new BufferedOutputStream(new FileOutputStream(Constants.openFolder+ "/" +filesList[i], true));
                    closeFile(b, DataFileUtility.getInstance().getValidationCount(), Constants.openFolder+ "/" +filesList[i]);
                }
            }

        }catch (Exception ex)
        {
            log.error( "initialize():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void closeFile(BufferedOutputStream out, int validationCount, String fileName)
    {
        try{
            if (out == null)
                return;
            ArrayList<Byte> buff = new ArrayList<Byte>();
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(new Date());
            //add 2 byte of TAG.
            buff.add((byte)'S');
            buff.add((byte)'C');

            //add 2 byte of LENGTH.
            buff.add((byte)0);
            buff.add((byte)15);

            //add 4 byte of DIFF SECOND from 2000 to NOW.
            buff.addAll(ByteUtils.toByteArray(getDiffSecond(null), 4));

            //add 1 byte of CLOSE MODE = Auto
            buff.add((byte) 0x01);

            //add 2 byte of TRANSACTION COUNT.
            buff.addAll(ByteUtils.toByteArray(validationCount, 2));

            List<Byte> fileCRC = calculateFileCRC(buff, fileName);
            if(fileCRC != null) {
                //add 8 byte of SIGNATURE.
                buff.addAll(fileCRC);
            }else{
                //add 8 byte of SIGNATURE.
                buff.addAll(ByteUtils.convertPrimitiveToByte(new byte[8]));
            }



            out.write(ByteUtils.convertByteToPrimitive(buff));
            out.flush();
            out.close();

            //zip files here
            //move to closed folder
            String s = fileName.substring(fileName.lastIndexOf("/") + 1);
            String closeZipFileName = Constants.closedFolder + s.substring(0, s.lastIndexOf("."))+ ".zip";
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

    private Boolean loadFileSequenceID()
    {
        try{
            afsid = DataFileUtility.getInstance().loadFileSeqId();
            if(afsid == -1)
                return false;
            return true;
        }
        catch (Exception ex) {
            log.error( "LoadFileSequenceID():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }


    private Boolean loadMessageSequenceNumber()
    {
        try {
            messageSequenceNumber = DataFileUtility.getInstance().loadMsgSeqId();
            if(messageSequenceNumber == -1)
                return false;
            return true;
        }
        catch (Exception ex) {
            log.error( "loadMessageSequenceNumber():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }



    private Boolean loadValidationCount()
    {
        try {
            transactionCount = DataFileUtility.getInstance().getValidationCount();
            if(transactionCount == -1)
                return false;
            return true;
        }
        catch (Exception ex) {
            log.error( "loadValidationCount():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public void openNewFile(boolean isAuto)
    {
        try {
            ArrayList<Byte> buff = new ArrayList<Byte>();
            ArrayList<Byte> tempBuff = new ArrayList<Byte>();
            Date now = new Date();
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(now);

            currentOutFileName = Constants.openFolder  + StateHandler.getInstance().getBusId()+"_"+ StateHandler.getInstance().getLineId() +"_" +
                    sdf.format(now) + tempExtension;
            out = new BufferedOutputStream(new FileOutputStream(currentOutFileName));
            //add 2 byte of TAG
            buff.add((byte)'S');
            buff.add((byte)'O');

            //add 4 byte of DIFF SECOND from 2000 to NOW.
            tempBuff.addAll(ByteUtils.toByteArray(getDiffSecond(null), 4));

            //add 1 byte of OPEN MODE
            if (isAuto)
                tempBuff.add((byte) 0x01);
            else
                tempBuff.add((byte) 0x02);

            //add 2 byte of LINE ID
            tempBuff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getLineId(), 2));

            //add 1 byte of EQUIPMENT TYPE
            tempBuff.add((byte) Constants.EQUIPMENT_TYPE);

            //add 4 byte of BUS ID
            tempBuff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getBusId(), 4));

           /* //add 1 byte of OPERATION MODE = Offline
            buff.add((byte) 2);*/

            //add 4 byte of AFSID
            tempBuff.addAll(ByteUtils.toByteArray(afsid, 4));

            //project ID
            tempBuff.add((byte) 1);

            //add 20 byte of STAFF ID
            if (ReaderHandler.getInstance().getStaffID() != 0L)
                tempBuff.addAll(ByteUtils.toByteArray(ReaderHandler.getInstance().getStaffID(), 20));
            else
                tempBuff.addAll(ByteUtils.convertPrimitiveToByte(new byte[20]));

            //add 4 byte sam ID
            if (ReaderHandler.getInstance().getSamID() != null) {
                List<Byte> samIDToSend = new ArrayList<>();
                samIDToSend.add((byte)0x00);
                samIDToSend.addAll(ReaderHandler.getInstance().getSamID());
                tempBuff.addAll(ByteUtils.convertPrimitiveToByte(samIDToSend));
            }else
                tempBuff.addAll(ByteUtils.convertPrimitiveToByte(new byte[4]));

            //add 4 byte shift id
            tempBuff.addAll(ByteUtils.toByteArray(StateHandler.getInstance().getShiftId(), 4));

            //add 2 byte of LENGTH
            buff.add((byte)0);
            buff.add((byte)tempBuff.size());
            buff.addAll(tempBuff);

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

    private boolean incrementFileSequenceID()
    {
        try {
            afsid = DataFileUtility.getInstance().incFileSeqId();
            if(afsid > 65533) {
                afsid = 1;
                DataFileUtility.getInstance().setFileSeqId(1);
            }
            return true;
        }
        catch (Exception ex) {
            log.error( "incrementFileSequenceID():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public boolean incrementMessageSequenceNumber()
    {
        try {
            DataFileUtility.getInstance().incMsgSeqId();
            return true;
        }
        catch (Exception ex)
        {
            log.error( "incrementFileSequenceID():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }

    }

    public void newFile(boolean isAuto)
    {
        if (StateHandler.getInstance().getLineId() != 0 && StateHandler.getInstance().getBusId() != 0) {
            incrementFileSequenceID();
            setTransactionCount(0);
            /*if (!isAuto)
                ShiftStatusFileManager.getInstance().newFile(false);*/

            if (out == null) {
                openNewFile(isAuto);
            } else {
                closeFile(out, transactionCount, currentOutFileName);
                openNewFile(isAuto);
            }
        }
    }

    public void closeFile(boolean isAuto){
        closeFile(out, transactionCount, currentOutFileName);
        if (!isAuto) {
            ShiftStatusFileManager.getInstance().closeFile(false);
            DataFileUtility.getInstance().setValidationCount("0");
        }
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }


    public long getDiffSecond(Date date){
        try{
            Calendar now = Calendar.getInstance();
            if(date != null)
                now.setTime(date);
            Calendar old = Calendar.getInstance();
            old.set(Calendar.YEAR, 2000);
            old.set(Calendar.DAY_OF_YEAR, 1);
            old.set(Calendar.HOUR_OF_DAY, 0);
            old.set(Calendar.MINUTE, 0);
            old.set(Calendar.SECOND, 0);
            long diff = new Long((now.getTimeInMillis() - old.getTimeInMillis()));
            return diff/1000;
        }catch (Exception ex){
            log.error( "StatFileManager: --> getDiffSecond():" + ex.getMessage());
            log.error(ex.toString());
            return 0;
        }
    }


    public void writeMessage(ReaderFileTagType tagType, List<Byte> param)
    {
        try
        {
            List<Byte> toWrite = new ArrayList<Byte>();
            switch (tagType){
                case VA:
                    toWrite.addAll(param);
                    incrementMessageSequenceNumber();
                    DataFileUtility.getInstance().incValidationCount();
                    StateHandler.getInstance().setValidationCount(DataFileUtility.getInstance().getValidationCount());
                    break;

                default:
                    break;
            }
            synchronized (lock) {
                out.write(ByteUtils.convertByteToPrimitive(toWrite), 0, toWrite.size());
                out.flush();
            }
        }
        catch(Exception ex)
        {
            log.error( "StatFileManager: --> WriteMessage():" + ex.getMessage());
            log.error(ex.toString());
        }
    }
}
