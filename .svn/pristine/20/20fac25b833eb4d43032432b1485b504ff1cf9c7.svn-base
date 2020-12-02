package com.ar.echoafcavlapplication.Fragments;

import android.app.Fragment;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ar.echoafcavlapplication.Communication.FTPSClientUtility;
import com.ar.echoafcavlapplication.Communication.OnlineLocationSender;
import com.ar.echoafcavlapplication.Data.LicenceFileHandler;
import com.ar.echoafcavlapplication.Data.StateHandler;
import com.ar.echoafcavlapplication.Enums.RegisterStatus;
import com.ar.echoafcavlapplication.Models.MessageProtocol;
import com.ar.echoafcavlapplication.R;
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
import java.util.Date;
import java.util.List;

public class FragmentRegister extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentRegister.class);

    private TextView devIdView, statusView;
    private Button sendReqBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register_frag, parent, false);
        sendReqBtn = v.findViewById(R.id.sendReqBtn);
        devIdView = v.findViewById(R.id.devIdView);
        statusView = v.findViewById(R.id.statusView);

        String s = LicenceFileHandler.getInstance().getIDFromOS();
        devIdView.setText(s);
        sendReqBtn.setText("ثبت درخواست");

        sendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RequestTask().execute();
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public class RequestTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.NOT_REGISTERED)){
                    //send reg request
                    sendRegisterRequest();
                }else if (LicenceFileHandler.getInstance().getDCStatus().equals(RegisterStatus.REGISTERED_NOT_CONFIRMED)){
                    //send reg check request
                    sendCheckRegisterRequest();
                }else {
                    //you already registered
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusView.setText("دستگاه قبلا ثبت شده است");
                        }
                    });
                }
                return null;
            } catch (Exception ex) {
                log.error("FragmentDownloadUpload > RequestTask > doInBackground():" + ex.getMessage());
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


    private void sendRegisterRequest() {
        try {
            MessageProtocol cmd = new MessageProtocol(MessageProtocol.SendRegisterCommand, new Date(), Utility.generateParamForRegisterCommand());
            String res = OnlineLocationSender.getInstance().sendInquiry(cmd);
            if (res != null) {
                MessageProtocol msg = new MessageProtocol(res);
                switch (msg.getType()) {
                    case MessageProtocol.AllReadyRegisteredCommand:
                        LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                        LicenceFileHandler.getInstance().setDCStatus(RegisterStatus.REGISTERED_CONFIRMED);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusView.setText("پاسخ سرور: دستگاه قبلا ثبت شده است");
                            }
                        });
                        break;
                    case MessageProtocol.WrongLicenceCommand:
                        LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                        LicenceFileHandler.getInstance().setDCStatus(RegisterStatus.NOT_REGISTERED);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusView.setText("پاسخ سرور: شناسه ی نا معتبر"+"\n"+"این شناسه با کد اتوبوس تنظیم شده روی دستگاه هم خوانی ندارد");
                            }
                        });
                        break;
                    case MessageProtocol.OKCommand:
                        LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                        LicenceFileHandler.getInstance().setDCStatus(RegisterStatus.REGISTERED_NOT_CONFIRMED);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusView.setText("پاسخ سرور: درخواست ثبت دستگاه انجام شد");
                            }
                        });
                        break;
                    default:
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusView.setText("دریافت خطا از سمت سرور");
                            }
                        });
                        break;
                }
            }else{
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setText("جوابی از سمت سرور دریافت نشد");
                    }
                });
            }
        } catch (Exception ex) {
            log.error("LogHandler --> sendRegisterRequest():" + ex.getMessage());
            log.error(ex.toString());
        }
    }


    private void sendCheckRegisterRequest() {
        try {
            MessageProtocol cmd = new MessageProtocol(MessageProtocol.SendRegisterStatusCommand, new Date(), Utility.generateParamForRegisterCommand());
            String res = OnlineLocationSender.getInstance().sendInquiry(cmd);
            if (res != null) {
                MessageProtocol msg = new MessageProtocol(res);
                switch (msg.getType()) {
                    case MessageProtocol.SendRegisterStatusCommand:
                        if (msg.getParams().containsKey(MessageProtocol.dcRegisterValue)) {
                            RegisterStatus registerStatus = RegisterStatus.registerStatusValueOf(Integer.valueOf(msg.getParams().get(MessageProtocol.dcRegisterValue)));
                            if (registerStatus.equals(RegisterStatus.NOT_REGISTERED)){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        statusView.setText("پاسخ سرور: دستگاه ثبت نشده است");
                                    }
                                });
                            }else if (registerStatus.equals(RegisterStatus.REGISTERED_NOT_CONFIRMED)){
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        statusView.setText("پاسخ سرور: دستگاه ثبت شده استو در صف انتظار برای تایید شدن است");
                                    }
                                });
                            }else{
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        statusView.setText("پاسخ سرور: دستگاه ثبت شده است");
                                    }
                                });
                            }
                            if (!registerStatus.equals(LicenceFileHandler.getInstance().getDCStatus())) {
                                LicenceFileHandler.getInstance().setDCStatus(registerStatus);
                                if (LicenceFileHandler.getInstance().getDCLic() != null) {
                                    if (!LicenceFileHandler.getInstance().getDCLic().equals(LicenceFileHandler.getInstance().getRunTimeLicence())) {
                                        LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                                    }
                                } else {
                                    LicenceFileHandler.getInstance().setDCLic(LicenceFileHandler.getInstance().getRunTimeLicence());
                                }
                            }
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            log.error("LogHandler --> sendCheckRegisterRequest():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

}

