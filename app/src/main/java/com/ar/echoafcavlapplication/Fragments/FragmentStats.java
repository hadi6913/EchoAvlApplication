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

public class FragmentStats extends Fragment {
    private static Logger log = LoggerFactory.getLogger(FragmentStats.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stats_frag, parent, false);
        TextView startDateView = v.findViewById(R.id.startDate);
        TextView endDateView = v.findViewById(R.id.endDate);
        TextView transactionCounterView = v.findViewById(R.id.transactionCounter);
        startDateView.setText(getArguments().get("msg_1").toString());
        endDateView.setText(getArguments().get("msg_2").toString());
        transactionCounterView.setText(getArguments().get("msg_3").toString());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    }

}

