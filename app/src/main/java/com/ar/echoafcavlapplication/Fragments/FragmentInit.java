package com.ar.echoafcavlapplication.Fragments;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.app.Fragment;

import com.ar.echoafcavlapplication.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentInit extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentInit.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.init_frag, parent, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }



}
