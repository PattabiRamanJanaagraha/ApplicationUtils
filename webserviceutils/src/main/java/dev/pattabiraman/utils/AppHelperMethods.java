/*
 * Created by Pattabi Raman on 11/05/23, 11:23 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 11/05/23, 11:23 AM
 */

package dev.pattabiraman.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import dev.pattabiraman.utils.callback.OnButtonClick;
import dev.pattabiraman.utils.callback.OnTaskCompleted;
import dev.pattabiraman.utils.model.HTTPCodeModel;
import dev.pattabiraman.utils.model.SelectedImageModel;
import dev.pattabiraman.utils.webservice.LruBitmapCache;
import dev.pattabiraman.utils.webservice.VolleyMultipartRequest;
import dev.pattabiraman.utils.webservice.VolleySingleton;
import dev.pattabiraman.webserviceutils.R;

public class AppHelperMethods {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppHelperMethods mInstance;
    private boolean isToTraceLog;
    public static final int MY_SOCKET_TIMEOUT_MS = 864000;
    public static final String TAG = AppHelperMethods.class.getSimpleName();

    private static AppCompatActivity activity;

    public static AppHelperMethods getInstance(AppCompatActivity appCompaitActivity) {
        activity = appCompaitActivity;
        return mInstance == null ? mInstance = new AppHelperMethods() : mInstance;
    }

