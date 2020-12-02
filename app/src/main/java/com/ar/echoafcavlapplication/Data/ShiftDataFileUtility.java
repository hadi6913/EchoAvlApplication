package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Utils.Constants;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ShiftDataFileUtility {
    private static Logger log = LoggerFactory.getLogger(ShiftDataFileUtility.class);

    private final String ATTRS_TAG = "attrs";
    private final String SHIFT_ID_TAG = "shiftId";
    private final String MSG_SEQ_ID_TAG = "msgSeqId";
    private final String VALIDATION_COUNT_TAG = "validationCount";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm");
    private final String LAST_VALIDATION_DATE = "validationDate";
    private final String LAST_VALIDATION_YEAR = "validationYear";
    private final String LAST_VALIDATION_MONTH = "validationMonth";
    private final String LAST_VALIDATION_DAY = "validationDay";
    private final String LAST_VALIDATION_HOUR = "validationHour";
    private final String LAST_VALIDATION_MINUTE = "validationMinute";
    private final String VALUE_TAG = "value";
    private final String TICKETS_TAG = "tickets";
    private static ShiftDataFileUtility shiftDataFileUtility = null;
    private Object lock = new Object();
    File backupShiftDataFile = new File(Constants.shiftFileDataFolder + "shiftData_backup.xml");
    File shiftFile = new File(Constants.shiftFileDataFolder+"shiftData.xml");


    public ShiftDataFileUtility() {
        initial();
    }

    public static ShiftDataFileUtility getInstance(){
        if(shiftDataFileUtility == null)
            shiftDataFileUtility = new ShiftDataFileUtility();
        return shiftDataFileUtility;
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
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(shiftFile);
                Node node = doc.getElementsByTagName(LAST_VALIDATION_DATE).item(0);
                if (node == null) {
                    return false;
                }
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
            File tFile = new File(Constants.shiftFileDataFolder+"shiftData.xml");
            tFile.createNewFile();
            StringBuilder fileSB = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            fileSB.append("<"+ ATTRS_TAG +">");
            fileSB.append("<"+ SHIFT_ID_TAG +" "+ VALUE_TAG +"=\"0\" />");
            fileSB.append("<"+ LAST_VALIDATION_DATE +" "+ VALUE_TAG +"=\"none\" />");
            fileSB.append("</"+ ATTRS_TAG +">");
            FileUtils.write(tFile, fileSB);
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  createStatFile():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public Integer getShiftId(){
        try{
            synchronized (lock) {
                if (!shiftFile.exists())
                    return -1;

                Integer result = 0;
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(shiftFile);


                Node validationDate = doc.getElementsByTagName(LAST_VALIDATION_DATE).item(0);
                NamedNodeMap attrDate = validationDate.getAttributes();
                Node nodeDateAttr = attrDate.getNamedItem(VALUE_TAG);
                if(!nodeDateAttr.getTextContent().equals("none")) {
                    Calendar fileDate = Calendar.getInstance();
                    Calendar now = Calendar.getInstance();
                    fileDate.setTime(simpleDateFormat.parse(nodeDateAttr.getTextContent()));
                    if(now.getTimeInMillis() > fileDate.getTimeInMillis()){
                        fileDate.add(Calendar.DATE, 1);
                        if(now.getTimeInMillis() > fileDate.getTimeInMillis()){
                            setShiftId("0");
                            return 0;
                        }else{
                            fileDate.setTime(simpleDateFormat.parse(nodeDateAttr.getTextContent()));
                            if(now.get(Calendar.DAY_OF_MONTH) > fileDate.get(Calendar.DAY_OF_MONTH)){
                                setShiftId("0");
                                return 0;
                            }else{
                                Node fileSeqId = doc.getElementsByTagName(SHIFT_ID_TAG).item(0);
                                NamedNodeMap attr = fileSeqId.getAttributes();
                                Node nodeAttr = attr.getNamedItem(VALUE_TAG);
                                result = Integer.parseInt(nodeAttr.getTextContent());
                            }
                        }
                    }

                }else{
                    Node fileSeqId = doc.getElementsByTagName(SHIFT_ID_TAG).item(0);
                    NamedNodeMap attr = fileSeqId.getAttributes();
                    Node nodeAttr = attr.getNamedItem(VALUE_TAG);
                    result = Integer.parseInt(nodeAttr.getTextContent());
                }
                return result;
            }
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  getShiftId():" + ex.getMessage());
            log.error(ex.toString());
            return -1;
        }finally {
            if(!checkFileStructure())
                restoreBackupFile();
        }
    }

    public Integer setShiftId(String newValue){
        try{
            synchronized (lock) {
                if (!shiftFile.exists())
                    return -1;
                Integer result = 0;
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.parse(shiftFile);

                Node fileSeqId = doc.getElementsByTagName(SHIFT_ID_TAG).item(0);
                NamedNodeMap attr = fileSeqId.getAttributes();
                Node nodeAttr = attr.getNamedItem(VALUE_TAG);
                nodeAttr.setTextContent(newValue);

                Node validationDate = doc.getElementsByTagName(LAST_VALIDATION_DATE).item(0);
                NamedNodeMap attrDate = validationDate.getAttributes();
                Node nodeDateAttr = attrDate.getNamedItem(VALUE_TAG);
                if(newValue.equals("0"))
                    nodeDateAttr.setTextContent("none");
                else
                    nodeDateAttr.setTextContent(simpleDateFormat.format(Calendar.getInstance().getTime()));

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult r = new StreamResult(shiftFile);
                transformer.transform(source, r);
                StateHandler.getInstance().setValidationCount(Integer.valueOf(newValue));

                return result;
            }
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  setShiftId():" + ex.getMessage());
            log.error(ex.toString());
            return -1;
        }finally {
            if(!checkFileStructure())
                restoreBackupFile();
            else
                getBackupFile();
        }
    }

    public void incShiftId(){
        try{
            synchronized (lock) {
                if (!shiftFile.exists())
                    return;

                Integer count = getShiftId();
                setShiftId(String.valueOf(count+1));
            }
        }catch (Exception ex){
            log.error( "ShiftDataFileUtility() -->  incShiftId():" + ex.getMessage());
            log.error(ex.toString());
        }
    }
}
