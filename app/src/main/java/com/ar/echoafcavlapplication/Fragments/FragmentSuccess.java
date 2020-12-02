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

public class FragmentSuccess extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentSuccess.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.success_frag, parent, false);
        TextView fareTxtView = (TextView) v.findViewById(R.id.fareTxtView);
        TextView balanceTxtView = (TextView) v.findViewById(R.id.balanceTxtView);
        fareTxtView.setText(getArguments().get("msg_1").toString());
        balanceTxtView.setText(getArguments().get("msg_2").toString());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

}
