/*
 * Created by Pattabi Raman on 31/05/23, 12:10 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 31/05/23, 12:08 PM
 */

package dev.pattabiraman.apputils.otpless;

import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.otpless.views.OtplessManager;
import com.otpless.views.WhatsappLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.callback.OnButtonClick;
import dev.pattabiraman.utils.callback.OnResponseListener;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;
import dev.pattabiraman.utils.webservice.PluginWebserviceHelper;
import dev.pattabiraman.webserviceutils.BuildConfig;

public class OTPlessVerification extends PluginBaseAppCompatActivity {
    private static AppCompatActivity activity;
    private static OTPlessVerification mInstance;

    public OTPlessVerification() {

    }

    public static OTPlessVerification OTPlessVerification(AppCompatActivity activityObj) {
        OtplessManager.getInstance().init(activityObj);
        activity = activityObj;
        return mInstance == null ? mInstance = new OTPlessVerification() : mInstance;
    }

    public void setOTPPressCTA(final WhatsappLoginButton button) {
        button.setResultCallback((data) -> {
            if (data != null && data.getWaId() != null) {
                String waid = data.getWaId();
// Send the waId to your server and pass the waId in getUserDetail API to retrieve the users whatsapp mobile number and name.
// Handle the signup/signin process here
                JSONObject requestParams = new JSONObject();
                try {
                    requestParams.put("waId", waid);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                HashMap<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("clientId", BuildConfig.otplessClientId);
                requestHeaders.put("clientSecret", BuildConfig.otplessClientSecret);
                requestHeaders.put("Content-Type", "application/json");

                new PluginWebserviceHelper().doPostJSONRequest(activity, "https://janaagraha.authlink.me", requestParams, new OnResponseListener() {
                    @Override
                    public void OnResponseFailure(JSONObject response) {

                    }

                    @Override
                    public void OnResponseSuccess(JSONObject response) {
                        PluginAppUtils.getInstance(activity).showAlert(activity, "Response", response.toString(), false, new OnButtonClick() {
                            @Override
                            public void onPositiveButtonClicked(DialogInterface dialogInterface) {

                            }

                            @Override
                            public void onNegativeButtonClicked(DialogInterface dialogInterface) {

                            }
                        });
                    }
                }, true, requestHeaders);
            }

        });
    }

}
