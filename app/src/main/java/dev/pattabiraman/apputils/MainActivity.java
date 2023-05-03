package dev.pattabiraman.apputils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.json.JSONObject;

import java.util.HashMap;

import dev.pattabiraman.webserviceutils.callback.OnResponseListener;
import dev.pattabiraman.webserviceutils.webservice.PluginWebserviceHelper;

public class MainActivity extends AppCompatActivity {
    private AppCompatActivity activity;
    private String url;
    private HashMap<String, String> requestParams = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PluginWebserviceHelper.getInstance().runWebService(activity, PluginWebserviceHelper.METHOD_GET, url, requestParams, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {

            }

            @Override
            public void OnResponseSuccess(JSONObject response) {

            }
        }, false, PluginWebserviceHelper.HEADER_TYPE_NORMAL);
    }
}