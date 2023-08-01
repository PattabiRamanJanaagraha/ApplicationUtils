package dev.pattabiraman.apputils;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.util.HashMap;

import dev.pattabiraman.apputils.databinding.ActivityMainBinding;
import dev.pattabiraman.apputils.databinding.InflateBottomsheetImagePickerDialogBinding;
import dev.pattabiraman.apputils.otpless.OTPlessVerification;
import dev.pattabiraman.utils.AppHelperMethods;
import dev.pattabiraman.utils.PluginAppConstant;
import dev.pattabiraman.utils.calendarutils.CalendarSinglaDateSelectActivity;
import dev.pattabiraman.utils.callback.OnResponseListener;
import dev.pattabiraman.utils.imagepickerutils.PluginSelectImageActivity;
import dev.pattabiraman.utils.locationutils.LocationSelectConfirmLocationOnMap;
import dev.pattabiraman.utils.model.DateRangeFilterModel;
import dev.pattabiraman.utils.model.SelectedImageModel;
import dev.pattabiraman.utils.webservice.PluginWebserviceHelper;
import dev.pattabiraman.webserviceutils.BuildConfig;

public class MainActivity extends AppCompatActivity {
    private AppCompatActivity activity;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = MainActivity.this;
        AppHelperMethods.getInstance(activity).setToTraceLog(true);

        binding.runWebService.setOnClickListener(V -> {
            /**
             * @apiNote set this method to true to trace log. if this method is not called, by default no traces are logged in logcat view*/
            runWebservice();
        });
        /**@apiNote first approach - show bottomsheet dialog from library referenced layout*/
        binding.pickImage.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, PluginSelectImageActivity.class).putExtra("requestCode", 101).putExtra("clickTypeAutomate", PluginAppConstant.CLICK_TYPE_NONE).putExtra("cameraBtnText", "Pick from Camera").putExtra("galleryBtnText", "Pick from Gallery").putExtra("cancelBtnText", "Cancel Selection").putExtra("isToShowDrawableStart", true), 101);
        });
        /*second approach - use own ui/layout then send flags to pick (automatic CTA) image*/
        binding.btnBottomSheet.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
            InflateBottomsheetImagePickerDialogBinding imagePickerDialogBinding = InflateBottomsheetImagePickerDialogBinding.inflate(getLayoutInflater());
            bottomSheetDialog.setContentView(imagePickerDialogBinding.getRoot());
            bottomSheetDialog.setCancelable(false);
            imagePickerDialogBinding.tvCamera.setOnClickListener(tvCameraView -> {
                bottomSheetDialog.dismiss();
                startActivityForResult(new Intent(activity, PluginSelectImageActivity.class).putExtra("requestCode", 101).putExtra("clickTypeAutomate", PluginAppConstant.CLICK_TYPE_CAMERA).putExtra("cameraBtnText", "Pick from Camera").putExtra("galleryBtnText", "Pick from Gallery").putExtra("cancelBtnText", "Cancel Selection").putExtra("isToShowDrawableStart", true), 101);

            });
            imagePickerDialogBinding.tvGallery.setOnClickListener(tvCameraView -> {
                bottomSheetDialog.dismiss();
                startActivityForResult(new Intent(activity, PluginSelectImageActivity.class).putExtra("requestCode", 101).putExtra("clickTypeAutomate", PluginAppConstant.CLICK_TYPE_GALLERY).putExtra("cameraBtnText", "Pick from Camera").putExtra("galleryBtnText", "Pick from Gallery").putExtra("cancelBtnText", "Cancel Selection").putExtra("isToShowDrawableStart", true), 101);
            });
            imagePickerDialogBinding.tvCancel.setOnClickListener(tvCameraView -> {
                bottomSheetDialog.dismiss();

            });
            bottomSheetDialog.show();
        });

        /*pick a location*/
        binding.btnLocationPicker.setOnClickListener(v -> {
            activity.startActivityForResult(new Intent(activity, LocationSelectConfirmLocationOnMap.class)
                            .putExtra("requestCode", 102)
                            .putExtra("MAP_API_KEY", BuildConfig.googleMapAutoSuggestionApiKey)
                            .putExtra("btnConfirmLocationText", "Confirm Location")
                            .putExtra("btnDetectLocationText", "Detect my location")
                            .putExtra("btnConfirmLocationTextColor", getColor(R.color.white))
                            .putExtra("searchStringText", "Search")
                    , 102);
        });

        /*pick a date*/
        binding.btnPickADate.setOnClickListener(v -> {
            startActivityForResult(new Intent(activity, CalendarSinglaDateSelectActivity.class).putExtra("requestCode", 103).putExtra("isToSelectTime", true).putExtra("dateSelectionMode",PluginAppConstant.DATE_SELECTION_MODE_PAST_ANY).putExtra("numberOfDays",7), 103);
        });

        /*otpless login */
        OTPlessVerification.OTPlessVerification(activity).setOTPPressCTA(binding.whatsappLogin);
        //binding.whatsappLogin.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 101:
                    final SelectedImageModel model = PluginAppConstant.getInstance().getSelectedImageModel();
                    binding.ivSelectedImage.setImageURI(model.getUriOfImage());
                    break;
                case 102:
                    final String location;
                    if (data != null) {
                        location = data.getExtras().getString("location");
                        final Double lat = data.getExtras().getDouble("lat");
                        final Double lon = data.getExtras().getDouble("lon");
                        binding.tvSelectedLocationDetails.setText("Location: " + location + "\n" + "lat: " + lat + "\nlon: " + lon);
                    }
                    break;
                case 103:
                    if (data != null) {
                        DateRangeFilterModel mSelectDateModel = (DateRangeFilterModel) data.getSerializableExtra("result");
                        binding.btnPickADate.setText(mSelectDateModel.getmDateString());
                    }
                    break;
            }

        }
    }

    private void runWebservice() {
        final String url = "https://www.ichangemycity.com/android/api/icmyc/v1/api.php?cityId=1&userId=390610&channel=icmyc-citizen-android";
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Accept", "application/json");
        PluginWebserviceHelper.getInstance().runWebService(activity, PluginWebserviceHelper.METHOD_GET, url, null, new OnResponseListener() {
            @Override
            public void OnResponseFailure(JSONObject response) {

            }

            @Override
            public void OnResponseSuccess(JSONObject response) {
                binding.tvResponse.setTextColor(Color.GREEN);
                binding.tvResponse.setText(response.toString());
            }
        }, false, requestHeaders);
    }
}