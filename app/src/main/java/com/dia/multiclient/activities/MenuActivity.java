package com.dia.multiclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.dia.multiclient.audio.activities.AudioActivity;
import com.dia.multiclient.cores.CoreActivity;
import com.dia.multiclient.news.dialogs.FilterDialog;
import com.dia.multiclient.fragments.MenuFragment;
import com.dia.multiclient.R;
import com.dia.multiclient.friends.fragments.FriendsFragment;
import com.dia.multiclient.message.fragments.PreviewListFragment;
import com.dia.multiclient.news.fragments.NewsFragment;
import com.dia.multiclient.post.NewPostActivity;
import com.dia.multiclient.profile.fragments.ProfileTabsFragment;

/**
 * Created by Ilya on 10.09.2016.
 */
public class MenuActivity extends CoreActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private int which = 1;

    private static MenuFragment menuFragment;
    private ProfileTabsFragment ptf;
    private static FriendsFragment ff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);

        menuFragment = new MenuFragment();
        ptf = new ProfileTabsFragment();
        ff = new FriendsFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, menuFragment).commit();
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawerLayout.closeDrawers();
                int id = menuItem.getItemId();

                if (id == R.id.mess) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, menuFragment).commit();
                    which = 1;
                }
                if (id == R.id.audio) {
                    startAudio();
                    which = 3;
                }
                if (id == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, ptf).commit();
                    which = 0;
                }
                if (id == R.id.friends) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, ff).commit();
                    which = 2;
                }

                return false;
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.app_name, R.string.app_name);

        drawerLayout.setDrawerListener(drawerToggle);
        setSupportActionBar(toolbar);

        drawerToggle.syncState();
    }

    private void startAudio() {
        startActivity(new Intent(this, AudioActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().show();
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.exit_account :
                startMain();
                return true;
            case R.id.menu_reload :
                switch (which) {
                    case 1:
                        ((PreviewListFragment) MenuFragment.getTabs().get(1)).reload();
                        ((NewsFragment) MenuFragment.getTabs().get(0)).reload();

                        break;
                    case 0:
                        ptf.getTabs().get(0).reload();
                        ptf.getTabs().get(1).reload();
                        break;
                    case 2:
                        ff.reload();
                        break;
                }
                return true;
            case R.id.new_post :
                startNewPost();
                return true;
            case android.R.id.toggle :
                drawerLayout.openDrawer(drawerLayout);
                return true;
            case R.id.filter :
                switch (which) {
                    case 1:
                        new FilterDialog().show(getFragmentManager(), "fdn");
                        break;
                    case 2:
                        new com.dia.multiclient.friends.dialogs.FilterDialog().show(getFragmentManager(), "fdf");
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void startNewPost(){
        startActivity(new Intent(this, NewPostActivity.class));
    }

    public static FriendsFragment getFF() {
        return ff;
    }
}
