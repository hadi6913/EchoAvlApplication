package com.ar.echoafcavlapplication.Communication;

import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Models.MessageProtocol;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.Utility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class FTPSClientUtility {
    private static Logger log = LoggerFactory.getLogger(FTPSClientUtility.class);
    private static FTPSClient ftp = null;
    private static FTPSClientUtility ftpClientUtility = null;
    private static boolean isConnected = false;

    public FTPSClientUtility() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            ftp = new FTPSClient(sslContext);
        } catch (Exception ex) {
            log.error("FTPSClientUtility() -->  constructor:" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public static FTPSClientUtility getInstance(){
        if(ftpClientUtility == null)
            ftpClientUtility = new FTPSClientUtility();
        return ftpClientUtility;
    }

    public void destroyFTP() throws IOException {
        if(ftp != null){
            try {
                if(ftp.isConnected()) {
                    isConnected = false;
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException ex) {
                log.error( "FTPSClientUtility() -->  destroyFTP():" + ex.getMessage());
                log.error(ex.toString());
            }
        }
    }

    public Boolean connectAndLoginToServer(String server, int portNumber, String user, String password) throws IOException {
        try{
            boolean success = false;
            ftp.setDefaultTimeout(8*60*1000);
            ftp.connect(server, portNumber);
            success = ftp.login(user, password);
            if(success)
                isConnected = true;
            else
                isConnected = false;
            return success;
        }catch (Exception ex){
            log.error( "FTPSClientUtility() -->  connectToServer():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public Boolean downloadFile(String fileNameWantToDownload, String fileNameWantToSave) throws IOException {
        OutputStream outputStream = null;
        try {
            if(!isConnected()) {
                connectAndLoginToServer(StateHandler.getInstance().getCcIPAddress(), Constants.FTP_PORT_NUMBER, StateHandler.getInstance().getSoftFtpUser(), StateHandler.getInstance().getSoftFtpPass());
            }
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftp.execPBSZ(0);
            ftp.execPROT("P");

            boolean success = false;
            File file = new File(fileNameWantToSave);
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
            success = ftp.retrieveFile(fileNameWantToDownload, outputStream);
            return success;

        }catch (Exception ex){
            log.error( "FTPClientUtility() -->  downloadFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
        finally {
            ftp.logout();
            ftp.disconnect();
            isConnected = false;
            if (outputStream != null)
                outputStream.close();
        }
    }




    public Boolean uploadListOfImageFile(String server, int portNumber, String user, String password , File[] fileList) throws IOException {
        FileInputStream in = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            FTPSClient innerFtp = new FTPSClient(sslContext);
            try {
                boolean loginRes;
                innerFtp.connect(server, portNumber);
                if(!innerFtp.isConnected()){
                    return false;
                }
                innerFtp.setDataTimeout(4*60*1000);
                innerFtp.setSoTimeout(120*1000);
                innerFtp.execPBSZ(0);
                innerFtp.execPROT("P");
                innerFtp.enterLocalPassiveMode();
                loginRes = innerFtp.login(user, password);
                if(!loginRes)
                    return false;

                for(File file : fileList){
                    in = new FileInputStream(file);
                    innerFtp.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                    innerFtp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                    //innerFtp.enterLocalPassiveMode();
                    boolean result = innerFtp.storeFile(file.getName(),in);
                    if(result){
                        if(isFileUploadedCorrectly(file)){
                            file.delete();
                            log.info( file.getName() + " has been uploaded at " + Calendar.getInstance().getTime().toString());
                        }
                    }
                }

                return true;

            }catch (Exception ex){
                log.error( "FTPClientUtility() -->  uploadListOfFile():" + ex.getMessage());
                log.error(ex.toString());
                return false;
            }
            finally {
                if (innerFtp != null) {
                    innerFtp.logout();
                    innerFtp.disconnect();
                }
                if (in != null)
                    in.close();
            }
        }catch (Exception ex){
            log.error( "FTPClientUtility() -->  uploadListOfFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }


    public Boolean uploadListOfFile(String server, int portNumber, String user, String password , File[] fileList, boolean archive) throws IOException {
        FileInputStream in = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            FTPSClient innerFtp = new FTPSClient(sslContext);
            try {
                boolean loginRes;
                innerFtp.connect(server, portNumber);
                if(!innerFtp.isConnected()){
                    return false;
                }
                innerFtp.setDataTimeout(4*60*1000);
                innerFtp.setSoTimeout(120*1000);
                innerFtp.execPBSZ(0);
                innerFtp.execPROT("P");
                innerFtp.enterLocalPassiveMode();
                loginRes = innerFtp.login(user, password);
                if(!loginRes)
                    return false;

                File archiveFolder = new File(Constants.archiveFolder);
                for(File file : fileList){
                    in = new FileInputStream(file);
                    innerFtp.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                    innerFtp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                    //innerFtp.enterLocalPassiveMode();
                    boolean result = innerFtp.storeFile(file.getName(),in);
                    if(result){
                        if(isFileUploadedCorrectly(file)){
                            if(archive)
                                FileUtils.copyFileToDirectory(file, archiveFolder);
                            file.delete();
                            log.info( file.getName() + " has been uploaded at " + Calendar.getInstance().getTime().toString());
                        }
                    }
                }

                return true;

            }catch (Exception ex){
                log.error( "FTPClientUtility() -->  uploadListOfFile():" + ex.getMessage());
                log.error(ex.toString());
                return false;
            }
            finally {
                if (innerFtp != null) {
                    innerFtp.logout();
                    innerFtp.disconnect();
                }
                if (in != null)
                    in.close();
            }
        }catch (Exception ex){
            log.error( "FTPClientUtility() -->  uploadListOfFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }


    public Boolean uploadListOfShiftLogFile(String server, int portNumber, String user, String password , File[] fileList, boolean isAlarm) throws IOException {
        FileInputStream in = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            FTPSClient innerFtp = new FTPSClient(sslContext);
            try {
                boolean loginRes;
                innerFtp.connect(server, portNumber);
                if(!innerFtp.isConnected()){
                    return false;
                }
                innerFtp.setDataTimeout(4*60*1000);
                innerFtp.setSoTimeout(120*1000);
                innerFtp.execPBSZ(0);
                innerFtp.execPROT("P");
                innerFtp.enterLocalPassiveMode();
                loginRes = innerFtp.login(user, password);
                if(!loginRes)
                    return false;

                File archiveFolder = new File(Constants.archiveShiftLogFolder);
                for(File file : fileList){
                    in = new FileInputStream(file);
                    innerFtp.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                    innerFtp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                    //innerFtp.enterLocalPassiveMode();
                    boolean result = innerFtp.storeFile(file.getName(),in);
                    if(result){
                        if(isFileUploadedCorrectly(file)){
                            if(!isAlarm)
                                FileUtils.copyFileToDirectory(file, archiveFolder);
                            file.delete();
                            log.info( file.getName() + " has been uploaded at " + Calendar.getInstance().getTime().toString());
                        }
                    }
                }

                return true;

            }catch (Exception ex){
                log.error( "FTPClientUtility() -->  uploadListOfFile():" + ex.getMessage());
                log.error(ex.toString());
                return false;
            }
            finally {
                if (innerFtp != null) {
                    innerFtp.logout();
                    innerFtp.disconnect();
                }
                if (in != null)
                    in.close();
            }
        }catch (Exception ex){
            log.error( "FTPClientUtility() -->  uploadListOfFile():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public static Boolean isFileUploadedCorrectly(File file)
    {
        try {
            Map<String, String> paramsDictionary = new HashMap<String, String>();
            paramsDictionary.put(MessageProtocol.EquipmentIdAttribute, "" + StateHandler.getInstance().getBusId());
            paramsDictionary.put(MessageProtocol.EquipmentTypeAttribute, String.valueOf(Constants.EQUIPMENT_TYPE));
            paramsDictionary.put(MessageProtocol.FileNameAttribute, file.getName());
            paramsDictionary.put(MessageProtocol.FileCRCAttribute, Utility.calculateCRCForTransactionFiles(file));
            MessageProtocol command = new MessageProtocol(MessageProtocol.CRCCheckCommand, Calendar.getInstance().getTime(), paramsDictionary);
            String res = OnlineLocationSender.getInstance().sendInquiry(command);

            if (res != null && res.contains(MessageProtocol.CRCResultAttribute))
            {
                MessageProtocol mp = new MessageProtocol(res);
                if(mp.getParams().get(MessageProtocol.CRCResultAttribute).equals("1"))
                    return true;
            }
            return false;
        }catch (Exception ex){
            log.error("FTPClientUtility() -->  isFileUploadedCorrectly():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
