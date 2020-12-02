package com.ar.echoafcavlapplication.SerialPort;

import android.util.Log;

import com.ar.echoafcavlapplication.Utils.ByteUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android_serialport_api.SerialPort;

public abstract class SerialHelper{
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
    private String sPort="/dev/s3c2410_serial0";
    private int iBaudRate=9600;
    private boolean _isOpen=false;
    private byte[] _bLoopData=new byte[]{0x30};
    private int iDelay=500;
    private boolean isRS485 = false;
    private Object lock = new Object();

    //----------------------------------------------------
    public SerialHelper(String sPort,int iBaudRate){
        this.sPort = sPort;
        this.iBaudRate=iBaudRate;
    }
    public SerialHelper(){
        this("/dev/s3c2410_serial0",9600);
    }
    public SerialHelper(String sPort){
        this(sPort,9600);
    }
    public SerialHelper(String sPort,String sBaudRate){
        this(sPort,Integer.parseInt(sBaudRate));
    }
    //----------------------------------------------------
    public void open() throws SecurityException, IOException, InvalidParameterException {
        mSerialPort =  new SerialPort(new File(sPort), iBaudRate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        /*mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();*/
        _isOpen=true;
        isRS485 = false;
    }

    public void openRS485() throws SecurityException, IOException,InvalidParameterException {
        mSerialPort =  new SerialPort(new File(sPort), iBaudRate, 0);
        mOutputStream = mSerialPort.getOutputStream();
        mInputStream = mSerialPort.getInputStream();
        /*mReadThread = new ReadThread();
        mReadThread.start();
        mSendThread = new SendThread();
        mSendThread.setSuspendFlag();
        mSendThread.start();*/
        _isOpen=true;
        isRS485 = true;
    }

    //----------------------------------------------------
    public void close(){
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
        _isOpen=false;
    }
    //----------------------------------------------------
    public void send(byte[] bOutArray){
        synchronized (lock) {
            try {
                mOutputStream.write(bOutArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //----------------------------------------------------
    public void sendHex(String sHex){
        byte[] bOutArray = MyFunc.HexToByteArr(sHex);
        send(bOutArray);
    }
    //----------------------------------------------------
    public void sendTxt(String sTxt){
        byte[] bOutArray =sTxt.getBytes();
        send(bOutArray);
    }
    //----------------------------------------------------

    //added by Mohsen
    public ComBean read(){
        synchronized (lock) {
            try {
                if (mInputStream == null) {
                    return null;
                }
                byte[] buffer = new byte[2048];
                int size = mInputStream.read(buffer);
                if (size > 0) {
                    ComBean ComRecData = new ComBean(sPort, buffer, size);
                    return ComRecData;
                }
            } catch (Exception ex) {
                Log.e("MOS", "READ Exception Occurred");
                ex.printStackTrace();
            }
        }
        return null;
    }


    public byte[] readReader(){
        synchronized (lock) {
            try {
                if (mInputStream == null) {
                    return null;
                }
                readByte();
                readByte();
                int length = readByte();
                List<Byte> res = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    res.add(readByte());
                }
                res.add(readByte());
                return ByteUtils.convertByteToPrimitive(res);
            } catch (Exception ex) {
                Log.e("MOS", "readReader Exception Occurred");
                ex.printStackTrace();
            }
        }
        return null;
    }

    public byte readByte(){
        synchronized (lock) {
            try {
                if (mInputStream == null) {
                    return 0;
                }
                byte[] buffer = new byte[1];
                int size = mInputStream.read(buffer);
                if (size > 0) {
                    return buffer[0];
                }
            } catch (Exception ex) {
                Log.e("MOS", "readByte Exception Occurred");
                ex.printStackTrace();
            }
        }
        return 0;
    }

    public void ignorePre() {
        synchronized (lock) {
            try {
                mInputStream.skip(mInputStream.available());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    //----------------------------------------------------

    public boolean stopCond = false;
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            while(!stopCond) {
                try
                {
                    if (mInputStream == null) {
                        return;
                    }
                    byte[] buffer=new byte[2048];
                    int size = mInputStream.read(buffer);
                    if (size > 0){
                        ComBean ComRecData = new ComBean(sPort,buffer,size);
                        onDataReceived(ComRecData);
                        if (!isRS485)
                            mInputStream.skip(mInputStream.available());
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.e("MOS", "READ Thread Exception Occurred");
                    e.printStackTrace();
                    return;
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
        }
    }
    //----------------------------------------------------
    private class SendThread extends Thread{
        public boolean suspendFlag = true;// 控制线程的执行
        @Override
        public void run() {
            super.run();
            while(!stopCond) {
                synchronized (this)
                {
                    while (suspendFlag)
                    {
                        try
                        {
                            wait();
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                send(getbLoopData());
                try
                {
                    Thread.sleep(iDelay);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }

        //线程暂停
        public void setSuspendFlag() {
            this.suspendFlag = false;
        }

        //唤醒线程
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }
    //----------------------------------------------------
    public int getBaudRate()
    {
        return iBaudRate;
    }
    public boolean setBaudRate(int iBaud)
    {
        if (_isOpen)
        {
            return false;
        } else
        {
            iBaudRate = iBaud;
            return true;
        }
    }
    public boolean setBaudRate(String sBaud)
    {
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }
    //----------------------------------------------------
    public String getPort()
    {
        return sPort;
    }
    public boolean setPort(String sPort)
    {
        if (_isOpen)
        {
            return false;
        } else
        {
            this.sPort = sPort;
            return true;
        }
    }
    //----------------------------------------------------
    public boolean isOpen()
    {
        return _isOpen;
    }
    //----------------------------------------------------
    public byte[] getbLoopData()
    {
        return _bLoopData;
    }
    //----------------------------------------------------
    public void setbLoopData(byte[] bLoopData)
    {
        this._bLoopData = bLoopData;
    }
    //----------------------------------------------------
    public void setTxtLoopData(String sTxt){
        this._bLoopData = sTxt.getBytes();
    }
    //----------------------------------------------------
    public void setHexLoopData(String sHex){
        this._bLoopData = MyFunc.HexToByteArr(sHex);
    }
    //----------------------------------------------------
    public int getiDelay()
    {
        return iDelay;
    }
    //----------------------------------------------------
    public void setiDelay(int iDelay)
    {
        this.iDelay = iDelay;
    }
    //----------------------------------------------------
    public void startSend()
    {
        if (mSendThread != null)
        {
            mSendThread.setResume();
        }
    }
    //----------------------------------------------------
    public void stopSend()
    {
        if (mSendThread != null)
        {
            mSendThread.setSuspendFlag();
        }
    }
    //----------------------------------------------------
    protected abstract void onDataReceived(ComBean ComRecData);
}

