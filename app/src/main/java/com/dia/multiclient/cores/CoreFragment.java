package com.dia.multiclient.cores;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Ilya on 25.07.2016.
 */
public class CoreFragment extends Fragment implements OnBackPressed {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((CoreActivity)getActivity()).setOnBackPressed(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onBack() {
        getActivity().finish();
    }
}
