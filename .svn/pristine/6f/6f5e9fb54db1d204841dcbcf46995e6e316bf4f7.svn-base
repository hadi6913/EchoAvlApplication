package com.ar.echoafcavlapplication.Fragments;


import android.app.ActivityManager;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Services.LocationHandler;
import com.ar.echoafcavlapplication.Utils.Utility;
import com.potterhsu.Pinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ACTIVITY_SERVICE;

public class FragmentHardwareTest extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentHardwareTest.class);
    private TextView memoryView, gpsView;
    private ImageView netImg, gpsImg;
    private Button netBtn;
    private TestFourG testFourG;
    private GPSListener gpsListener;
    private Long lat, lng;
    private Date lastReceived = new Date();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hardware_test_frag, parent, false);
        memoryView = v. findViewById(R.id.memoryTxt);
        gpsView = v. findViewById(R.id.gpsTestTxt);
        netImg = v.findViewById(R.id.netResult);
        gpsImg = v.findViewById(R.id.gpsResult);
        netBtn = v.findViewById(R.id.netTestBtn);
        lat = 0L;
        lng = 0L;
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            memoryView.setText(getMemoryStatus());
        }catch (Exception ex){
            log.error("FragmentHardwareTest > onViewCreated():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void startFourGTest(){
        try {
            testFourG = new TestFourG();
            testFourG.start();
        }catch (Exception ex){
            log.error("FragmentHardwareTest > startFourGTest():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    public void startGPSListener(){
        try {
            gpsListener = new GPSListener();
            gpsListener.start();
        }catch (Exception ex){
            log.error("FragmentHardwareTest > startGPSListener():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    boolean newData = false;
    public class GPSListener extends Thread{
        @Override
        public void run() {
            super.run();
            try{
                lat = LocationHandler.getInstance().getCurrentLat();
                lng = LocationHandler.getInstance().getCurrentLon();

                while (true){
                    StringBuilder builder = new StringBuilder();
                    builder.append("Last Latitude: "+ LocationHandler.getInstance().getCurrentLat()+"\n");
                    builder.append("Last Longitude: "+ LocationHandler.getInstance().getCurrentLon()+"\n");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gpsView.setText(builder.toString());
                        }
                    });

                    if ((lat != LocationHandler.getInstance().getCurrentLat()) && (lng != LocationHandler.getInstance().getCurrentLon())){
                        lat = LocationHandler.getInstance().getCurrentLat();
                        lng = LocationHandler.getInstance().getCurrentLon();
                        lastReceived = new Date();
                        newData = true;
                    }

                    if (Math.abs(Calendar.getInstance().getTimeInMillis()-lastReceived.getTime()) > 60*1000){
                        //error
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                gpsImg.setImageResource(R.drawable.cancel);
                            }

                        });
                    }else{
                        //ok
                        if (newData) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gpsImg.setImageResource(R.drawable.checked);
                                }
                            });
                            newData = false;
                        }
                    }
                    Thread.sleep(1000);
                }
            }catch (Exception ex){
                log.error("FragmentHardwareTest > GPSListener > run():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
            }
        }
    }

    public class TestFourG extends Thread{
        @Override
        public void run() {
            try{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netBtn.setEnabled(false);
                    }
                });
                Pinger pinger = new Pinger();
                if(pinger.ping("www.google.com", 2)){
                    //ping successfully done
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netImg.setImageResource(R.drawable.checked);
                        }
                    });
                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            netImg.setImageResource(R.drawable.cancel);
                        }
                    });
                }
            }catch (Exception ex){
                log.error("FragmentHardwareTest > TestFourG > run():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
            }finally {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        netBtn.setEnabled(true);
                    }
                });
            }
        }
    }


    public String getMemoryStatus(){
        try{
            StringBuilder builder = new StringBuilder();
            builder.append("مقدار حافظه داخلی در دسترس: "+Utility.getAvailableInternalMemorySize()+"\n");
            builder.append("مقدار کل حافظه داخلی: "+Utility.getTotalInternalMemorySize()+"\n");
            builder.append("مقدار حافظه خارجی در دسترس: "+Utility.getAvailableExternalMemorySize()+"\n");
            builder.append("مقدار کل حافظه خارجی: "+Utility.getTotalExternalMemorySize());
            return builder.toString();
        }catch (Exception ex){
            log.error("FragmentHardwareTest > getMemoryStatus():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
        return "---";
    }



}