    public void showToast(final AppCompatActivity activity, final String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(activity);
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                AppHelperMethods.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public void handleVolleyError(final AppCompatActivity act,
                                  final VolleyError volleyError) {
        NetworkResponse response = volleyError.networkResponse;
        JSONObject responseObject = new JSONObject();
        StringBuilder errors = new StringBuilder();
        try {
            responseObject = new JSONObject(new String(response.data));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException ex) {
            errors = new StringBuilder(Objects.requireNonNull(ex.getMessage()));
        }
        if (response != null) {
            switch (response.statusCode) {
                case HTTPCodeModel.HTTP_SUCCESS_OK:
                case HTTPCodeModel.HTTP_SUCCESS_OK_:
                    break;
                case HTTPCodeModel.HTTP_NOT_FOUND:
                    AppHelperMethods.getInstance(activity).showToast(act,
                            responseObject.optString("message"));
                    break;
                case HTTPCodeModel.HTTP_VALIDATION_ERROR:
                    try {
                        Iterator<String> iterator = responseObject.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            try {
                                if (key.equalsIgnoreCase("errors")) {
                                    JSONObject value = (responseObject.optJSONObject(key));
                                    Iterator<String> iterator1 = value != null ? value.keys() : null;
                                    while (iterator1 != null && iterator1.hasNext()) {
                                        String key1 = iterator1.next();
                                        errors.append(Objects.requireNonNull(value.optJSONArray(key1)).get(0)).append("\n");
                                    }
                                } else if (key.equalsIgnoreCase("data")) {
                                    Object dataObject = responseObject.get(key);
                                    if (dataObject instanceof JSONObject) {
                                        JSONObject value = (responseObject.optJSONObject(key));
                                        Iterator<String> iterator1 = value != null ? value.keys() : null;
                                        while (Objects.requireNonNull(iterator1).hasNext()) {
                                            String key1 = iterator1.next();
                                            errors.append(Objects.requireNonNull(value.optJSONArray(key1)).get(0)).append("\n");
                                        }
                                    } else if (dataObject instanceof JSONArray) {
                                        JSONArray value = responseObject.optJSONArray(key);
                                        if (value != null) {
                                            for (int i = 0; i < value.length(); i++) {
                                                errors.append(value.optJSONObject(i).optString("message")).append("\n");
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                errors = new StringBuilder("Error Parsing Failed!");
                            }
                        }
                        AppHelperMethods.getInstance(activity)
                                .showAlert(act, responseObject.optString("message"), String.valueOf(errors), false,
                                        new OnButtonClick() {
                                            @Override
                                            public void onPositiveButtonClicked(DialogInterface dialogInterface) {

                                            }

                                            @Override
                                            public void onNegativeButtonClicked(DialogInterface dialogInterface) {

                                            }
                                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case HTTPCodeModel.HTTP_TOO_MANY_ATTEMPTS:
                    errors = new StringBuilder(responseObject.optString("message"));
                    if (!TextUtils.isEmpty(errors) && errors.toString().trim().length() > 0) {
                        AppHelperMethods.getInstance(activity)
                                .showToast(act, errors.toString());
                    }
                    break;
                case HTTPCodeModel.HTTP_BAD_REQUEST:
                case HTTPCodeModel.HTTP_UN_AUTHORIZED:
                    errors = new StringBuilder(responseObject.optString("message"));
                    AppHelperMethods.getInstance(activity).showToast(act, errors.toString());
                    break;
                case HTTPCodeModel.HTTP_UNAUTHENTICATED:
                    new AppHelperMethods().cancelPendingRequests(AppHelperMethods.TAG);
                    AppHelperMethods.getInstance(activity)
                            .showToast(act, "Session Expired, Login again");
                    break;
                case HTTPCodeModel.HTTP_SERVER_ERROR:
                   /* AppHelperMethods.getInstance(activity).showToast(act,
                            responseObject.optString("message"));*/
                    AppHelperMethods.getInstance(activity).traceLog("error", responseObject + "");
                    break;
                case HTTPCodeModel.HTTP_CONNECTION_TIME_OUT:
                case HTTPCodeModel.HTTP_TIME_OUT:
                case HTTPCodeModel.HTTP_UNKNOWN_ERROR:
                    AppHelperMethods.getInstance(activity).showToast(act,
                            "Poor internet connection");
                    break;
                default:
                    errors = new StringBuilder(responseObject.optString("message") + "");
                    AppHelperMethods.getInstance(activity).traceLog("error", responseObject + "");
                    AppHelperMethods.getInstance(activity).showToast(act, errors.toString());
                    break;
            }
        } else {
            if (volleyError.getMessage() != null) {
                AppHelperMethods.getInstance(activity)
                        .showToast(act, volleyError.getMessage());
            }
        }
    }

    private static Dialog progressDialog;

    /**
     * @param activity AppCompatActivity object of calling class
     * @param isToShow boolean - whether to show progress dialog or not
     */
    public void showProgressDialog(final AppCompatActivity activity, final Boolean isToShow) {
        try {
            if (isToShow) {
                progressDialog = new Dialog(activity);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.inflate_view_loading);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

                progressDialog.show();
            } else {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(final AppCompatActivity activity, final String uploadImageURL, final Map<String, String> params, final boolean isToShowProgressDialog, final OnTaskCompleted onTaskCompleted, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog)
            showProgressDialog(activity, true);
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                uploadImageURL, response -> {
            if (isToShowProgressDialog)
                showProgressDialog(activity, false);

            String resultResponse = new String(response.data);
            try {
                onTaskCompleted.onTaskSuccess(new JSONObject(resultResponse));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }, error -> {
            showProgressDialog(activity, false);
            handleVolleyError(activity, error);
            onTaskCompleted.onTaskFailure(error);
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (!TextUtils.isEmpty(PluginAppConstant.getInstance().getSelectedImageModel().getPathOfSelectedImage())) {
                    // file name could found file base or direct access from real path
                    params.put("banner", new DataPart("image" + new Random().nextInt() + ".jpg", AppHelperMethods
                            .getFileDataFromDrawable(activity, PluginAppConstant.getInstance().getSelectedImageModel()),
                            "image/jpeg"));
                }
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                PluginAppConstant.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(activity).addToRequestQueue(multipartRequest);

    }

    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(AppCompatActivity context, int id) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, id);
            Bitmap bitmap = ((BitmapDrawable) Objects.requireNonNull(drawable)).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[1024];
    }

    /**
     * Turn SelectedImageModel drawable into byte array.
     *
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(final AppCompatActivity context, final SelectedImageModel path) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), path.getUriOfImage());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            bitmap.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public void showAlert(final AppCompatActivity activity, final String title, final String message,
                          final boolean isToShowNegativeButton, final
                          OnButtonClick onButtonClick) {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab.setTitle(title);
        ab.setMessage(message);
        ab.setPositiveButton("Ok",
                (dialogInterface, i) -> onButtonClick.onPositiveButtonClicked(dialogInterface));
        if (isToShowNegativeButton) {
            ab.setNegativeButton("Cancel",
                    (dialogInterface, i) -> onButtonClick.onNegativeButtonClicked(dialogInterface));
        }
        ab.show();
    }

    /**
     * @return boolean - is trace log enabled
     */
    public boolean isToTraceLog() {
        return isToTraceLog;
    }

    /**
     * @param toTraceLog boolean to denote whether log has to be traced or not
     * @apiNote Must call this method passing true to log the traces. By default the logs are not traced
     */
    public void setToTraceLog(boolean toTraceLog) {
        isToTraceLog = toTraceLog;
    }

    /**
     * @param key   String value of key which can be variable name / string value
     * @param value String value of key which can be variable value / string value
     */
    public void traceLog(final String key, final String value) {
        if (isToTraceLog())
            Log.e("TAG-->" + key, "VALUE-->" + value);
    }

}
