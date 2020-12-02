package com.ar.echoafcavlapplication.Fragments;

import android.app.Fragment;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ar.echoafcavlapplication.Communication.FTPSClientUtility;
import com.ar.echoafcavlapplication.Data.DataFileUtility;
import com.ar.echoafcavlapplication.Data.ShiftDataFileUtility;
import com.ar.echoafcavlapplication.Data.ShiftStatusTemporaryFileManager;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Data.StuffActivityFileManager;
import com.ar.echoafcavlapplication.Data.SummaryReportFileHandler;
import com.ar.echoafcavlapplication.Enums.BrightnessType;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Services.OutOfServiceHandler;
import com.ar.echoafcavlapplication.Utils.Constants;
import com.ar.echoafcavlapplication.Utils.ParameterUtils;
import com.ar.echoafcavlapplication.Utils.Utility;
import com.thanosfisherman.wifiutils.WifiUtils;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiConnect.ConnectionSuccessListener;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionErrorCode;
import com.thanosfisherman.wifiutils.wifiDisconnect.DisconnectionSuccessListener;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import io.realm.internal.Util;

public class FragmentDownloadUpload extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentDownloadUpload.class);

    private Button connectTryAgainBtn, uploadBtn, downloadBtn, returnBtn;
    private TextView connectStatusTextView, status;
    private EditText newServerIp;
    private LinearLayout mainOpLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.upload_download_frag, parent, false);
        connectStatusTextView = v.findViewById(R.id.connectStatusView);
        connectTryAgainBtn = v.findViewById(R.id.tryAgainConnectBtnView);
        newServerIp = v.findViewById(R.id.newServerIpVal);
        mainOpLayout = v.findViewById(R.id.mainOpLayout);
        uploadBtn = v.findViewById(R.id.uploadBtn);
        downloadBtn = v.findViewById(R.id.downloadBtn);
        status = v.findViewById(R.id.opStatus);
        returnBtn = v.findViewById(R.id.returnBtn);

        newServerIp.setText("afc.qom.ir");


        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadTask().execute();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UploadTask().execute();
            }
        });

        connectTryAgainBtn.setEnabled(false);
        connectTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkEnvironment();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        enableWifi();
        checkEnvironment();
    }

    public void checkEnvironment() {
        try {
            connectTryAgainBtn.setEnabled(false);
            connectStatusTextView.setText("تلاش برای جستجو شبکه ...");
            scanForNetwork();
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> checkEnvironment():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (WifiUtils.withContext(Utility.getContext()).isWifiConnected()) {
                //lets disconnect first
                WifiUtils.withContext(Utility.getContext()).disconnect(new DisconnectionSuccessListener() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void failed(@NonNull DisconnectionErrorCode errorCode) {

                    }
                });
            }
            disableWifi();
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> onDestroy():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void enableWifi() {
        try {
            WifiUtils.withContext(Utility.getContext()).enableWifi();
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> enableWifi():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    private void disableWifi() {
        try {
            WifiUtils.withContext(Utility.getContext()).disableWifi();
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> disableWifi():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void scanForNetwork() {
        try {
            WifiUtils.withContext(Utility.getContext()).scanWifi(this::getScanResults).start();
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> scanForNetwork():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }

    private void getScanResults(@NonNull final List<ScanResult> results) {
        try {
            if (results.isEmpty()) {
                return;
            }
            for (ScanResult result : results) {
                if (result.SSID.equals(Constants.wifiName)) {
                    // TODO: 8/2/2020 show is ok connect
                    connectStatusTextView.setText("شبکه با موفقیت پیدا شد، تلاش برای اتصال ...");
                    if (WifiUtils.withContext(Utility.getContext()).isWifiConnected(Constants.wifiName)){
                        //already connected to ssid
                        connectStatusTextView.setText("ادامه اتصال به شبکه");
                        connectTryAgainBtn.setEnabled(true);
                        mainOpLayout.setVisibility(View.VISIBLE);
                        uploadBtn.setEnabled(true);
                        downloadBtn.setEnabled(true);
                        connectTryAgainBtn.setEnabled(true);
                        return;
                    }else if (WifiUtils.withContext(Utility.getContext()).isWifiConnected()){
                        //connected to somewhere else
                        WifiUtils.withContext(Utility.getContext()).disconnect(new DisconnectionSuccessListener() {
                            @Override
                            public void success() {
                                connectStatusTextView.setText("قطع ارتباط از شبکه نا آشنا");
                                WifiUtils.withContext(Utility.getContext())
                                        .connectWith(Constants.wifiName, Constants.wifiPass)
                                        .setTimeout(40000)
                                        .onConnectionResult(new ConnectionSuccessListener() {
                                            @Override
                                            public void success() {
                                                connectStatusTextView.setText("اتصال موفق به شبکه");
                                                connectTryAgainBtn.setEnabled(true);
                                                mainOpLayout.setVisibility(View.VISIBLE);
                                                uploadBtn.setEnabled(true);
                                                downloadBtn.setEnabled(true);
                                            }

                                            @Override
                                            public void failed(@NonNull ConnectionErrorCode errorCode) {
                                                connectStatusTextView.setText("خطا در اتصال به شبکه");
                                                connectTryAgainBtn.setEnabled(true);
                                                uploadBtn.setEnabled(false);
                                                downloadBtn.setEnabled(false);
                                                mainOpLayout.setVisibility(View.GONE);
                                            }
                                        })
                                        .start();
                            }

                            @Override
                            public void failed(@NonNull DisconnectionErrorCode errorCode) {
                                connectStatusTextView.setText("خطا در قطع ارتباط از شبکه نا آشنا");
                            }
                        });
                        connectTryAgainBtn.setEnabled(true);
                        return;
                    }else {
                        WifiUtils.withContext(Utility.getContext())
                                .connectWith(Constants.wifiName, Constants.wifiPass)
                                .setTimeout(40000)
                                .onConnectionResult(new ConnectionSuccessListener() {
                                    @Override
                                    public void success() {
                                        connectStatusTextView.setText("اتصال موفق به شبکه");
                                        connectTryAgainBtn.setEnabled(true);
                                        mainOpLayout.setVisibility(View.VISIBLE);
                                        uploadBtn.setEnabled(true);
                                        downloadBtn.setEnabled(true);
                                    }

                                    @Override
                                    public void failed(@NonNull ConnectionErrorCode errorCode) {
                                        connectStatusTextView.setText("خطا در اتصال به شبکه");
                                        connectTryAgainBtn.setEnabled(true);
                                        uploadBtn.setEnabled(false);
                                        downloadBtn.setEnabled(false);
                                        mainOpLayout.setVisibility(View.GONE);
                                    }
                                })
                                .start();
                        connectTryAgainBtn.setEnabled(true);
                        return;
                    }
                }
            }
            // TODO: 8/2/2020 show there is no network
            connectStatusTextView.setText("شبکه مورد نظر یافت نشد");
            connectTryAgainBtn.setEnabled(true);
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> getScanResults():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }


    public void startDownloadOp() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadBtn.setEnabled(false);
                    downloadBtn.setEnabled(false);
                }
            });
            checkForParameterUpdate(newServerIp.getText().toString());
            Thread.sleep(1000);
            checkForOperatorsUpdate(newServerIp.getText().toString());
            Thread.sleep(1000);
            checkSoftUpdate(newServerIp.getText().toString());
            Thread.sleep(1000);
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> startDownloadOp():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        } finally {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadBtn.setEnabled(true);
                    downloadBtn.setEnabled(true);
                    status.setText("---");
                }
            });
        }
    }

    public class UploadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                startUploadOp();
                return null;
            } catch (Exception ex) {
                log.error("FragmentDownloadUpload > UploadTask > doInBackground():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


    public class DownloadTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                startDownloadOp();
                return null;
            } catch (Exception ex) {
                log.error("FragmentDownloadUpload > DownloadTask > doInBackground():" + ex.getMessage());
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                log.error(errors.toString());
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }


    public void startUploadOp() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    log.error("mosmo: "+ newServerIp.getText().toString());
                    status.setText("آپلود فایل های تراکنش");
                    uploadBtn.setEnabled(false);
                    downloadBtn.setEnabled(false);
                }
            });
            Utility.uploadClosedFile(newServerIp.getText().toString());
            Thread.sleep(1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("آپلود فایل های شیفت");
                }
            });
            Utility.uploadClosedShiftLogFile(newServerIp.getText().toString());
            Thread.sleep(1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("آپلود فایل های وضعیت");
                }
            });
            Utility.uploadStuffActivityFiles(newServerIp.getText().toString());
            Thread.sleep(1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("آپلود فایل های موقت");
                }
            });
            Utility.uploadTempShiftStatusFiles(newServerIp.getText().toString());
            Thread.sleep(1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("آپلود تصاویر");
                }
            });
            Utility.uploadDrivesImagesFile(newServerIp.getText().toString());
            Thread.sleep(1000);
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> startUploadOp():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        } finally {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uploadBtn.setEnabled(true);
                    downloadBtn.setEnabled(true);
                    status.setText("---");
                }
            });
        }
    }




















    public boolean checkForParameterUpdate(String server){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("بروز رسانی پارامتر");
                }
            });

            FileUtils.cleanDirectory(new File(Constants.tempFolder));
            boolean state = false;
            boolean downloadState = false;
            boolean updateState = false;
            state = FTPSClientUtility.getInstance().connectAndLoginToServer(server,
                    Constants.FTP_PORT_NUMBER,
                    StateHandler.getInstance().getSoftFtpUser(),
                    StateHandler.getInstance().getSoftFtpPass());
            if(state) {
                downloadState = FTPSClientUtility.getInstance().downloadFile("PARAM/bus_version.sis", Constants.tempFolder + "version.sis");
                if(downloadState){
                    ParameterUtils.getInstance().checkParameterVersion(new File(Constants.tempFolder + "version.sis"));
                    boolean conditionOne = ParameterUtils.getInstance().getNextParamVersion().equals("") && (!ParameterUtils.getInstance().getNextParamVersionTemp().equals(ParameterUtils.getInstance().getCurrentParamVersion()));
                    boolean conditionTwo = (!ParameterUtils.getInstance().getNextParamVersion().equals("")) && (!ParameterUtils.getInstance().getCurrentParamVersionTemp().equals(ParameterUtils.getInstance().getCurrentParamVersion())
                            || !ParameterUtils.getInstance().getNextParamVersionTemp().equals(ParameterUtils.getInstance().getNextParamVersion())
                            || !ParameterUtils.getInstance().getLoadDateTemp().equals(ParameterUtils.getInstance().getLoadDate()));
                    boolean conditionThree = (ParameterUtils.getInstance().getCurrentParamVersion().equals("") && ParameterUtils.getInstance().getNextParamVersion().equals("") && ParameterUtils.getInstance().getCurrentParamVersionTemp().equals("") && !ParameterUtils.getInstance().getNextParamVersionTemp().equals(""));
                    if(conditionOne || conditionTwo || conditionThree){
                        //new param version found, downloading new version param.
                        FileUtils.cleanDirectory(new File(Constants.uploadFolder));
                        downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.valid_code_param_server_address, Constants.uploadFolder + "valid_param.code");
                        if (downloadState) {
                            //lets check which device is able to get the update
                            if (ParameterUtils.getInstance().isOurCodeInDownloadList(new File(Constants.uploadFolder + "valid_param.code"))) {
                                //we should get the update
                                updateState = ParameterUtils.getInstance().downloadParameterUpdate();
                                return updateState;
                            }
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("بروز رسانی جدید برای پارامتر یافت نشد");
                        }
                    });

                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("خطا در دریافت ورژن نسخه جدید پارامتر");
                        }
                    });

                    log.error( "FragmentDownloadUpload --> checkForParameterUpdate(): Can not download Version.sis form server.");
                }
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("خطا در ارتباط با سرور");
                    }
                });

                log.error( "FragmentDownloadUpload --> checkForParameterUpdate(): FTP Can not connect to server.");
            }
            return updateState;
        }catch (Exception ex){
            log.error("FragmentDownloadUpload --> checkForParameterUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }





    public boolean checkForOperatorsUpdate(String server){
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("بروز رسانی اپراتور");
                }
            });

            FileUtils.cleanDirectory(new File(Constants.tempFolder));
            boolean state = false;
            boolean downloadState = false;
            boolean updateState = false;
            state = FTPSClientUtility.getInstance().connectAndLoginToServer(server,
                    Constants.FTP_PORT_NUMBER,
                    StateHandler.getInstance().getSoftFtpUser(),
                    StateHandler.getInstance().getSoftFtpPass());
            if(state) {
                downloadState = FTPSClientUtility.getInstance().downloadFile("PARAM/operatorversion.sis", Constants.tempFolder + "operatorversion.sis");
                if(downloadState){
                    ParameterUtils.getInstance().checkOperatorsVersion(new File(Constants.tempFolder + "operatorversion.sis"));
                    boolean condition = false;
                    if(!ParameterUtils.getInstance().getCurrentOperatorsVersion().equals("")){
                        Long currentV = Long.parseLong(ParameterUtils.getInstance().getCurrentOperatorsVersion());
                        Long newV = Long.parseLong(ParameterUtils.getInstance().getCurrentOperatorsVersionTemp());
                        if(newV > currentV)
                            condition = true;
                    }else{
                        condition = true;
                    }

                    if(condition){
                        //new operator version found, downloading new version operator.
                        updateState = ParameterUtils.getInstance().downloadOperatorsUpdate();
                        return updateState;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("بروز رسانی جدید برای اپراتور یافت نشد");
                        }
                    });


                }else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("خطا در دریافت ورژن نسخه جدید اپراتور");
                        }
                    });

                    log.error( "FragmentDownloadUpload --> checkForOperatorsUpdate(): Can not download Version.sis form server.");
                }
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("خطا در ارتباط با سرور");
                    }
                });

                log.error( "FragmentDownloadUpload --> checkForOperatorsUpdate(): FTP Can not connect to server.");
            }
            return updateState;
        }catch (Exception ex){
            log.error("FragmentDownloadUpload --> checkForOperatorsUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
            return false;
        }
    }




    public void checkSoftUpdate(String server) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    status.setText("بروز رسانی نرم افزار");
                }
            });

            boolean connectState = false;
            boolean downloadState = false;
            File tempFolder = new File(Constants.tempFolder);
            if (!tempFolder.isDirectory())
                tempFolder.mkdirs();
            else
                FileUtils.cleanDirectory(tempFolder);
            //----------- trying to download version.sis
            connectState = FTPSClientUtility.getInstance().connectAndLoginToServer(server,
                    Constants.FTP_PORT_NUMBER,
                    StateHandler.getInstance().getSoftFtpUser(),
                    StateHandler.getInstance().getSoftFtpPass());
            if (connectState) {
                downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.new_application_version_server_address, Constants.tempFolder + "version_ag.sis");
                if (downloadState) {
                    String newVersion = ParameterUtils.getInstance().getSoftVersionData(Constants.tempFolder + "version_ag.sis");
                    String currentSoftVersion = String.valueOf(Utility.getAppVersionCode());
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
                                if (ParameterUtils.getInstance().isOurCodeInDownloadList(new File(Constants.uploadFolder + "valid.code"))) {
                                    //we should get the update
                                    Integer countTwo = 0;
                                    while (countTwo < 3) {
                                        downloadState = FTPSClientUtility.getInstance().downloadFile(Constants.new_application_server_address, Constants.uploadFolder + "app.zip");
                                        if (downloadState) {
                                            count = 5;
                                            // new app package has been downloaded.
                                            ParameterUtils.getInstance().handleNewAppAfterDownload();
                                        }
                                        countTwo++;
                                    }
                                }
                            }
                            count++;
                        }
                    } else {
                        //no new update for app.
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status.setText("بروز رسانی جدید برای نرم افزار یافت نشد");
                            }
                        });

                        return;
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText("خطا در دریافت ورژن نسخه جدید نرم افزار");
                        }
                    });

                    log.error("FragmentDownloadUpload --> checkSoftUpdate(): Can not download new application form server.");
                    return;
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("خطا در ارتباط با سرور");
                    }
                });

                log.error("FragmentDownloadUpload --> checkSoftUpdate(): FTP Can not connect to server.");
                return;
            }
        } catch (Exception ex) {
            log.error("FragmentDownloadUpload --> checkSoftUpdate():" + ex.getMessage());
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            log.error(errors.toString());
        }
    }
}

