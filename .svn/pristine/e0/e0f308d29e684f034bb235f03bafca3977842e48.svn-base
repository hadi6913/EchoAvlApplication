package com.ar.echoafcavlapplication.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class FragmentMaintenance extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentMaintenance.class);
    private EditText ccIpAddressBox, lineIDBox, busIDBox, appUserBox, appPassBox, statUserBox, statPassBox;
    private TextView msgTextView;
    private File confFile = new File(Constants.confFolder + "config.properties");
    private String LINE_ID_STR = "line_id";
    private String BUS_ID_STR = "bus_id";
    private String SOFT_FTP_USER_STR = "soft_ftp_user";
    private String SOFT_FTP_PASS_STR = "soft_ftp_pass";
    private String STAT_FTP_USER_STR = "stat_ftp_user";
    private String STAT_FTP_PASS_STR = "stat_ftp_pass";
    private String CC_IP_STR = "CC_ip_address";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maintenance_frag, parent, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ccIpAddressBox = view.findViewById(R.id.ccIpAddress);
        lineIDBox = view.findViewById(R.id.lineId);
        busIDBox = view.findViewById(R.id.busId);
        appUserBox = view.findViewById(R.id.appUser);
        appPassBox = view.findViewById(R.id.appPass);
        statUserBox = view.findViewById(R.id.statUser);
        statPassBox = view.findViewById(R.id.statPass);
        msgTextView = view.findViewById(R.id.msg);
        init();
    }

    public void init(){
        try{
            Thread.sleep(500);
           if (confFile.exists()){
               //load
               loadConfigFile();
           }else{
               //create
               StateHandler.getInstance().createConfigFile();
               loadConfigFile();
           }
        }catch (Exception ex){
            log.error("FragmentMaintenance > init():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void loadConfigFile(){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ccIpAddressBox.setText(StateHandler.getInstance().getCcIPAddress());
                    lineIDBox.setText(String.valueOf(StateHandler.getInstance().getLineId()));
                    busIDBox.setText(String.valueOf(StateHandler.getInstance().getBusId()));
                    appUserBox.setText(StateHandler.getInstance().getSoftFtpUser());
                    appPassBox.setText(StateHandler.getInstance().getSoftFtpPass());
                    statUserBox.setText(StateHandler.getInstance().getStatFtpUser());
                    statPassBox.setText(StateHandler.getInstance().getStatFtpPass());
                }
            });

        }catch (Exception ex){
            log.error("FragmentMaintenance > loadConfigFile():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void saveChanges(){
        try{
            Map<String, String> param = new HashMap<>();
            param.put(CC_IP_STR, ccIpAddressBox.getText().toString());
            if (!lineIDBox.getText().toString().trim().equals(""))
                param.put(LINE_ID_STR, lineIDBox.getText().toString());
            else
                param.put(LINE_ID_STR, "0");
            if (!busIDBox.getText().toString().trim().equals(""))
                param.put(BUS_ID_STR, busIDBox.getText().toString());
            else
                param.put(BUS_ID_STR, "0");
            param.put(SOFT_FTP_USER_STR, appUserBox.getText().toString());
            param.put(SOFT_FTP_PASS_STR, appPassBox.getText().toString());
            param.put(STAT_FTP_USER_STR, statUserBox.getText().toString());
            param.put(STAT_FTP_PASS_STR, statPassBox.getText().toString());
            StateHandler.getInstance().changeAllValue(param);
            showMsg("با موفقیت ذخیره شد");
        }catch (Exception ex){
            log.error("FragmentMaintenance > saveChanges():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void showMsg(String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msgTextView.animate().alpha(1.0f).setDuration(300);
                msgTextView.setText(msg);
            }
        });
    }



}
