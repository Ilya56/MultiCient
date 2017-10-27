package com.dia.multiclient.vk;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.methods.VKApiBase;

import java.util.Locale;

/**
 * Created by Ilya on 28.08.2016.
 */
public class VKCustom extends VKApiBase {
    String group;

    public VKCustom(String group) {
        this.group = group;
    }

    @Override
    protected String getMethodsGroup() {
        return group;
    }

    public VKRequest customRequest(String request, VKParameters params) {
        return prepareRequest(request, params);
    }

    public static VKRequest prepareRequestLPS(String methodName, VKParameters methodParameters) {
        return new VKRequest(String.format(Locale.US, "%s", methodName), methodParameters, null);
    }
}
