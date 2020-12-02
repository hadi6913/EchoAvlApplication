package com.ar.echoafcavlapplication.Communication;

import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Models.MessageProtocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TCPClientUtility implements Runnable {
    private static Logger log = LoggerFactory.getLogger(TCPClientUtility.class);
    private static SimpleDateFormat dateTimeSDF = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm:ss");

    HashMap<String, String> keepAliveMap = new HashMap<String, String>();
    private boolean continueSendingLog = true;
    private static final int INTERVAL_SECONDS = 10;
    private Socket onlineSocket;
    private boolean isOnline = false;
    private Date lastTryToConnectDate;
    private String host;
    private int port;
    private String keepAlive;

    public TCPClientUtility(String host, int port)
    {
        this.host = host;
        this.port = port;
        lastTryToConnectDate = new Date();
        keepAliveMap.put(MessageProtocol.BusCodeAttribute, String.valueOf(StateHandler.getInstance().getBusId()));

    }

    private Object lockObj = new Object();

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
        }

        while (continueSendingLog) {
            try {
                keepAliveConnect();
                Thread.sleep(INTERVAL_SECONDS * 1000);
            } catch (Exception e) {
                log.error("TCPClientUtility > run:" + e.getMessage());
                log.error(e.toString());
            }
        }
    }


    private void keepAliveConnect() {
        try {
            if (onlineSocket == null
                    || (onlineSocket.isClosed() || !onlineSocket
                    .isConnected())) {
                isOnline = false;

                internalRefreshConnection();
            }
            if(onlineSocket != null) {
                Date now = new Date();
                if ((Math.abs(now.getTime() - lastTryToConnectDate.getTime()) / 1000 ) >= INTERVAL_SECONDS) {

                    keepAlive = sendInfo(new MessageProtocol(MessageProtocol.KeepAliveCommand, new Date(), keepAliveMap).ToTheString());
                }
                isOnline = true;
            }
        } catch (Exception e) {
            log.error( "error in sending keep alive:" + e.getMessage());
            log.error(e.toString());
            isOnline = false;
            internalRefreshConnection();
        }
    }

    public String sendInfoCommand(String strCmd)
    {
        try{
            return sendInfo(strCmd);
        }catch (Exception ex)
        {
            closeConnection();
        }
        return null;
    }

    public void sendInfoWithIgnoreInputCommand(String strCmd)
    {
        try{
            sendInfoWithIgnoreInput(strCmd);
        }catch (Exception ex)
        {
            closeConnection();
        }
    }

    private String sendInfo(String str) throws IOException {
        synchronized (lockObj) {
            lastTryToConnectDate = new Date();
            BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(onlineSocket.getOutputStream()));
            BufferedReader inReader = new BufferedReader(new InputStreamReader(
                    onlineSocket.getInputStream()));
            outWriter.write(str + "\r\n");
            outWriter.flush();
            if(!str.startsWith(MessageProtocol.AckCommand)) {
                return inReader.readLine();
            }
            return "";
        }
    }

    private void sendInfoWithIgnoreInput(String str) throws IOException {
        synchronized (lockObj) {
            lastTryToConnectDate = new Date();
            BufferedWriter outWriter = new BufferedWriter(new OutputStreamWriter(onlineSocket.getOutputStream()));
            BufferedReader inReader = new BufferedReader(new InputStreamReader(
                    onlineSocket.getInputStream()));
            outWriter.write(str + "\r\n");
            outWriter.flush();
            if (inReader.ready())
                inReader.readLine();
        }
    }

    public void closeConnection()
    {
        log.error( "closeConnection() called.....");
        synchronized (lockObj) {
            try
            {
                if (onlineSocket != null)
                {
                    try{
                        onlineSocket.shutdownInput();
                    }catch (Exception e1){}
                    try{
                        onlineSocket.shutdownOutput();
                    }catch (Exception e1){}
                    try{
                        onlineSocket.close();
                    }catch (Exception e1){}
                    try{
                        //outWriter.close();
                    }catch (Exception e1){}
                    try{
                        //inReader.close();
                    }catch (Exception e1){}
                }
            }
            catch (Exception e1)
            {

            }
            onlineSocket = null;
            //outWriter = null;
            //inReader = null;
            isOnline = false;

        }
    }

    public void internalRefreshConnection()
    {
        log.error( "internalRefreshConnection() called.....");
        synchronized (lockObj) {
            try
            {
                if (onlineSocket != null)
                {
                    try{
                        onlineSocket.shutdownInput();
                    }catch (Exception e1){}
                    try{
                        onlineSocket.shutdownOutput();
                    }catch (Exception e1){}
                    try{
                        onlineSocket.close();
                    }catch (Exception e1){}
                    try{
                        //outWriter.close();
                    }catch (Exception e1){}
                    try{
                        //inReader.close();
                    }catch (Exception e1){}
                }
            }
            catch (Exception e1)
            {

            }
            onlineSocket = null;
            //outWriter = null;
            //inReader = null;
            isOnline = false;
            try {
                onlineSocket = new Socket(host, port);
                onlineSocket.setSoTimeout(15000);
                //outWriter = new BufferedWriter(new OutputStreamWriter(onlineSocket.getOutputStream()));
                //inReader = new BufferedReader(new InputStreamReader(
                //onlineSocket.getInputStream()));
            }catch (Exception ex)
            {
                log.error( "TCPClientUtility >internalRefreshConnection()"+ex.getMessage());
            }
        }
    }

    public void refreshConnection()
    {
        log.error( "refreshConnection() called.....");
        synchronized (lockObj) {
            try {
                if (onlineSocket != null) {
                    try {
                        onlineSocket.shutdownInput();
                    } catch (Exception e1) {
                    }
                    try {
                        onlineSocket.shutdownOutput();
                    } catch (Exception e1) {
                    }
                    try {
                        onlineSocket.close();
                    } catch (Exception e1) {
                    }
                    try {
                        //outWriter.close();
                    } catch (Exception e1) {
                    }
                    try {
                        //inReader.close();
                    } catch (Exception e1) {
                    }
                }
            } catch (Exception e1) {

            }
            onlineSocket = null;
            //outWriter = null;
            //inReader = null;
            isOnline = false;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastTryToConnectDate);
            calendar.add(Calendar.SECOND, -20);
            lastTryToConnectDate = calendar.getTime();

        }
    }
    public boolean isOnline() {
        return isOnline;
    }


    public void stop()
    {
        continueSendingLog = false;
        closeConnection();
    }

    public String getKeepAlive()
    {
        return keepAlive;
    }
}

