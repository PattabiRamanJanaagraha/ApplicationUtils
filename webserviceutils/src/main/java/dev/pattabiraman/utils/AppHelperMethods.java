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

 /**
  * The AppHelperMethods class is a utility class in Java that provides various helper methods for
  * handling network requests, displaying toast messages, showing progress dialogs, handling errors,
  * and logging traces.
  */
 public class AppHelperMethods {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppHelperMethods mInstance;
    private boolean isToTraceLog;
    public static final int MY_SOCKET_TIMEOUT_MS = 864000;
    public static final String TAG = AppHelperMethods.class.getSimpleName();

    private static AppCompatActivity activity;

    /**
     * The getInstance method returns an instance of the AppHelperMethods class, creating a new
     * instance if one does not already exist.
     *
     * @param appCompaitActivity The appCompaitActivity parameter is an instance of the
     * AppCompatActivity class, which is a base class for activities that use the support library
     * action bar features. It is used to reference the current activity in which the AppHelperMethods
     * instance is being created.
     * @return The method is returning an instance of the AppHelperMethods class.
     */
    public static AppHelperMethods getInstance(AppCompatActivity appCompaitActivity) {
        activity = appCompaitActivity;
        return mInstance == null ? mInstance = new AppHelperMethods() : mInstance;
    }

    /**
     * The function showToast displays a toast message with a given message on an activity.
     *
     * @param activity The activity parameter is the reference to the current activity in which the
     * toast message will be displayed. It is of type AppCompatActivity, which is a subclass of the
     * Android Activity class.
     * @param message The message parameter is a string that represents the text that will be displayed
     * in the toast message.
     */
    public void showToast(final AppCompatActivity activity, final String message) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
    }

    /**
     * The function returns the request queue, creating a new one if it doesn't exist.
     *
     * @return The method is returning the RequestQueue object.
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(activity);
        }

        return mRequestQueue;
    }

    /**
     * The function returns an instance of the ImageLoader class, creating it if it doesn't already
     * exist.
     *
     * @return The method is returning an instance of the ImageLoader class.
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    /**
     * The function adds a request to the request queue with a specified tag and sets a default retry
     * policy.
     *
     * @param req The req parameter is an instance of the Request class, which represents a network
     * request to be added to the request queue. It can be any type of request, such as a
     * StringRequest, JsonObjectRequest, or ImageRequest.
     * @param tag The "tag" parameter is a string that is used to identify the request. It can be used
     * later to cancel or find the request in the request queue. If the "tag" parameter is empty or
     * null, a default tag will be set.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                AppHelperMethods.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * The function adds a request to the request queue and assigns a tag to it.
     *
     * @param req The parameter "req" is an instance of the Request class, which represents a network
     * request to be made. It can be of any type, as indicated by the generic type parameter <T>.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * The function cancels all pending requests with a specific tag in a request queue.
     *
     * @param tag The "tag" parameter is used to identify and cancel all pending requests that have
     * been added to the request queue with the same tag. It can be any object that uniquely identifies
     * a group of requests.
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * The function `handleVolleyError` handles different types of Volley errors and displays
     * appropriate error messages based on the HTTP response code.
     *
     * @param act The parameter "act" is of type AppCompatActivity. It represents the current activity
     * in which the method is being called.
     * @param volleyError The `volleyError` parameter is an instance of the `VolleyError` class, which
     * is an error that occurred during a Volley network request. It contains information about the
     * error, such as the network response and any error messages.
     */
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
     * The function `showProgressDialog` displays or hides a progress dialog in an Android activity.
     *
     * @param activity The activity parameter is the reference to the current AppCompatActivity. It is
     * used to create the dialog and show it on the screen.
     * @param isToShow A boolean value indicating whether to show or hide the progress dialog.
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

    /**
     * The function `uploadImage` is a Java method that uploads an image file to a specified URL using
     * Volley library, and it includes options for showing a progress dialog, handling success and
     * failure callbacks, and setting request headers.
     *
     * @param activity The `activity` parameter is an instance of the `AppCompatActivity` class. It is
     * used to show the progress dialog and handle any UI-related tasks.
     * @param uploadImageURL The URL where the image will be uploaded.
     * @param params A map containing additional parameters to be sent along with the image upload
     * request.
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * uploading the image.
     * @param onTaskCompleted The `onTaskCompleted` parameter is an interface that defines two methods:
     * `onTaskSuccess` and `onTaskFailure`. These methods are called when the upload task is completed
     * successfully or encounters an error, respectively. You need to implement this interface and pass
     * an instance of it to the `upload
     * @param requestHeaders A HashMap containing the headers to be included in the request. The keys
     * represent the header names, and the values represent the header values.
     */
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
     * The function retrieves file data from a drawable resource in Android and returns it as a byte
     * array.
     *
     * @param context The context parameter is an instance of the AppCompatActivity class, which
     * represents the current activity context in an Android application. It is used to access
     * resources and perform various operations within the activity.
     * @param id The id parameter is the resource id of the drawable image that you want to convert to
     * byte array.
     * @return The method is returning a byte array.
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
     * The function takes an image URI, compresses it as a JPEG with 50% quality, and returns the byte
     * array of the compressed image data.
     *
     * @param context The context parameter is an instance of the AppCompatActivity class, which
     * represents the current activity or context in which the method is being called. It is used to
     * access resources and perform operations related to the activity.
     * @param path The "path" parameter is an instance of the "SelectedImageModel" class, which
     * contains information about the selected image. It likely includes the URI of the image, which is
     * used to retrieve the image data using the ContentResolver.
     * @return The method is returning a byte array, which represents the file data of the selected
     * image.
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

    /**
     * The function showAlert displays an alert dialog with a title, message, and optional negative
     * button, and executes the appropriate callback when the positive or negative button is clicked.
     *
     * @param activity The activity parameter is the reference to the current AppCompatActivity. It is
     * used to display the AlertDialog on the screen.
     * @param title The title of the alert dialog box. It is displayed at the top of the dialog box.
     * @param message The message parameter is a string that represents the content of the alert
     * dialog. It is the main text that will be displayed to the user.
     * @param isToShowNegativeButton A boolean value indicating whether or not to show a negative
     * button in the alert dialog. If set to true, a "Cancel" button will be displayed. If set to
     * false, no negative button will be displayed.
     * @param onButtonClick onButtonClick is an interface that defines two methods:
     * onPositiveButtonClicked and onNegativeButtonClicked. These methods are called when the positive
     * and negative buttons of the AlertDialog are clicked, respectively.
     */
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
     * The function returns a boolean value indicating whether to enable trace logging.
     *
     * @return The method is returning the value of the variable "isToTraceLog".
     */
    public boolean isToTraceLog() {
        return this.isToTraceLog;
    }

    /**
     * The function sets a boolean variable to determine whether to enable tracing logs.
     *
     * @param toTraceLog The "toTraceLog" parameter is a boolean value that determines whether or not
     * to enable tracing logs. If set to true, tracing logs will be enabled. If set to false, tracing
     * logs will be disabled.
     */
    public void setToTraceLog(boolean toTraceLog) {
        this.isToTraceLog = toTraceLog;
    }


    /**
     * The function "traceLog" logs the key-value pair if tracing is enabled.
     *
     * @param key The key parameter is a string that represents the key for the log entry. It is used
     * to identify the specific log entry in the log output.
     * @param value The "value" parameter is a string that represents the value associated with the
     * given key.
     */
    public void traceLog(final String key, final String value) {
        if (isToTraceLog())
            Log.e(TAG, "KEY-->" + key + "\nVALUE-->" + value);
    }

}
