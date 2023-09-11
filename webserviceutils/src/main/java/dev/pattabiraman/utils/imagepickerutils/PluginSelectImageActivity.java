/*
 * Created by Pattabi Raman on 03/05/23, 2:55 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 03/05/23, 2:55 PM
 */

package dev.pattabiraman.utils.imagepickerutils;

import static dev.pattabiraman.utils.PluginAppConstant.CROP_PIC_REQUEST_CODE;
import static dev.pattabiraman.utils.PluginAppConstant.OPEN_SINGLE_MEDIA_PICKER;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.android.volley.VolleyError;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dev.pattabiraman.utils.PluginAppConstant;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.callback.OnTaskCompleted;
import dev.pattabiraman.utils.model.SelectedImageModel;
import dev.pattabiraman.utils.permissionutils.GetPermissionResult;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;
import dev.pattabiraman.webserviceutils.R;

/**
 * @author Pattabi
 * @apiNote MUST DO BEFORE CALLING THIS ACTIVITY:<br/>
 * SEND below params in Intent putExtra:<br/><br/>
 * • intentObj.putExtra("requestCode", 101);  <br/><br/>
 * • intentObj.putExtra("clickTypeAutomate", Integer); <br/> -> refer  PluginAppConstant.CLICK_TYPE_CAMERA / PluginAppConstant.CLICK_TYPE_GALLERY / PluginAppConstant.CLICK_TYPE_CANCEL / PluginAppConstant.CLICK_TYPE_NONE  <br/><br/>
 * • intentObj.putExtra("cameraBtnText", String); <br/> -> camera button name <br/><br/>
 * • intentObj.putExtra("galleryBtnText", String); <br/> -> gallery button name <br/><br/>
 * • intentObj.putExtra("cancelBtnText", String); <br/> -> cancel button name <br/><br/>
 * • intentObj.putExtra("isToShowDrawableStart", Boolean); <br/> -> Whether to show drawables for all CTA buttons <br/><br/>
 * DECLARE activity in Manifest:<br/>
 * • dev.pattabiraman.utils.imagepickerutils.PluginSelectImageActivity <br/>
 **/
public class PluginSelectImageActivity extends PluginBaseAppCompatActivity {
    private AppCompatActivity activity;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    Uri fileUri;
    private String cameraBtnText, galleryBtnText, cancelBtnText;
    private boolean isToShowDrawableStart;
    private int clickTypeAutomate = PluginAppConstant.CLICK_TYPE_NONE;

    private final List<String> permissionsRequired = new ArrayList<>();
    private int requestCodeCallingClass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = PluginSelectImageActivity.this;
        requestCodeCallingClass = getIntent().getExtras().getInt("requestCode");
        cameraBtnText = getIntent().getExtras().getString("cameraBtnText");
        galleryBtnText = getIntent().getExtras().getString("galleryBtnText");
        cancelBtnText = getIntent().getExtras().getString("cancelBtnText");
        isToShowDrawableStart = getIntent().getExtras().getBoolean("isToShowDrawableStart");
        clickTypeAutomate = getIntent().getExtras().getInt("clickTypeAutomate");

