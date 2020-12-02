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

public class FragmentOutOfService extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentOutOfService.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.outofservice_frag, parent, false);
        TextView fareTxtView = v.findViewById(R.id.bodyTxtView);
        fareTxtView.setText("کد: "+getArguments().get("msg_1").toString());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

}

