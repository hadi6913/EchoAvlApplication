package com.ar.echoafcavlapplication.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ar.echoafcavlapplication.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentOperator extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentOperator.class);
    private String adminCode = "";
    private TextView welcomeTxtView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.operator_frag, parent, false);
        adminCode = getArguments().get("msg_1").toString();
        welcomeTxtView = v.findViewById(R.id.welcomeTxtView);
        welcomeTxtView.setText("پشتیبان عزیز "+" خوش آمدید");
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }



}