        if (clickTypeAutomate == PluginAppConstant.CLICK_TYPE_NONE)
            showAlert();
        else {
            switch (clickTypeAutomate) {
                case PluginAppConstant.CLICK_TYPE_CAMERA:
                    checkForCameraPermission();
                    break;
                case PluginAppConstant.CLICK_TYPE_GALLERY:
                    checkForStorageAccessPermission();
                    break;
            }
        }
    }

    private void checkForStorageAccessPermission() {
        permissionsRequired.clear();
        permissionsRequired.add(android.Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
            @Override
            public void resultPermissionSuccess() {
                openGallery();
            }

            @Override
            public void resultPermissionRevoked() {
                activity.finish();


            }
        });
    }

    /**
     * The showAlert() function displays a bottom sheet dialog with options for camera, gallery, and
     * cancel, and performs corresponding actions when each option is clicked.
     */
    private void showAlert() {
        final BottomSheetDialog ab = new BottomSheetDialog(activity);
        ab.setContentView(R.layout.inflate_image_picker_dialog);
        final AppCompatTextView tvCamera = ab.findViewById(R.id.tvCamera);
        final AppCompatTextView tvGallery = ab.findViewById(R.id.tvGallery);
        final AppCompatTextView tvCancel = ab.findViewById(R.id.tvCancel);

        if (tvCamera != null) {
            /*customize name*/
            if (!TextUtils.isEmpty(cameraBtnText))
                tvCamera.setText(cameraBtnText);

            /*show/hide drawableStart*/
            if (!isToShowDrawableStart) {
                tvCamera.setCompoundDrawables(null, null, null, null);
            }
            tvCamera.setOnClickListener(v -> {
                ab.dismiss();
                checkForCameraPermission();
            });
        }

        if (tvGallery != null) {
            /*customise name*/
            if (!TextUtils.isEmpty(galleryBtnText))
                tvGallery.setText(galleryBtnText);
            /*show/hide drawableStart*/
            if (!isToShowDrawableStart) {
                tvGallery.setCompoundDrawables(null, null, null, null);
            }
            tvGallery.setOnClickListener(v -> {
                ab.dismiss();
                checkForStoragePermission(new OnTaskCompleted() {
                    @Override
                    public void onTaskSuccess(JSONObject jsonObject) {
                        openGallery();
                    }

                    @Override
                    public void onTaskFailure(VolleyError error) {

                    }
                });
            });
        }
        if (tvCancel != null) {
            ab.dismiss();
            /*customise name*/
            if (!TextUtils.isEmpty(cancelBtnText))
                tvCancel.setText(cancelBtnText);
            /*show/hide drawableStart*/
            if (!isToShowDrawableStart) {
                tvCancel.setCompoundDrawables(null, null, null, null);
            }
            tvCancel.setOnClickListener(v -> {
                ab.dismiss();
                activity.finish();
            });
        }
        ab.setCancelable(false);
        ab.show();

    }

    /**
     * The function checks for camera permission and if granted, opens the camera.
     */
    private void checkForCameraPermission() {
        permissionsRequired.clear();
        permissionsRequired.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkForStoragePermission(new OnTaskCompleted() {
            @Override
            public void onTaskSuccess(JSONObject jsonObject) {
                openCamera();
            }

            @Override
            public void onTaskFailure(VolleyError error) {

            }
        });
    }


    /**
     * The function checks for storage permission and calls the appropriate callback based on the
     * result.
     *
     * @param onTaskCompleted An interface that defines two methods: onTaskSuccess(JSONObject result)
     *                        and onTaskFailure(Exception error). These methods are called when the task is completed
     *                        successfully or when it fails, respectively.
     */
    private void checkForStoragePermission(final OnTaskCompleted onTaskCompleted) {
        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
            @Override
            public void resultPermissionSuccess() {
                try {
                    onTaskCompleted.onTaskSuccess(new JSONObject().put("success", true));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void resultPermissionRevoked() {
                onTaskCompleted.onTaskFailure(null);
            }
        });
    }

    /**
     * Open Native Camera<br/>
     * The function "openCamera" opens the camera app to capture an image and saves it with a title and
     * description.
     */
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * @apiNote refer PluginOPEN_SINGLE_MEDIA_PICKER / PluginOPEN_MULTIPLE_MEDIA_PICKER<br/>
     * The function "openGallery()" opens the gallery to select a single image.
     */
    private void openGallery() {
        /*Intent takePicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(takePicture, OPEN_SINGLE_MEDIA_PICKER);*/
        try {
            pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // The code is using the ActivityResultContracts.PickVisualMedia contract to register an
    // activity result launcher. This launcher is used to launch a photo picker activity and handle the
    // result.
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: " + uri);
            final SelectedImageModel imageModel = new SelectedImageModel();
            imageModel.setUriOfImage(uri);
            imageModel.setArrPath(uri.getPath());
            if (!TextUtils.isEmpty(uri.getPath())) {
                setDetailsOfImage(uri);
            } else {
                if (clickTypeAutomate != PluginAppConstant.CLICK_TYPE_NONE) {
                    activity.finish();
                }
            }

        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    setDetailsOfImage(fileUri);
//                    doCrop(fileUri);
                } else if (resultCode == RESULT_CANCELED) {
                    PluginAppUtils.getInstance(activity).showToast(activity, "You have cancelled image selection");
                    activity.finish();
                } else {
//                    AppHelperMethods.getInstance(activity).showToast(activity, "Please select an image");
                    if (clickTypeAutomate != PluginAppConstant.CLICK_TYPE_NONE) {
                        activity.finish();
                    }
                }
                break;
            case OPEN_SINGLE_MEDIA_PICKER:
                if (resultCode == RESULT_OK && data != null) {
                    final Uri uri = data.getData();
                    setDetailsOfImage(uri);
//                    doCrop(uri);
                } else {
//                    AppHelperMethods.getInstance(activity).showToast(activity, "Please select an image again");
                    if (clickTypeAutomate != PluginAppConstant.CLICK_TYPE_NONE) {
                        activity.finish();
                    }
                }
                break;
            case CROP_PIC_REQUEST_CODE:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = extras.getParcelable("data");
                    final Uri croppedImageUri = getImageUri(activity, bitmap);
//                    AppHelperMethods.getInstance(activity).showToast(activity, croppedImageUri.getPath());

                } else {
                    PluginAppUtils.getInstance(activity).showToast(activity, "Please select an image again");
                }
                break;
            default:
                if (data != null && data.getData() != null) {
                    final Uri uri = data.getData();
                    setDetailsOfImage(uri);
                }
                //activity.finish();

        }
    }

    /**
     * The function "doCrop" crops an image selected by the user using the Android camera app, with a
     * specified aspect ratio and output size.
     *
     * @param picUri The picUri parameter is the URI of the image that you want to crop. It is used to
     *               set the data and type of the cropIntent.
     */
    private void doCrop(final Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");

            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 600);
            cropIntent.putExtra("outputY", 600);
            cropIntent.putExtra("scale", "true");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(cropIntent, CROP_PIC_REQUEST_CODE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            PluginAppUtils.getInstance(activity).showToast(activity, errorMessage);
        }
    }

    /**
     * The function takes an AppCompatActivity and a Bitmap as input, compresses the Bitmap into a JPEG
     * format, inserts it into the MediaStore.Images.Media, and returns the Uri of the inserted image.
     *
     * @param inContext The inContext parameter is an instance of the AppCompatActivity class, which is
     *                  typically the current activity or context in which the method is being called. It is used to
     *                  access the content resolver and other resources of the activity.
     * @param inImage   The inImage parameter is a Bitmap object, which represents an image in memory.
     * @return The method is returning a Uri object.
     */
    public Uri getImageUri(final AppCompatActivity inContext, final Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    SelectedImageModel mSelectedImageModels = new SelectedImageModel();

    /**
     * The function sets various details of an image and finishes the activity.
     *
     * @param resultUri The resultUri parameter is a Uri object that represents the location of the
     *                  selected image.
     */
    private void setDetailsOfImage(final Uri resultUri) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        mSelectedImageModels.setDATE_TAKEN(PluginAppUtils.getInstance(activity).getDate(cal.getTimeInMillis(), PluginAppConstant.DATE_FORMAT));
        mSelectedImageModels.setPathOfSelectedImage(resultUri.getPath());
        mSelectedImageModels.setUriOfImage(resultUri);
        mSelectedImageModels.setArrPath(resultUri.getPath());
        mSelectedImageModels.setThumbnails(PluginAppUtils.getInstance(activity).getBitmapFromURI(activity, resultUri));
        PluginAppConstant.getInstance().selectedImageModel = mSelectedImageModels;

        setResult(RESULT_OK);
        finish();
    }

    /**
     * The function returns the current activity, and if it is null, it assigns the value of
     * PluginSelectImageActivity.this to it.
     *
     * @return The method is returning the activity object.
     */
    public Activity getActivity() {
        if (activity == null) {
            activity = PluginSelectImageActivity.this;
        }
        return activity;
    }
}
