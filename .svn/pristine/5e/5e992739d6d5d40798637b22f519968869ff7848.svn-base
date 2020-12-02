package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Models.LastShiftData;
import com.ar.echoafcavlapplication.Models.LastShiftDataHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LastShiftHandler {
    private static Logger log = LoggerFactory.getLogger(LastShiftHandler.class);
    private static LastShiftHandler instance = null;
    private LastShiftDataHelper mainShiftData = null;

    public static LastShiftHandler getInstance(){
        if (instance == null)
            instance = new LastShiftHandler();
        return instance;
    }

    public LastShiftHandler() {
        initial();
    }


    public void initial(){
        try{
            boolean isEmpty = DataBaseHandler.getInstance().shiftIsEmpty();
            if (isEmpty){
                DataBaseHandler.getInstance().setShiftData(-1L, 0, new Date());
                mainShiftData = new LastShiftDataHelper(-1L,
                        0,
                        new Date());
            }else{
                LastShiftData shiftData = readShiftData();
                mainShiftData = new LastShiftDataHelper(shiftData.getCardSerial(),
                        shiftData.getValidationCount(),
                        shiftData.getStartShiftDate());
            }
        }catch (Exception ex){
            log.error( "LastShiftHandler > initial():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void writeShiftData(Long serial, Integer count, Date date){
        try{
            boolean isEmpty = DataBaseHandler.getInstance().shiftIsEmpty();
            if (isEmpty){
                DataBaseHandler.getInstance().setShiftData(-1L, 0, new Date());
                mainShiftData = new LastShiftDataHelper(-1L,
                        0,
                        new Date());
            }else{
                LastShiftData shiftData = DataBaseHandler.getInstance().getShiftData();
                if (date != null) {
                    DataBaseHandler.getInstance().setShiftData(serial,
                            count,
                            date);
                    mainShiftData = new LastShiftDataHelper(serial,
                            count,
                            date);
                }else {
                    DataBaseHandler.getInstance().setShiftData(serial,
                            count,
                            shiftData.getStartShiftDate());
                    mainShiftData = new LastShiftDataHelper(serial,
                            count,
                            shiftData.getStartShiftDate());
                }

            }
        }catch (Exception ex){
            log.error( "LastShiftHandler > writeShiftData():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    public LastShiftData readShiftData(){
        LastShiftData shiftData = null;
        try{
            boolean isEmpty = DataBaseHandler.getInstance().shiftIsEmpty();
            if (!isEmpty) {
                shiftData = DataBaseHandler.getInstance().getShiftData();
            }
        }catch (Exception ex){
            log.error( "LastShiftHandler > readShiftData():" + ex.getMessage());
            log.error(ex.toString());
        }
        return shiftData;
    }

    public LastShiftDataHelper getMainShiftData() {
        return mainShiftData;
    }
}
