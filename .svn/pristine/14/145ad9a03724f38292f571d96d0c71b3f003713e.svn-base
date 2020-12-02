package com.ar.echoafcavlapplication.Data;

import android.content.Context;
import android.widget.Toast;

import com.ar.echoafcavlapplication.Models.LastShiftData;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class DataBaseHandler {
    private static Logger log = LoggerFactory.getLogger(LocationDto.class);
    private Object lock = new Object();
    private static DataBaseHandler instance = null;

    public static DataBaseHandler getInstance() {
        if (instance == null)
            instance = new DataBaseHandler();
        return instance;
    }

    public DataBaseHandler() {
    }

    public boolean write(final RealmDataWrapper dataWrapper) {
        final boolean[] status = {false};
        synchronized (lock) {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        LocationData data = LocationDto.toLocationDataForRealm(dataWrapper.getLocation(), dataWrapper.getDate());
                        data.setId(generateId());
                        realm.insertOrUpdate(data);
                        status[0] = true;
                    }
                });
            } catch (Exception ex) {
                log.error("DatabaseHandler --> write():" + ex.getMessage());
                log.error(ex.toString());
            }
        }
        return status[0];
    }

    public long generateId() {
        synchronized (lock) {
            long nextId = 0;
            try{
                Realm realm = Realm.getDefaultInstance();
                Number currentIdNum = realm.where(LocationData.class).max("id");

                if (currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
            } catch (Exception ex) {
                log.error("DatabaseHandler --> generateId():" + ex.getMessage());
                log.error(ex.toString());
            }
            return nextId;
        }
    }

    public LocationData readFirst() {
        LocationData data = null;
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                data = realm.where(LocationData.class).sort("id", Sort.ASCENDING).findFirst();
            } catch (Exception ex) {
                log.error("DatabaseHandler --> readFirst():" + ex.getMessage());
                log.error(ex.toString());
            }
            return data;
        }
    }

    public void remove(final List<LocationData> data) {
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (LocationData d : data){
                            d.deleteFromRealm();
                        }
                    }
                });
            } catch (Exception ex) {
                log.error("DatabaseHandler --> remove():" + ex.getMessage());
                log.error(ex.toString());
            }
        }
    }

    public boolean isEmpty() {
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                return realm.isEmpty();
            }catch (Exception ex){
                return true;
            }
        }
    }

    public List<LocationData> ReadAll() {
        synchronized (lock) {
            List<LocationData> data = null;
            List<LocationData> dataCopy = null;
            try{
                Realm realm = Realm.getDefaultInstance();
                data = realm.where(LocationData.class).sort("id", Sort.ASCENDING).findAll();
                return data;
            } catch (Exception ex) {
                log.error("DatabaseHandler --> ReadAll():" + ex.getMessage());
                log.error(ex.toString());
            }
            return null;
        }
    }


    public List<LocationData> ReadAllXRecords(Integer count) {
        List<LocationData> data = null;
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                data = realm.where(LocationData.class).sort("id", Sort.ASCENDING).limit(count).findAll();
            } catch (Exception ex) {
                log.error("DatabaseHandler --> ReadAll():" + ex.getMessage());
                log.error(ex.toString());
            }
            return data;
        }
    }

    public Long getRecordsCount(){
        Long count = -1L;
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                count = realm.where(LocationData.class).sort("id", Sort.ASCENDING).count();
            } catch (Exception ex) {
                log.error("DatabaseHandler --> getRecordsCount():" + ex.getMessage());
                log.error(ex.toString());
            }
            return count;
        }
    }


    //-----SHIFT DATA METHOD----------

    public boolean shiftIsEmpty(){
        Long count = -1L;
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                count = realm.where(LastShiftData.class).sort("id", Sort.ASCENDING).count();
            } catch (Exception ex) {
                log.error("DatabaseHandler --> getRecordsCount():" + ex.getMessage());
                log.error(ex.toString());
            }
            if (count.equals(1L)){
                return false;
            }else{
                return true;
            }
        }
    }


    public LastShiftData getShiftData() {
        LastShiftData data = null;
        synchronized (lock) {
            try{
                Realm realm = Realm.getDefaultInstance();
                data = realm.where(LastShiftData.class).sort("id", Sort.ASCENDING).findFirst();
            } catch (Exception ex) {
                log.error("DatabaseHandler --> getShiftData():" + ex.getMessage());
                log.error(ex.toString());
            }
            return data;
        }
    }


    public boolean setShiftData(final Long cardSerial, final Integer count, final Date date) {
        final boolean[] status = {false};
        synchronized (lock) {
            try {
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        LastShiftData shiftData = new LastShiftData();
                        shiftData.setCardSerial(cardSerial);
                        shiftData.setStartShiftDate(date);
                        shiftData.setValidationCount(count);
                        shiftData.setId(1);
                        realm.insertOrUpdate(shiftData);
                        status[0] = true;
                    }
                });
            } catch (Exception ex) {
                log.error("DatabaseHandler --> write():" + ex.getMessage());
                log.error(ex.toString());
            }
        }
        return status[0];
    }
}
