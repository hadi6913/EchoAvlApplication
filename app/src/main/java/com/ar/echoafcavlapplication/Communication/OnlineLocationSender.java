package com.ar.echoafcavlapplication.Communication;

import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Models.MessageProtocol;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class OnlineLocationSender {
    private static Logger log = LoggerFactory.getLogger(OnlineLocationSender.class);

    private TCPClientUtility clientUtility = null;
    private ScheduledThreadPoolExecutor exec;
    private static OnlineLocationSender locationSender = null;
    Thread tc ;

    private OnlineLocationSender(){
        clientUtility = new TCPClientUtility(StateHandler.getInstance().getCcIPAddress(), Constants.onlineLocationSenderPort);
        tc = new Thread(clientUtility);
        tc.start();
    }

    public static OnlineLocationSender getInstance(){
        if(locationSender == null)
            locationSender = new OnlineLocationSender();
        return locationSender;
    }

    public String sendInquiry(final MessageProtocol cmd)
    {
        if(clientUtility.isOnline())
        {
            return clientUtility.sendInfoCommand(cmd.ToTheString());
        }
        return null;
    }

    public boolean isOnline(){
        return clientUtility.isOnline();
    }

    public void refresh(){
        clientUtility.refreshConnection();
    }

    public void stop()
    {
        clientUtility.stop();
    }
}


