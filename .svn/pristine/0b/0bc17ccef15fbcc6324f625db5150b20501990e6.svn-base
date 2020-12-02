package com.ar.echoafcavlapplication.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ar.echoafcavlapplication.Enums.UiBeepType;
import com.ar.echoafcavlapplication.Enums.UiCommandTypeEnum;
import com.ar.echoafcavlapplication.MainActivity;
import com.ar.echoafcavlapplication.R;
import com.ar.echoafcavlapplication.Utils.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class FragmentLoginToMaintenance extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentLoginToMaintenance.class);
    private EditText username, password;
    TextView msgTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_maintenance_frag, parent, false);
        username = v.findViewById(R.id.username);
        password = v.findViewById(R.id.password);
        msgTextView = v.findViewById(R.id.msg);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }



    public void doLogin(MainActivity mainActivity){
        try{
            String givenUsername = username.getText().toString();
            String givenPassword = password.getText().toString();
            if (givenPassword == null || givenUsername == null) {
                showMsg("لطفا مقادیر را وارد نمایید");
                return;
            }
            if (givenPassword.equals("") || givenPassword.equals("")){
                showMsg("لطفا مقادیر را وارد نمایید");
                return;
            }
            if (!givenPassword.equals(Constants.DEFAULT_PASS) || !givenUsername.equals(Constants.DEFAULT_USERNAME)){
                showMsg("نام کاربری یا کلمه ی عبور اشتباه است");
                return;
            }
            mainActivity.changeFixedMessageUi(UiCommandTypeEnum.MAINTENANCE, null, UiBeepType.SUCCESS);
        }catch (Exception ex){
            log.error("FragmentLoginToMaintenance > doLogin():" + ex.getMessage());
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
