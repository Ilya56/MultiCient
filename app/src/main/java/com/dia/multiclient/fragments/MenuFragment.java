package com.dia.multiclient.fragments;

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
import com.dia.multiclient.message.fragments.PreviewListFragment;
import com.dia.multiclient.news.fragments.NewsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilya on 10.09.2016.
 */
public class MenuFragment extends CoreFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private static List<Fragment> tabs;
    private List<String> titles;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        tabs = new ArrayList<>();
        tabs.add(new NewsFragment());
        if (G.loginInVK)
            tabs.add(new PreviewListFragment());

        titles = new ArrayList<>();
        titles.add(getString(R.string.news));
        titles.add(getString(R.string.messages));

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

    public static List<Fragment> getTabs() {
        return tabs;
    }
}
