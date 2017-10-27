package com.dia.multiclient.vk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dia.multiclient.G;
import com.dia.multiclient.R;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

/**
 * Created by Ilya on 22.10.2016.
 */
public class VKLoginButton extends Button {
    private static String[] myScope = new String[]{};

    public VKLoginButton(Context context) {
        super(context);
        create();
    }

    public VKLoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        create();
    }

    public VKLoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create();
    }

    public void setScope(String[] scopes) {
        myScope = scopes;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @SuppressLint("NewApi")
    private void create() {
        setBackground(getResources().getDrawable(R.drawable.vk_button));
        setMaxWidth(70);
        setMaxHeight(70);
        setPadding(0, 0, 0, 0);
        setLayoutParams(new LinearLayout.LayoutParams(70, 70));

        final SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.file_name), Context.MODE_PRIVATE);
        G.loginInVK = sharedPref.getBoolean("vk", false);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (G.loginInVK) {
                    VKSdk.logout();
                    G.loginInVK = false;
                    setText(getResources().getText(R.string.log_in_vk));
                    sharedPref.edit().putBoolean("vk", false);
                    sharedPref.edit().apply();
                }
                else {
                    VKSdk.login((Activity) getContext(), myScope);
                    G.loginInVK = true;
                    setText(getResources().getText(R.string.log_out));
                    sharedPref.edit().putBoolean("vk", true);
                    sharedPref.edit().apply();
                }
            }
        });

        VKSdk.wakeUpSession(getContext(), new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                if (res == VKSdk.LoginState.LoggedIn) {
                    {
                        G.loginInVK = true;
                        setText(getResources().getText(R.string.log_out));
                        sharedPref.edit().putBoolean("vk", true);
                        sharedPref.edit().apply();
                    }
                } else {
                    G.loginInVK = false;
                    setText(getResources().getText(R.string.log_in_vk));
                    sharedPref.edit().putBoolean("vk", false);
                    sharedPref.edit().apply();
                }
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getContext(), "Can't connect to server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
