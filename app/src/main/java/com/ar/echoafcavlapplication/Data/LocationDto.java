package com.ar.echoafcavlapplication.Data;

import android.location.Location;

import com.ar.echoafcavlapplication.Utils.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LocationDto {
    private static Logger log = LoggerFactory.getLogger(LocationDto.class);
    public static LocationData toLocationDataForRealm(Location location, Date date) {

        LocationData locationData = new LocationData();
        try {
            SimpleDateFormat newSDF = new SimpleDateFormat("yyyyMMddHHmmss");
            locationData.setBusCode(String.valueOf(StateHandler.getInstance().getBusId()));
            locationData.setClientDate(newSDF.format(date));
            locationData.setGroundSpeed(String.valueOf(location.getSpeed()));
            locationData.setLatitude(String.valueOf(location.getLatitude()));
            locationData.setLongitude(String.valueOf(location.getLongitude()));
            locationData.setLineId(String.valueOf(StateHandler.getInstance().getLineId()));
        } catch (Exception ex) {
            log.error("LocationDto --> toLocationDataForRealm():" + ex.getMessage());
            log.error(ex.toString());
        }
        return locationData;
    }

    public static Map<String, String> toParamForLocation(LocationData locationData) {
        Map<String, String> params = new HashMap<>();
        try {
            params.put("bc", String.valueOf(StateHandler.getInstance().getBusId()));
            params.put("c_dt", locationData.getClientDate());
            params.put("gskh", String.valueOf(Utility.getStandardSpeed(Float.parseFloat(locationData.getGroundSpeed()))));
            params.put("gs", locationData.getGroundSpeed());
            params.put("lat", locationData.getLatitude());
            params.put("lng", locationData.getLongitude());
            params.put("lc", locationData.getLineId());
        } catch (Exception ex) {
            log.error("LocationDto --> toParamForLocation():" + ex.getMessage());
            log.error(ex.toString());
        }
        return params;
    }
}
