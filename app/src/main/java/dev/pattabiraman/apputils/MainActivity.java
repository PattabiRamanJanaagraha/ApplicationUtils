package dev.pattabiraman.apputils;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;

import dev.pattabiraman.webserviceutils.PluginAppUtils;
import dev.pattabiraman.webserviceutils.callback.OnResponseListener;
import dev.pattabiraman.webserviceutils.webservice.PluginWebserviceHelper;

public class MainActivity extends AppCompatActivity {
    private AppCompatActivity activity;
    private String url;
    private HashMap<String, String> requestParams = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        PluginWebserviceHelper.getInstance().runWebService(activity, PluginWebserviceHelper.METHOD_GET, url, requestParams, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {

            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                PluginAppUtils.getInstance(activity).showToast(activity, response.toString());
            }
        }, false, PluginWebserviceHelper.HEADER_TYPE_NORMAL);
    }
}