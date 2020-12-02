package com.ar.echoafcavlapplication.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ar.echoafcavlapplication.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FragmentShiftCloseConfirm extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentShiftCloseConfirm.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.shift_close_confirm_frag, parent, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }



}
