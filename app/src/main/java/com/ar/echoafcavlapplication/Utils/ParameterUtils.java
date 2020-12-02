package com.ar.echoafcavlapplication.Utils;

import android.content.Intent;
import android.graphics.Path;

import com.ar.echoafcavlapplication.Communication.FTPSClientUtility;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.LocalCalendarDateType;
import com.ar.echoafcavlapplication.Models.Bus;
import com.ar.echoafcavlapplication.Models.Fare;
import com.ar.echoafcavlapplication.Models.FareClass;
import com.ar.echoafcavlapplication.Models.Line;
import com.ar.echoafcavlapplication.Models.LocalCalendar;
import com.ar.echoafcavlapplication.Models.Operator;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Services.LocationHandler;
import com.ar.echoafcavlapplication.Services.OutOfServiceHandler;

import net.lingala.zip4j.core.ZipFile;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterUtils {
    private static Logger log = LoggerFactory.getLogger(ParameterUtils.class);
    private static ParameterUtils instance;

    private String currentSoftVersion = "";
    private String currentParamVersion = "";
    private String currentOperatorsVersion = "";
    private String currentOperatorsVersionTemp = "";
    private String currentOperatorsEVD = "";
    private String nextParamVersion = "";
    private String currentParamVersionTemp = "";
    private String nextParamVersionTemp = "";
    private String loadDate = "";
    private String loadDateTemp = "";
    private String nightTimeFrom = "";
    private String nightTimeTo = "";
    private Map<String, LocalCalendarDateType> localCalendarMap = new HashMap<>();
    private List<LocalCalendar> localCalendarList;
    private List<Line> lineList;
    private List<Bus> busList;
    private List<FareClass> fareClassList;
    private List<Fare> fareList;
    private List<Operator> operatorsList;
    private boolean isLoadFirstTime = false;
    private Long antiPassBack = null;
    private Integer brightness = 0;

    //tags
    public static String CALENDAR_TAG = "calendar";
    public static String CALENDARS_TAG = "calendars";
    public static String YEAR_TAG = "year";
    public static String MONTH_TAG = "month";
    public static String DAY_TAG = "day";
    public static String DATE_TAG = "date";
    public static String DATE_TYPE_TAG = "datetype";
    public static String VERSION_TAG = "version";


    public static String LINE_TAG = "line";
    public static String LINES_TAG = "lines";
    public static String FARE_ID_TAG = "fareId";
    public static String LAST_TAG = "last";
    public static String FIRST_TAG = "first";
    public static String LABEL_TAG = "label";
    public static String CODE_TAG = "code";

    public static String BUSES_TAG = "buses";
    public static String BUS_TAG = "bus";
    public static String IMEI_TAG = "dc";


    public static String FARE_CLASS_TAG = "fareclass";
    public static String FARE_CLASSES_TAG = "fareclasses";
    public static String FARE_TAG = "fare";
    public static String NIGHT_TIME_TO_TAG = "nighttimeto";
    public static String NIGHT_TIME_FROM_TAG = "nighttimefrom";
    public static String HOLIDAY_NIGHT_FARE_TAG = "holidaynightfare";
    public static String HOLIDAY_BASE_FARE_TAG = "holidaybasefare";
    public static String NIGHT_SJT_TAG = "nightsjt";
    public static String NIGHT_BASE_FARE_TAG = "nightbasefare";
    public static String SJT_TAG = "sjt";
    public static String BASE_FARE_TAG = "basefare";
    public static String PRODUCT_CODE_TAG = "productcode";


    public static String CONSTANT_CONFIGURATIONS_TAG = "constantconfig";
    public static String CONSTANT_CONFIGURATION_TAG = "config";
    public static String ANTI_PASS_BACK_TAG = "antiPassBack";
    public static String BRIGHTNESS_TAG = "dcBrightness";


    public static String PARAM_TAG = "param";
    public static String STAFFS_TAG = "staffs";
    public static String STAFF_TAG = "staff";
    public static String EVD_DATE_TAG = "evddate";
    public static String ALG_TAG = "alg";
    public static String DEVICE_TYPE_TAG = "devicetype";
    public static String PASSWORD_TAG = "password";
    public static String USERNAME_TAG = "username";
    public static String ID_TAG = "id";
    public static String NAME_TAG = "name";
    public static String CARD_SERIAL_TAG = "cardserial";
    public static String FAMILY_TAG = "family";
    public static String BUS_LIST_TAG = "busList";
    //----

    public ParameterUtils() {
    }

    public static ParameterUtils getInstance(){
        if (instance == null)
            instance = new ParameterUtils();
        return instance;
    }

    public boolean loadParameters(){
        try{
            boolean noParamFile = false;
            String calendarFile = Constants.currentParamFolder + "calendar.xml";
            String fareClassFile = Constants.currentParamFolder + "fareclass.xml";
            String lineFile = Constants.currentParamFolder + "lines.xml";
            String constantConFile = Constants.currentParamFolder + "constantconfiguration.xml";
            String busFile = Constants.currentParamFolder + "bus.xml";

            String calendarFileTemp = Constants.currentParamFolder + "calendarTemp.xml";
            String fareClassFileTemp = Constants.currentParamFolder + "fareclassTemp.xml";
            String lineFileTemp = Constants.currentParamFolder + "linesTemp.xml";
            String constantConFileTemp = Constants.currentParamFolder + "constant_configurationTemp.xml";
            String busFileTemp = Constants.currentParamFolder + "busTemp.xml";

            List<String> tempFileForCreate = new ArrayList<String>();
            tempFileForCreate.add(calendarFileTemp);
            tempFileForCreate.add(fareClassFileTemp);
            tempFileForCreate.add(lineFileTemp);
            tempFileForCreate.add(constantConFileTemp);
            tempFileForCreate.add(busFileTemp);

            for(String tempFileAddress : tempFileForCreate){
                File file = new File(tempFileAddress);
                if(file.exists())
                    file.delete();
                file.createNewFile();
            }

            Boolean calendarStatus = false;
            Boolean fareClassStatus = false;
            Boolean linesStatus = false;
            Boolean constantStatus = false;
            Boolean busStatus = false;

            if(new File(calendarFile).exists()){
                //load calendar xml here
                byte[] res = Base64.decodeBase64(EncryptionUtility.getInstance().decrypt(FileUtils.readFileToString(new File(calendarFile), "UTF-8")));
                if (res != null) {
                    FileUtils.writeByteArrayToFile(new File(calendarFileTemp), res);
                    calendarStatus = loadLocalCalendars(calendarFileTemp);
                }else{
                    calendarStatus = false;
                }

            }else {
                log.error( "ParameterUtils --> loadParameters(): there is no calendar.xml file");
                calendarStatus = false;
                noParamFile = true;
            }

            /*if(new File(busFile).exists()){
                //load bus xml here
                byte[] res = Base64.decodeBase64(EncryptionUtility.getInstance().decrypt(FileUtils.readFileToString(new File(busFile), "UTF-8")));
                if (res != null) {
                    FileUtils.writeByteArrayToFile(new File(busFileTemp), res);
                    busStatus = loadBuses(busFileTemp);
                }else{
                    busStatus = false;
                }

            }else {
                log.error( "ParameterUtils --> loadParameters(): there is no bus.xml file");
                busStatus = false;
                noParamFile = true;
            }*/

            if(new File(lineFile).exists()){
                //load calendar xml here
                byte[] res = Base64.decodeBase64(EncryptionUtility.getInstance().decrypt(FileUtils.readFileToString(new File(lineFile), "UTF-8")));
                if (res != null) {
                    FileUtils.writeByteArrayToFile(new File(lineFileTemp), res);
                    linesStatus = loadLines(lineFileTemp);
                }else{
                    linesStatus = false;
                }

            }else {
                log.error( "ParameterUtils --> loadParameters(): there is no lines.xml file");
                linesStatus = false;
                noParamFile = true;
            }


            if(new File(fareClassFile).exists()){
                //load calendar xml here
                byte[] res = Base64.decodeBase64(EncryptionUtility.getInstance().decrypt(FileUtils.readFileToString(new File(fareClassFile), "UTF-8")));
                if (res != null) {
                    FileUtils.writeByteArrayToFile(new File(fareClassFileTemp), res);
                    fareClassStatus = loadFareClasses(fareClassFileTemp);
                }else{
                    fareClassStatus = false;
                }

            }else {
                log.error( "ParameterUtils --> loadParameters(): there is no fareClassFile.xml file");
                fareClassStatus = false;
                noParamFile = true;
            }


            // TODO: 7/15/2020 uncomment below
            if(new File(constantConFile).exists()){
                //load constant xml here
                byte[] res = Base64.decodeBase64(EncryptionUtility.getInstance().decrypt(FileUtils.readFileToString(new File(constantConFile), "UTF-8")));
                if (res != null) {
                    FileUtils.writeByteArrayToFile(new File(constantConFileTemp), res);
                    constantStatus = loadConstantConfiguration(constantConFileTemp);
                }else{
                    constantStatus = false;
                }

            }else {
                log.error( "ParameterUtils --> loadParameters(): there is no constant.xml file");
                constantStatus = false;
                //noParamFile = true;
            }

            if (!constantStatus)
                antiPassBack = null;

            if (noParamFile){
                OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_NO_PARAMETER, 1);
                return false;
            }
            OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_NO_PARAMETER);

            // TODO: 7/15/2020 add constant boolean below
            if (calendarStatus && fareClassStatus && linesStatus){
                OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_PARAMETER_LOAD);
                return true;
            }
            OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_PARAMETER_LOAD, 1);
            return false;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadParameters():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_NO_PARAMETER, 1);
        }
        return false;
    }

    private Boolean loadBuses(String fileName) {
        InputStream fis = null;
        try{
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            File file = new File(fileName);
            fis = new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            boolean status = false;
            int eventType = parser.getEventType();
            List<Bus> busList = new ArrayList<>();

            Long code = 0L;
            Long imei = 0L;

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equalsIgnoreCase(BUSES_TAG)){
                            currentParamVersion = parser.getAttributeValue(null, VERSION_TAG);
                            if(nextParamVersion.equals(currentParamVersion))
                                nextParamVersion = "";
                        }

                        if(parser.getName().equalsIgnoreCase(BUS_TAG)){
                            if (parser.getAttributeValue(null, CODE_TAG) != null)
                                code = Long.parseLong(parser.getAttributeValue(null, CODE_TAG));
                            if (parser.getAttributeValue(null, IMEI_TAG) != null){
                                if (!parser.getAttributeValue(null, IMEI_TAG).toLowerCase().equals("null")){
                                    imei = Long.parseLong(parser.getAttributeValue(null, IMEI_TAG));
                                }
                            }
                            Bus bus = new Bus(imei, code);
                            busList.add(bus);
                        }
                        break;
                }
                eventType = parser.next();
            }
            if(!busList.isEmpty()) {
                setBusList(busList);
                status = true;
            }
            return status;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadBuses():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }


    public boolean loadOperators(){

        try{
            String operatorsFile = Constants.paramFolder + "operators.xml";
            String operatorsFileTemp = Constants.paramFolder + "operatorsTemp.xml";
            File file = new File(operatorsFileTemp);
            if(file.exists())
                file.delete();
            file.createNewFile();
            Boolean operatorsStatus = false;
            if(new File(operatorsFile).exists()){
                //load operators xml here
                byte[] res = Base64.decodeBase64(EncryptionUtility.getInstance().decrypt(FileUtils.readFileToString(new File(operatorsFile), "UTF-8")));
                if (res != null) {
                    FileUtils.writeByteArrayToFile(new File(operatorsFileTemp), res);
                    operatorsStatus = loadOperatorsFile(operatorsFileTemp);
                }else{
                    operatorsStatus = false;
                }
            }else {
                operatorsStatus = false;
            }
            return operatorsStatus;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadOperators():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }


    public Boolean loadOperatorsFile(String fileName){
        InputStream fis = null;
        try{
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            File file = new File(fileName);
            fis = new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            boolean status = false;
            int eventType = parser.getEventType();
            List<Operator> operatorsList = new ArrayList<Operator>();
            String name;
            String family;
            String username;
            String password;
            String code;
            Long cardSerial = 0L;
            List<Integer> busList = new ArrayList<>();;
            LocalCalendarDateType dateType;

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equalsIgnoreCase(PARAM_TAG)){
                            currentOperatorsVersion = parser.getAttributeValue(null, VERSION_TAG);
                            currentOperatorsEVD = parser.getAttributeValue(null, EVD_DATE_TAG);
                        }
                        //start tracing the xml node by node and open the list of objects.
                        if(parser.getName().equalsIgnoreCase(STAFF_TAG)){
                            code = parser.getAttributeValue(null, ID_TAG);
                            username = parser.getAttributeValue(null, USERNAME_TAG);
                            password = parser.getAttributeValue(null, PASSWORD_TAG);
                            name = parser.getAttributeValue(null, NAME_TAG);
                            family = parser.getAttributeValue(null, FAMILY_TAG);
                            if (parser.getAttributeValue(null, CARD_SERIAL_TAG) != null && !parser.getAttributeValue(null, CARD_SERIAL_TAG).equals("null"))
                                cardSerial = Long.parseLong(parser.getAttributeValue(null, CARD_SERIAL_TAG));
                            String busesStr = parser.getAttributeValue(null, BUS_LIST_TAG);
                            busList.clear();
                            if (busesStr != null) {
                                if (busesStr.contains(",")) {
                                    String[] temp = busesStr.split(",");
                                    for (String bus : temp) {
                                        busList.add(Integer.parseInt(bus));
                                    }
                                } else if (!busesStr.equals("")) {
                                    busList.add(Integer.parseInt(busesStr));
                                }
                            }

                            Operator operator = new Operator();
                            operator.setCode(code);
                            operator.setName(name);
                            operator.setPassword(password);
                            operator.setUsername(username);
                            operator.setCardSerial(cardSerial);
                            operator.setFamily(family);
                            /*if (cardSerial.equals(4101343812L))
                                busList.add(12589);*/
                            operator.setBusList(busList);
                            operatorsList.add(operator);
                        }
                        break;
                }
                eventType = parser.next();
            }
            if(!operatorsList.isEmpty()) {
                setOperatorsList(operatorsList);
                status = true;
            }

            return status;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadOperatorsFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }finally {
            try {
                if (fis != null)
                    fis.close();
            }catch (Exception ex){
            }
        }
    }


    private Boolean loadConstantConfiguration(String fileName) {
        InputStream fis = null;
        try{
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            File file = new File(fileName);
            fis = new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            boolean status = false;
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equalsIgnoreCase(CONSTANT_CONFIGURATIONS_TAG)){
                            currentParamVersion = parser.getAttributeValue(null, VERSION_TAG);
                            if(nextParamVersion.equals(currentParamVersion))
                                nextParamVersion = "";
                        }
                        if(parser.getName().equalsIgnoreCase(CONSTANT_CONFIGURATION_TAG)){
                            if (parser.getAttributeValue(null, ANTI_PASS_BACK_TAG) != null) {
                                antiPassBack = Long.parseLong(parser.getAttributeValue(null, ANTI_PASS_BACK_TAG));
                                status = true;
                            }
                            if (parser.getAttributeValue(null, BRIGHTNESS_TAG) != null) {
                                brightness = Integer.parseInt(parser.getAttributeValue(null, BRIGHTNESS_TAG));
                                status = true;
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
            return status;

        }catch (Exception ex){
            log.error("ParameterUtils --> loadConstantConfiguration():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }

    private boolean loadFareClasses(String fileName) {
        InputStream fis = null;
        try{
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            File file = new File(fileName);
            fis = new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            boolean status = false;
            int eventType = parser.getEventType();
            List<FareClass> fareClassList = new ArrayList<>();

            Integer code;
            Long holidayNightFare;
            Long holidayBaseFare;
            Long nightSJT;
            Long nightBaseFare;
            Long sjt;
            Long baseFare;
            Integer productCode;
            FareClass fareClass = new FareClass();
            Map<Integer, Fare> fareMap = new HashMap<>();
            List<Fare> mainFareList = new ArrayList<>();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equalsIgnoreCase(FARE_CLASSES_TAG)){
                            currentParamVersion = parser.getAttributeValue(null, VERSION_TAG);
                            if(nextParamVersion.equals(currentParamVersion))
                                nextParamVersion = "";
                            nightTimeFrom = parser.getAttributeValue(null, NIGHT_TIME_FROM_TAG);
                            nightTimeTo = parser.getAttributeValue(null, NIGHT_TIME_TO_TAG);
                        }

                        if(parser.getName().equalsIgnoreCase(FARE_CLASS_TAG)){
                            code = Integer.parseInt(parser.getAttributeValue(null, CODE_TAG));
                            fareClass = new FareClass();
                            fareMap = new HashMap<>();
                            fareClass.setCode(code);
                        }

                        if(parser.getName().equalsIgnoreCase(FARE_TAG)){
                            holidayNightFare = Long.parseLong(parser.getAttributeValue(null, HOLIDAY_NIGHT_FARE_TAG));
                            holidayBaseFare = Long.parseLong(parser.getAttributeValue(null, HOLIDAY_BASE_FARE_TAG));
                            nightSJT = Long.parseLong(parser.getAttributeValue(null, NIGHT_SJT_TAG));
                            nightBaseFare = Long.parseLong(parser.getAttributeValue(null, NIGHT_BASE_FARE_TAG));
                            sjt = Long.parseLong(parser.getAttributeValue(null, SJT_TAG));
                            baseFare = Long.parseLong(parser.getAttributeValue(null, BASE_FARE_TAG));
                            productCode = Integer.parseInt(parser.getAttributeValue(null, PRODUCT_CODE_TAG));
                            Fare fare = new Fare();
                            fare.setBaseFare(baseFare);
                            fare.setHolidayBaseFare(holidayBaseFare);
                            fare.setHolidayNightFare(holidayNightFare);
                            fare.setNightBaseFare(nightBaseFare);
                            fare.setNightSJT(nightSJT);
                            fare.setSjt(sjt);
                            fareMap.put(productCode, fare);
                            mainFareList.add(fare);
                        }

                        break;

                    case XmlPullParser.END_TAG:
                        if(parser.getName().equalsIgnoreCase(FARE_CLASS_TAG)){
                            fareClass.setFareMap(fareMap);
                            fareClassList.add(fareClass);
                        }
                        break;
                }
                eventType = parser.next();
            }
            if(!fareClassList.isEmpty()) {
                status = true;
                setFareClassList(fareClassList);
                if (!mainFareList.isEmpty())
                    setFareList(mainFareList);
            }
            return status;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadFareClasses():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }

    private boolean loadLines(String fileName) {
        InputStream fis = null;
        try{
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            File file = new File(fileName);
            fis = new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            boolean status = false;
            int eventType = parser.getEventType();
            List<Line> lineList = new ArrayList<>();

            Integer fareClassCode;
            String lastStop;
            String firstStop;
            String label;
            Integer lineCode;

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equalsIgnoreCase(LINES_TAG)){
                            currentParamVersion = parser.getAttributeValue(null, VERSION_TAG);
                            if(nextParamVersion.equals(currentParamVersion))
                                nextParamVersion = "";
                        }

                        if(parser.getName().equalsIgnoreCase(LINE_TAG)){
                            fareClassCode = Integer.parseInt(parser.getAttributeValue(null, FARE_ID_TAG));
                            lastStop = parser.getAttributeValue(null, LAST_TAG);
                            firstStop = parser.getAttributeValue(null, FIRST_TAG);
                            label = parser.getAttributeValue(null, LABEL_TAG);
                            lineCode = Integer.parseInt(parser.getAttributeValue(null, CODE_TAG));
                            Line line = new Line();
                            line.setFareClassCode(fareClassCode);
                            line.setFirstStop(firstStop);
                            line.setLabel(label);
                            line.setLastStop(lastStop);
                            line.setLineCode(lineCode);
                            lineList.add(line);
                        }
                        break;
                }
                eventType = parser.next();
            }
            if(!lineList.isEmpty()) {
                setLineList(lineList);
                status = true;
            }
            return status;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadLines():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }

    private boolean loadLocalCalendars(String fileName){
        InputStream fis = null;
        try{
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlPullParserFactory.newPullParser();
            File file = new File(fileName);
            fis = new FileInputStream(file);
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(fis, null);
            boolean status = false;
            int eventType = parser.getEventType();
            List<LocalCalendar> calendarList = new ArrayList<LocalCalendar>();
            Date date;
            Integer day;
            Integer month;
            Integer year;
            LocalCalendarDateType dateType;

            if (localCalendarMap != null)
                localCalendarMap.clear();
            else
                localCalendarMap = new HashMap<>();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if(parser.getName().equalsIgnoreCase(CALENDARS_TAG)){
                            currentParamVersion = parser.getAttributeValue(null, VERSION_TAG);
                            if(nextParamVersion.equals(currentParamVersion))
                                nextParamVersion = "";
                        }
                        //start tracing the xml node by node and open the list of objects.
                        if(parser.getName().equalsIgnoreCase(CALENDAR_TAG)){
                            year = Integer.parseInt(parser.getAttributeValue(null, YEAR_TAG));
                            month = Integer.parseInt(parser.getAttributeValue(null, MONTH_TAG));
                            day = Integer.parseInt(parser.getAttributeValue(null, DAY_TAG));
                            dateType = LocalCalendarDateType.dateTypeValueOf(parser.getAttributeValue(null, DATE_TYPE_TAG).toLowerCase());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            Calendar c = Calendar.getInstance();
                            c.setTime(sdf.parse(parser.getAttributeValue(null, DATE_TAG)));
                            date = c.getTime();

                            LocalCalendar lc = new LocalCalendar();
                            lc.setDate(date);
                            lc.setDateType(dateType);
                            lc.setDay(day);
                            lc.setMonth(month);
                            lc.setYear(year);
                            calendarList.add(lc);
                            //FILL CALENDAR MAP BELOW
                            String key = year.toString()+String.format("%02d",month)+String.format("%02d",day);
                            localCalendarMap.put(key, dateType);
                        }
                        break;
                }
                eventType = parser.next();
            }
            if(!calendarList.isEmpty()) {
                setLocalCalendarList(calendarList);
                status = true;
            }
            return status;
        }catch (Exception ex){
            log.error("ParameterUtils --> loadLocalCalendars():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }finally {
            try {
                if (fis != null)
                    fis.close();
            }catch (Exception ex){
                //no need to collect this one.
            }
        }
    }

    public String getLineName(){
        try{
            if (!getLineList().isEmpty()){
                for (Line line : getLineList()){
                    if (line.getLineCode().equals(StateHandler.getInstance().getLineId())) {
                        OutOfServiceHandler.removeErrorFromMap(Constants.OUT_OF_SERVICE_LINE_ID_NOT_EXIST);
                        return line.getLabel() + " (" + StateHandler.getInstance().getLineId() + ")";
                    }
                }
            }
        }catch (Exception ex){
            log.error("ParameterUtils --> getLineName():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        OutOfServiceHandler.addErrorToMap(Constants.OUT_OF_SERVICE_LINE_ID_NOT_EXIST, 1);
        return String.valueOf(StateHandler.getInstance().getLineId());
    }

    public boolean checkForParameterUpdate(){
        try{
            FileUtils.cleanDirectory(new File(Constants.tempFolder));
            boolean state = false;
            boolean downloadState = false;
            boolean updateState = false;
            state = FTPSClientUtility.getInstance().connectAndLoginToServer(StateHandler.getInstance().getCcIPAddress(),
                    Constants.FTP_PORT_NUMBER,
                    StateHandler.getInstance().getSoftFtpUser(),
                    StateHandler.getInstance().getSoftFtpPass());
            if(state) {
                downloadState = FTPSClientUtility.getInstance().downloadFile("PARAM/bus_version.sis", Constants.tempFolder + "version.sis");
                if(downloadState){
                    checkParameterVersion(new File(Constants.tempFolder + "version.sis"));
                    boolean conditionOne = nextParamVersion.equals("") && (!nextParamVersionTemp.equals(currentParamVersion));
                    boolean conditionTwo = (!nextParamVersion.equals("")) && (!currentParamVersionTemp.equals(currentParamVersion)
                            || !nextParamVersionTemp.equals(nextParamVersion)
                            || !loadDateTemp.equals(loadDate));
                    boolean conditionThree = (currentParamVersion.equals("") && nextParamVersion.equals("") && currentParamVersionTemp.equals("") && !nextParamVersionTemp.equals(""));
                    if(conditionOne || conditionTwo || conditionThree){
                        //new param version found, downloading new version param.
                        FileUtils.cleanDirectory(new File(Constants.uploadFolder));
                        downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.valid_code_param_server_address, Constants.uploadFolder + "valid_param.code");
                        if (downloadState) {
                            //lets check which device is able to get the update
                            if (isOurCodeInDownloadList(new File(Constants.uploadFolder + "valid_param.code"))) {
                                //we should get the update
                                updateState = downloadParameterUpdate();
                                return updateState;
                            }
                        }

                    }
                }else {
                    log.error( "ParameterUtils --> checkForParameterUpdate(): Can not download Version.sis form server.");
                }
            }else{
                log.error( "ParameterUtils --> checkForParameterUpdate(): FTP Can not connect to server.");
            }
            return updateState;
        }catch (Exception ex){
            log.error("ParameterUtils --> checkForParameterUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }


    public void checkOperatorsVersion(File versionFile){
        RandomAccessFile raf = null;
        try{
            if(versionFile.exists()){
                raf = new RandomAccessFile(versionFile,"r");
                byte b1 = raf.readByte();
                byte b2 = raf.readByte();
                if(b1 == (byte)'C' && b2 == (byte)'V'){
                    byte[] val = new byte[8];
                    for(int i = 0 ; i < 8 ; i++){
                        val[i] = raf.readByte();
                    }
                    currentOperatorsVersionTemp = "" + ByteUtils.getValue(val);
                }
            }else{
                log.error( "ParameterUtils --> checkOperatorsVersion(): there is no version.sis for check -- Operators");
            }
        }catch (Exception ex){
            log.error("ParameterUtils --> checkOperatorsVersion():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }finally {
            try{
                if (raf != null)
                    raf.close();
            }catch (Exception ex){
                //no need to collect anything
            }
        }
    }

    public boolean checkForOperatorsUpdate(){
        try{

            FileUtils.cleanDirectory(new File(Constants.tempFolder));
            boolean state = false;
            boolean downloadState = false;
            boolean updateState = false;
            state = FTPSClientUtility.getInstance().connectAndLoginToServer(StateHandler.getInstance().getCcIPAddress(),
                    Constants.FTP_PORT_NUMBER,
                    StateHandler.getInstance().getSoftFtpUser(),
                    StateHandler.getInstance().getSoftFtpPass());
            if(state) {
                downloadState = FTPSClientUtility.getInstance().downloadFile("PARAM/operatorversion.sis", Constants.tempFolder + "operatorversion.sis");
                if(downloadState){
                    checkOperatorsVersion(new File(Constants.tempFolder + "operatorversion.sis"));
                    boolean condition = false;
                    if(!currentOperatorsVersion.equals("")){
                        Long currentV = Long.parseLong(currentOperatorsVersion);
                        Long newV = Long.parseLong(currentOperatorsVersionTemp);
                        if(newV > currentV)
                            condition = true;
                    }else{
                        condition = true;
                    }

                    if(condition){
                        //new operator version found, downloading new version operator.
                        updateState = downloadOperatorsUpdate();
                        return updateState;
                    }


                }else {
                    log.error( "ParameterUtils --> checkForOperatorsUpdate(): Can not download Version.sis form server.");
                }
            }else{
                log.error( "ParameterUtils --> checkForOperatorsUpdate(): FTP Can not connect to server.");
            }
            return updateState;
        }catch (Exception ex){
            log.error("ParameterUtils --> checkForOperatorsUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }


    public boolean downloadOperatorsUpdate(){
        try{
            boolean downloadState = false;
            Integer counter = 0;
            FileUtils.cleanDirectory(new File(Constants.tempUpdateFolder));
            while (counter < 3){
                downloadState = FTPSClientUtility.getInstance().downloadFile("PARAM/operators.zip", Constants.tempUpdateFolder + "operators.zip");
                if(downloadState){
                    //download has been done successfully.
                    downloadState = handleOperatorsAfterDownload();
                    counter = 4;
                }else{
                    log.error( "ParameterUtils --> downloadOperatorsUpdate(): Can not download Operator.zip form server.");
                }
                counter++;
            }
            return downloadState;
        }catch (Exception ex){
            log.error("ParameterUtils --> downloadOperatorsUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }


    public boolean handleOperatorsAfterDownload(){
        try{
            File serials = new File(Constants.tempUpdateFolder+"operators.zip");
            if(serials.exists()){
                File temp = new File(Constants.tempFolder);
                FileUtils.cleanDirectory(temp);
                ZipFile zipFile = new ZipFile(serials);
                zipFile.extractAll(Constants.tempFolder);
                FileUtils.copyDirectory(temp, new File(Constants.paramFolder));
                FileUtils.cleanDirectory(temp);
                loadOperators();
                return true;
            }
            return false;
        }catch (Exception ex){
            log.error("ParameterUtils --> handleOperatorsAfterDownload():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }


    public void checkParameterVersion(File versionFile){
        RandomAccessFile raf = null;
        try{
            if(versionFile.exists()){
                raf = new RandomAccessFile(versionFile,"r");
                byte b1 = raf.readByte();
                byte b2 = raf.readByte();
                if(b1 == (byte)'C' && b2 == (byte)'V'){
                    byte[] val = new byte[4];
                    for(int i = 0 ; i < 4 ; i++){
                        val[i] = raf.readByte();
                    }
                    currentParamVersionTemp = "" + ByteUtils.getValue(val);
                    byte b6 = raf.readByte();
                    byte b7 = raf.readByte();
                    if(b6 == (byte)'N' && b7 == (byte)'V'){
                        val = new byte[4];
                        for(int i = 0 ; i < 4 ; i++){
                            val[i] = raf.readByte();
                        }
                        nextParamVersionTemp = "" + ByteUtils.getValue(val);
                        byte b11 = raf.readByte();
                        byte b12 = raf.readByte();
                        if(b11 == (byte)'L' && b12 == (byte)'D')
                        {
                            loadDateTemp = String.format("%02d", raf.readByte())
                                    +String.format("%02d", raf.readByte())
                                    +String.format("%02d", raf.readByte());
                        }
                    }
                }
            }else{
                log.error( "ParameterUtils --> checkParameterVersion(): there is no version.sis for check -- parameter");
            }
        }catch (Exception ex){
            log.error("ParameterUtils --> checkParameterVersion():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }finally {
            try{
                if (raf != null)
                    raf.close();
            }catch (Exception ex){
                //no need to collect anything
            }
        }
    }


    public void getNextParamVersionAndLoadDateFromVersionFile(File versionFile){
        RandomAccessFile raf = null;
        try{
            if(versionFile.exists()){
                raf = new RandomAccessFile(versionFile,"r");
                byte b1 = raf.readByte();
                byte b2 = raf.readByte();
                if(b1 == (byte)'C' && b2 == (byte)'V'){
                    byte[] val = new byte[4];
                    for(int i = 0 ; i < 4 ; i++){
                        val[i] = raf.readByte();
                    }
                    byte b7 = raf.readByte();
                    byte b8 = raf.readByte();
                    if(b7 == (byte)'N' && b8 == (byte)'V'){
                        val = new byte[4];
                        for(int i = 0 ; i < 4 ; i++){
                            val[i] = raf.readByte();
                        }
                        String localNextParamVersion = "" + ByteUtils.getValue(val);
                        if(currentParamVersion.equals(localNextParamVersion))
                            nextParamVersion = "";
                        else
                            nextParamVersion = localNextParamVersion;

                        byte b13 = raf.readByte();
                        byte b14 = raf.readByte();
                        if(b13 == (byte)'L' && b14 == (byte)'D')
                        {
                            loadDate = String.format("%02d", raf.readByte())
                                    +String.format("%02d", raf.readByte())
                                    +String.format("%02d", raf.readByte());
                        }
                    }
                }
            }else{
                log.error( "ParameterUtils --> getNextParamVersionAndLoadDateFromVersionFile(): there is no version.sis for check -- parameter");
            }
        }catch (Exception ex){
            log.error("ParameterUtils --> getNextParamVersionAndLoadDateFromVersionFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }finally {
            try{
                if (raf != null)
                    raf.close();
            }catch (Exception ex){
                //no need to collect anything
            }
        }
    }


    public boolean downloadParameterUpdate(){
        try{
            boolean downloadState = false;
            Integer counter = 0;
            FileUtils.cleanDirectory(new File(Constants.tempUpdateFolder));
            while (counter < 3){
                downloadState = FTPSClientUtility.getInstance().downloadFile("PARAM/BP.zip", Constants.tempUpdateFolder + "parameter.zip");
                if(downloadState){
                    //download has been done successfully.
                    downloadState = handleNewParameterAfterDownload();
                    counter = 4;
                }else{
                    log.error( "ParameterUtils --> downloadParameterUpdate(): Can not download BP.zip form server.");
                }
                counter++;
            }
            return downloadState;
        }catch (Exception ex){
            log.error("ParameterUtils --> downloadParameterUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }



    public Boolean handleNewParameterAfterDownload(){
        try{
            Boolean status = false;
            File paramFile = new File(Constants.tempUpdateFolder + "parameter.zip");
            File tempDir = new File(Constants.tempFolder);
            if(paramFile.exists()){
                if(!tempDir.isDirectory())
                    tempDir.mkdirs();
                FileUtils.cleanDirectory(tempDir);
                ZipFile zipFile = new ZipFile(paramFile);
                zipFile.extractAll(Constants.tempFolder);
                try {
                    FileUtils.cleanDirectory(new File(Constants.currentParamFolder));
                    FileUtils.cleanDirectory(new File(Constants.nextParamFolder));
                    FileUtils.forceDeleteOnExit(new File(Constants.paramFolder + "version.sis"));

                }catch (Exception ex)
                {
                    log.error("cleanDirectory current & next: "+ex.getMessage());
                }
                FileUtils.copyDirectory(tempDir, new File(Constants.paramFolder));
                FileUtils.cleanDirectory(tempDir);
                loadParameters();
                status = true;
            }
            return status;
        }catch (Exception ex){
            log.error("ParameterUtils --> handleNewParameterAfterDownload():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }



    public boolean dailyCheckerForLocalParamUpdate(){
        try{
            Calendar _now = Calendar.getInstance();
            Calendar nextParamLoadDateCal = Calendar.getInstance();
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyMMdd");
            if(loadDate == "")
                return false;
            nextParamLoadDateCal.setTime(dateFormat.parse(loadDate));
            File dir = new File(Constants.nextParamFolder);
            if(nextParamLoadDateCal != null && nextParamVersion!="") {
                if (_now.getTimeInMillis() >= nextParamLoadDateCal.getTimeInMillis()) {
                    if (dir.listFiles().length > 0) {
                        try{
                            FileUtils.cleanDirectory(new File(Constants.currentParamFolder));
                        }catch (Exception e1){}
                        FileUtils.copyDirectory(dir, new File(Constants.currentParamFolder));
                        FileUtils.cleanDirectory(dir);
                        return loadParameters();
                    }
                }
            }
        }catch (Exception ex){
            log.error("ParameterUtils --> dailyCheckerForLocalParamUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }finally {
            isLoadFirstTime = true;
        }
        return false;
    }



    public void checkSoftUpdate() {
        try {
            boolean connectState = false;
            boolean downloadState = false;
            File tempFolder = new File(Constants.tempFolder);
            if (!tempFolder.isDirectory())
                tempFolder.mkdirs();
            else
                FileUtils.cleanDirectory(tempFolder);
            //----------- trying to download version.sis
            connectState = FTPSClientUtility.getInstance().connectAndLoginToServer(StateHandler.getInstance().getCcIPAddress(),
                    Constants.FTP_PORT_NUMBER,
                    StateHandler.getInstance().getSoftFtpUser(),
                    StateHandler.getInstance().getSoftFtpPass());
            if (connectState) {
                downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.new_application_version_server_address, Constants.tempFolder + "version_ag.sis");
                if (downloadState) {
                    String newVersion = getSoftVersionData(Constants.tempFolder + "version_ag.sis");
                    currentSoftVersion = String.valueOf(Utility.getAppVersionCode());
                    if (Integer.valueOf(currentSoftVersion) < Integer.valueOf(newVersion)) {
                        // there is new version of app to download.
                        //lets download valid.code file first
                        File tempUpdateFolder = new File(Constants.uploadFolder);
                        if (!tempUpdateFolder.isDirectory())
                            tempUpdateFolder.mkdirs();
                        else
                            FileUtils.cleanDirectory(tempUpdateFolder);
                        Integer count = 0;
                        while (count < 3) {
                            downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.valid_code_server_address, Constants.uploadFolder + "valid.code");
                            if (downloadState) {
                                count = 5;
                                //lets check which device is able to get the update
                                if (isOurCodeInDownloadList(new File(Constants.uploadFolder + "valid.code"))) {
                                    //we should get the update
                                    Integer countTwo = 0;
                                    while (countTwo < 3) {
                                        downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.new_application_server_address, Constants.uploadFolder + "app.zip");
                                        if (downloadState) {
                                            count = 5;
                                            // new app package has been downloaded.
                                            handleNewAppAfterDownload();
                                        }
                                        countTwo++;
                                    }
                                    handleNewAppAfterDownload();
                                }
                            }
                            count++;
                        }
                    } else {
                        //no new update for app.
                        return;
                    }
                } else {
                    log.error("ParameterUtils --> checkSoftUpdate(): Can not download new application form server.");
                    return;
                }
            } else {
                log.error("ParameterUtils --> checkSoftUpdate(): FTP Can not connect to server.");
                return;
            }
        } catch (Exception ex) {
            log.error("ParameterUtils --> checkSoftUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    public boolean isOurCodeInDownloadList(File validFile){
        try{
            List<String> lines = FileUtils.readLines(validFile);
            if (!lines.isEmpty()){
                for (String line : lines){
                    if (line.trim().toLowerCase().equals("*") || line.trim().toLowerCase().equals(String.valueOf(StateHandler.getInstance().getBusId())))
                        return true;
                }
            }
        }catch (Exception ex){
            log.error("ParameterUtils --> isOurCodeInDownloadList():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return false;
    }


    public String getSoftVersionData(String fileAddress) {
        try {
            FileInputStream fis = new FileInputStream(new File(fileAddress));
            int content;
            String str = "";
            StringBuffer sb = new StringBuffer();
            while ((content = fis.read()) != -1) {
                if ((char) content != '\n' && (char) content != '\r')
                    sb.append((char) content);
            }
            return sb.toString();
        } catch (Exception ex) {
            log.error("ParameterUtils --> getSoftVersionData():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return null;
        }
    }


    public void handleNewAppAfterDownload() {
        try {
            File appFile = new File(Constants.uploadFolder + "app.zip");
            if (appFile.exists()) {
                File tempFolder = new File(Constants.tempFolder);
                if (!tempFolder.isDirectory())
                    tempFolder.mkdirs();
                FileUtils.cleanDirectory(tempFolder);
                ZipFile zipFile = new ZipFile(appFile);
                zipFile.extractAll(Constants.tempFolder);
                File downloadFolder = new File(Constants.newAppFolder);
                if (!downloadFolder.exists())
                    downloadFolder.mkdirs();
                downloadFolder.setReadable(true, false);
                downloadFolder.setWritable(true, false);
                FileUtils.copyDirectory(tempFolder, downloadFolder);
                FileUtils.cleanDirectory(tempFolder);
                File finalAppFile = new File(Constants.newAppFolder+"app.apk");
                finalAppFile.setReadable(true, false);
                finalAppFile.setWritable(true, false);
                finalAppFile.setExecutable(true, false);
                openApp();
            }

        } catch (Exception ex) {
            log.error("ParameterUtils --> handleNewAppAfterDownload():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    private void openApp() {
        try {
            Intent intent = new Intent();
            intent.setAction(Constants.updater_package_name);//silent installer
            intent.putExtra(Utility.getContext().getResources().getString(R.string.path), Constants.newAppFolder + "app.apk");//The value of path is parametric, it can be changed for each customer.
            intent.putExtra(Utility.getContext().getResources().getString(R.string.app_package_name), Constants.application_package_name);//It can be changed, for example: com.scsoft.bus, or com.sbi.validator, or etc
            Utility.getContext().sendBroadcast(intent);
        } catch (Exception ex) {
            log.error("ParameterUtils --> openApp():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public String getCurrentSoftVersion() {
        return currentSoftVersion;
    }

    public void setCurrentSoftVersion(String currentSoftVersion) {
        this.currentSoftVersion = currentSoftVersion;
    }

    public String getCurrentParamVersion() {
        return currentParamVersion;
    }

    public void setCurrentParamVersion(String currentParamVersion) {
        this.currentParamVersion = currentParamVersion;
    }

    public String getNextParamVersion() {
        return nextParamVersion;
    }

    public void setNextParamVersion(String nextParamVersion) {
        this.nextParamVersion = nextParamVersion;
    }

    public String getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(String loadDate) {
        this.loadDate = loadDate;
    }

    public String getCurrentParamVersionTemp() {
        return currentParamVersionTemp;
    }

    public void setCurrentParamVersionTemp(String currentParamVersionTemp) {
        this.currentParamVersionTemp = currentParamVersionTemp;
    }

    public String getNextParamVersionTemp() {
        return nextParamVersionTemp;
    }

    public void setNextParamVersionTemp(String nextParamVersionTemp) {
        this.nextParamVersionTemp = nextParamVersionTemp;
    }

    public String getLoadDateTemp() {
        return loadDateTemp;
    }

    public void setLoadDateTemp(String loadDateTemp) {
        this.loadDateTemp = loadDateTemp;
    }

    public boolean isLoadFirstTime() {
        return isLoadFirstTime;
    }

    public void setLoadFirstTime(boolean loadFirstTime) {
        isLoadFirstTime = loadFirstTime;
    }

    public Map<String, LocalCalendarDateType> getLocalCalendarMap() {
        return localCalendarMap;
    }

    public void setLocalCalendarMap(Map<String, LocalCalendarDateType> localCalendarMap) {
        this.localCalendarMap = localCalendarMap;
    }

    public List<LocalCalendar> getLocalCalendarList() {
        return localCalendarList;
    }

    public void setLocalCalendarList(List<LocalCalendar> localCalendarList) {
        this.localCalendarList = localCalendarList;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

    public List<FareClass> getFareClassList() {
        return fareClassList;
    }

    public void setFareClassList(List<FareClass> fareClassList) {
        this.fareClassList = fareClassList;
    }

    public List<Fare> getFareList() {
        return fareList;
    }

    public void setFareList(List<Fare> fareList) {
        this.fareList = fareList;
    }

    public String getNightTimeFrom() {
        return nightTimeFrom;
    }

    public void setNightTimeFrom(String nightTimeFrom) {
        this.nightTimeFrom = nightTimeFrom;
    }

    public String getNightTimeTo() {
        return nightTimeTo;
    }

    public void setNightTimeTo(String nightTimeTo) {
        this.nightTimeTo = nightTimeTo;
    }

    public Long getAntiPassBack() {
        if (antiPassBack != null)
            return antiPassBack*1000;
        else
            return null;
    }

    public List<Operator> getOperatorsList() {
        return operatorsList;
    }

    public void setOperatorsList(List<Operator> operatorsList) {
        this.operatorsList = operatorsList;
    }

    public List<Bus> getBusList() {
        return busList;
    }

    public void setBusList(List<Bus> busList) {
        this.busList = busList;
    }

    public String getCurrentOperatorsVersion() {
        return currentOperatorsVersion;
    }

    public void setCurrentOperatorsVersion(String currentOperatorsVersion) {
        this.currentOperatorsVersion = currentOperatorsVersion;
    }

    public String getCurrentOperatorsEVD() {
        return currentOperatorsEVD;
    }

    public void setCurrentOperatorsEVD(String currentOperatorsEVD) {
        this.currentOperatorsEVD = currentOperatorsEVD;
    }

    public String getCurrentOperatorsVersionTemp() {
        return currentOperatorsVersionTemp;
    }

    public void setCurrentOperatorsVersionTemp(String currentOperatorsVersionTemp) {
        this.currentOperatorsVersionTemp = currentOperatorsVersionTemp;
    }


    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }

    public Integer getBrightness() {
        return brightness;
    }
}
