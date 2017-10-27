package com.dia.multiclient.profile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.cores.CoreFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 31.10.2016.
 */
public class ProfileTabsFragment extends CoreFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<ProfileFragment> tabs;
    private List<String> titles;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        tabs = new ArrayList<>();
        if (G.loginInVK)
            add(G.VK);
        if (G.loginInFB)
            add(G.FB);

        titles = new ArrayList<>();
        if (G.loginInVK)
            titles.add(getString(R.string.VK));
        if (G.loginInFB)
            titles.add(getString(R.string.facebook));

        viewPager.setAdapter(new TabAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        tabLayout.setBackgroundColor(getResources().getColor(R.color.backBlack));

        return view;
    }

    private class TabAdapter extends FragmentPagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabs.get(position);
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    private void add(int from) {
        ProfileFragment pf = new ProfileFragment();
        Bundle b = new Bundle();
        b.putInt(G.FROM, from);
        pf.setArguments(b);
        tabs.add(pf);
    }

    public List<ProfileFragment> getTabs() {
        return tabs;
    }
}
