package com.dia.multiclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;

/**
 * Created by Ilya on 08.11.2016.
 */
public class ProgressFragment extends CoreFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }
}
