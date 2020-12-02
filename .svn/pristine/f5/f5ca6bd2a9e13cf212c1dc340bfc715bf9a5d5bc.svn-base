package com.ar.echoafcavlapplication.Data;

import com.ar.echoafcavlapplication.Models.SummaryReportModel;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class SummaryReportFileHandler {
    private static Logger log = LoggerFactory.getLogger(SummaryReportFileHandler.class);
    private static SummaryReportFileHandler instance = null;
    private Object lock = new Object();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private List<String> lines = new ArrayList<>();
    private File reportDir = new File(Constants.summaryReportDataFolder);
    private File reportFile = new File(Constants.summaryReportDataFolder+"report.txt");

    public static SummaryReportFileHandler getInstance(){
        if (instance == null)
            instance = new SummaryReportFileHandler();
        return instance;
    }

    public SummaryReportFileHandler() {
        initial();
    }

    private void initial(){
        try{
            if (!reportDir.isDirectory())
                reportDir.mkdirs();
            if (!reportFile.exists())
                reportFile.createNewFile();
        }catch (Exception ex){
            log.error( "SummaryReportFileHandler > initial():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void writeFirstPartIntoReport(Long shiftId, Date start){
        try{
            synchronized (lock){
                lines.clear();
                lines = FileUtils.readLines(reportFile);
                if (!lines.isEmpty()){
                    if (!lines.get(lines.size()-1).endsWith("#")){
                        StringBuilder builder = new StringBuilder("\n");
                        builder.append(shiftId+"_"+dateFormat.format(start)+"_"+timeFormat.format(start)+"_");
                        FileUtils.writeStringToFile(reportFile, builder.toString(), true);
                        return;
                    }
                }
                StringBuilder builder = new StringBuilder();
                builder.append(shiftId+"_"+dateFormat.format(start)+"_"+timeFormat.format(start)+"_");
                FileUtils.writeStringToFile(reportFile, builder.toString(), true);
            }
        }catch (Exception ex){
            log.error( "SummaryReportFileHandler > writeFirstPartIntoReport():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void writeSecondPartIntoReport(Long shiftId, Date end, Integer count){
        try{
            synchronized (lock){
                lines.clear();
                lines = FileUtils.readLines(reportFile);
                if (lines.isEmpty())
                    return;
                StringBuilder builder = new StringBuilder();
                builder.append(timeFormat.format(end)+"_"+count+"#"+"\n");
                FileUtils.writeStringToFile(reportFile, builder.toString(), true);
                checkReportSize();
            }
        }catch (Exception ex){
            log.error( "SummaryReportFileHandler > writeFirstPartIntoReport():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public void checkReportSize(){
        try{
            synchronized (lock) {
                lines.clear();
                lines = FileUtils.readLines(reportFile);
                if (lines.isEmpty())
                    return;
                if (lines.size() > Constants.REPORT_DATA_LIMIT){
                    List<String> newData = lines.subList(1, lines.size());
                    FileUtils.writeLines(reportFile, newData);
                }
            }
        }catch (Exception ex){
            log.error( "SummaryReportFileHandler > checkReportSize():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public List<SummaryReportModel> getAllRecords(){
        List<SummaryReportModel> res = new ArrayList<>();
        try{
            synchronized (lock){
                lines.clear();
                lines = FileUtils.readLines(reportFile);
                if (lines.isEmpty())
                    return res;
                for (String line : lines){
                    /*if (line.endsWith("#")) {*/
                        String[] tOne = line.split("_");

                        if (tOne.length == 5){
                            SummaryReportModel model = new SummaryReportModel(Long.parseLong(tOne[0]),
                                    tOne[1],
                                    tOne[2],
                                    tOne[3],
                                    Integer.parseInt(tOne[4].split("#")[0]));
                            res.add(model);
                        }else{
                            SummaryReportModel model = new SummaryReportModel(Long.parseLong(tOne[0]),
                                    tOne[1],
                                    tOne[2],
                                    "?",
                                    -1);
                            res.add(model);
                        }

                        /*Integer vC = -1;
                        if (!tOne[4].split("#")[0].equals("?")){
                            vC = Integer.parseInt(tOne[4].split("#")[0]);
                        }
                        SummaryReportModel model = new SummaryReportModel(Long.parseLong(tOne[0]),
                                tOne[1],
                                tOne[2],
                                tOne[3],
                                vC);
                        res.add(model);*/
                    /*}*/
                }
            }
        }catch (Exception ex){
            log.error( "SummaryReportFileHandler > getAllRecords():" + ex.getMessage());
            log.error(ex.toString());
        }
        return res;
    }
}
