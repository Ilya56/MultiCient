package com.dia.multiclient.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.dia.multiclient.activities.MenuActivity;
import com.dia.multiclient.cores.CoreFragment;
import com.dia.multiclient.lastFm.LFMLoginButton;
import com.dia.multiclient.vk.VKErrorActivity;
import com.dia.multiclient.vk.VKLoginButton;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

/**
 * Created by Ilya on 25.09.2016.
 */
public class MainFragment extends Fragment {
    private CallbackManager callbackManager;
    private LFMLoginButton lfmLoginButton;

    private static final String[] myScope = new String[]{
            VKScope.FRIENDS,
            VKScope.GROUPS,
            VKScope.OFFLINE,
            VKScope.AUDIO,
            VKScope.DOCS,
            VKScope.MESSAGES,
            VKScope.NOHTTPS,
            VKScope.NOTIFICATIONS,
            VKScope.PAGES,
            VKScope.VIDEO,
            VKScope.WALL
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        //VK
        final VKLoginButton vkLogin = (VKLoginButton) view.findViewById(R.id.vk_log);
        vkLogin.setScope(myScope);

        //FB
        FacebookSdk.sdkInitialize(this.getActivity().getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.fb_login);
        loginButton.setReadPermissions("email, read_page_mailboxes, manage_pages, publish_actions, pages_messaging, user_posts, " +
                "user_friends, user_about_me, user_birthday, user_education_history, user_hometown, user_relationships, " +
                "user_status, publish_pages, user_managed_groups");
        loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                G.loginInFB = (loginResult.getAccessToken() != null);
                Log.d("qwe", "LOGIN");
            }

            @Override
            public void onCancel() {
                Log.d("qwe", "Cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("qwe", "Error");
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                G.loginInFB = (loginResult.getAccessToken() != null);
                Log.d("qwe", "LOGIN");
            }

            @Override
            public void onCancel() {
                Log.d("qwe", "Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("qwe", "Error");
            }
        });

        //system
        view.findViewById(R.id.next_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMenu();
            }
        });

        lfmLoginButton = (LFMLoginButton) view.findViewById(R.id.lfm_login);

        if (AccessToken.getCurrentAccessToken() != null) {
            G.loginInFB = true;
            Log.d("qwe", "LOGIN");
        }

        return view;
    }

    private void startError(String error) {
        startActivity(new Intent(getActivity(), VKErrorActivity.class).putExtra("from", "song list fragment: " + error));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //VK
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                G.loginInVK = res.accessToken != null;
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
                startError(error.toString());
            }
        };

        super.onActivityResult(requestCode, resultCode, data);
        //FB
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startMenu() {
        startActivity(new Intent(getActivity(), MenuActivity.class));
        getActivity().finish();
    }

    public LFMLoginButton getLfmLoginButton() {
        return lfmLoginButton;
    }
}
