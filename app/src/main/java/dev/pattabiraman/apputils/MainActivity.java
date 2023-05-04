package dev.pattabiraman.apputils;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;

import dev.pattabiraman.apputils.databinding.ActivityMainBinding;
import dev.pattabiraman.utils.PluginAppConstant;
import dev.pattabiraman.utils.callback.OnResponseListener;
import dev.pattabiraman.utils.imagepickerutils.PluginSelectImageActivity;
import dev.pattabiraman.utils.model.SelectedImageModel;
import dev.pattabiraman.utils.webservice.PluginWebserviceHelper;

public class MainActivity extends AppCompatActivity {
    private AppCompatActivity activity;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = MainActivity.this;

        binding.runWebService.setOnClickListener(V -> {
            runWebservice();
        });
        binding.pickImage.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, PluginSelectImageActivity.class).putExtra("requestCode", 101), 101);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final SelectedImageModel model = PluginAppConstant.getInstance().getSelectedImageModel();
            binding.ivSelectedImage.setImageURI(model.getUriOfImage());
        }
    }

    private void runWebservice() {
        final String url = "https://www.ichangemycity.com/android/api/icmyc/v1/api.php?cityId=1&userId=390610&channel=icmyc-citizen-android";
        HashMap<String, String> requestParams = new HashMap<>();

        PluginWebserviceHelper.getInstance().runWebService(activity, PluginWebserviceHelper.METHOD_GET, url, requestParams, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {

            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                binding.tvResponse.setText(response.toString());
            }
        }, false, PluginWebserviceHelper.HEADER_TYPE_NORMAL);
    }
}