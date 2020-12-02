package com.ar.echoafcavlapplication.Fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ar.echoafcavlapplication.Data.SummaryReportFileHandler;
import com.ar.echoafcavlapplication.Models.SummaryReportModel;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class FragmentSummaryReport extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentSummaryReport.class);
    private TableLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.summary_report_frag, parent, false);
        tableLayout = v.findViewById(R.id.main_table);
        drawResults();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    private void drawResults(){
        try{
            List<SummaryReportModel> result = SummaryReportFileHandler.getInstance().getAllRecords();
            if (result.isEmpty()){
                // TODO: 7/13/2020 show no record

            }else{
                //lets create headers first
                TableRow row= new TableRow(Utility.getContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                row.setLayoutParams(lp);
                TextView numberH = new TextView(Utility.getContext());
                numberH.setBackgroundResource(R.drawable.btn_bg_header_cell);
                numberH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
               /* TextView shiftIdH = new TextView(Utility.getContext());
                shiftIdH.setBackgroundResource(R.drawable.btn_bg_header_cell);
                shiftIdH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);*/
                TextView dateH = new TextView(Utility.getContext());
                dateH.setBackgroundResource(R.drawable.btn_bg_header_cell);
                dateH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView startH = new TextView(Utility.getContext());
                startH.setBackgroundResource(R.drawable.btn_bg_header_cell);
                startH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView endH = new TextView(Utility.getContext());
                endH.setBackgroundResource(R.drawable.btn_bg_header_cell);
                endH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TextView countH = new TextView(Utility.getContext());
                countH.setBackgroundResource(R.drawable.btn_bg_header_cell);
                countH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                numberH.setText("ردیف");
                numberH.setPadding(3, 3, 3, 3);
                /*shiftIdH.setText("شناسه");
                shiftIdH.setPadding(3, 3, 3, 3);*/
                dateH.setText("تاریخ");
                dateH.setPadding(3, 3, 3, 3);
                startH.setText("شروع");
                startH.setPadding(3, 3, 3, 3);
                endH.setText("پایان");
                endH.setPadding(3, 3, 3, 3);
                countH.setText("تعداد");
                countH.setPadding(3, 3, 3, 3);
                row.addView(numberH);
                /*row.addView(shiftIdH);*/
                row.addView(dateH);
                row.addView(startH);
                row.addView(endH);
                row.addView(countH);
                tableLayout.addView(row,0);

                //lets create each row
                int rowCount = 1;
                for (SummaryReportModel model : result){
                    TableRow innerRow= new TableRow(Utility.getContext());
                    TableRow.LayoutParams innerLp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                    innerRow.setLayoutParams(innerLp);
                    TextView number = new TextView(Utility.getContext());
                    /*TextView shiftId = new TextView(Utility.getContext());*/
                    TextView date = new TextView(Utility.getContext());
                    TextView start = new TextView(Utility.getContext());
                    TextView end = new TextView(Utility.getContext());
                    TextView count = new TextView(Utility.getContext());
                    number.setBackgroundResource(R.drawable.btn_bg_default_cell);
                    number.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    number.setTextColor(Color.BLACK);
                    /*shiftId.setBackgroundResource(R.drawable.btn_bg_default_cell);
                    shiftId.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    shiftId.setTextColor(Color.BLACK);*/
                    date.setBackgroundResource(R.drawable.btn_bg_default_cell);
                    date.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    date.setTextColor(Color.BLACK);
                    start.setBackgroundResource(R.drawable.btn_bg_default_cell);
                    start.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    start.setTextColor(Color.BLACK);
                    end.setBackgroundResource(R.drawable.btn_bg_default_cell);
                    end.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    end.setTextColor(Color.BLACK);
                    count.setBackgroundResource(R.drawable.btn_bg_default_cell);
                    count.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    count.setTextColor(Color.BLACK);

                    number.setText(String.valueOf(rowCount));
                    number.setPadding(3, 3, 3, 3);
                    /*shiftId.setText(model.getShiftId().toString());
                    shiftId.setPadding(3, 3, 3, 3);*/
                    PersianDate persianDate = new PersianDate(new SimpleDateFormat("yyMMdd").parse(model.getDate()));
                    date.setText(persianDate.getShDay() + " " + persianDate.monthName() + " " + persianDate.getShYear());
                    date.setPadding(3, 3, 3, 3);
                    start.setText(model.getStart());
                    start.setPadding(3, 3, 3, 3);
                    if (model.getEnd().equals("?")){
                        end.setText("---");
                    }else{
                        end.setText(model.getEnd());
                    }
                    end.setPadding(3, 3, 3, 3);
                    if (model.getTransactionCount().equals(-1)){
                        count.setText("---");
                    }else{
                        count.setText(model.getTransactionCount().toString());
                    }
                    count.setPadding(3, 3, 3, 3);
                    innerRow.addView(number);
                    /*innerRow.addView(shiftId);*/
                    innerRow.addView(date);
                    innerRow.addView(start);
                    innerRow.addView(end);
                    innerRow.addView(count);
                    tableLayout.addView(innerRow,rowCount);
                    rowCount+=1;
                }
            }
        }catch (Exception ex){
            log.error( "FragmentSummaryReport > drawResults():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

}
