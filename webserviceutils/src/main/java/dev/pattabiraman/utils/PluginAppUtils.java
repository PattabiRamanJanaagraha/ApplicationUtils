/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import dev.pattabiraman.utils.callback.OnButtonClick;
import dev.pattabiraman.utils.locationutils.GPSTracker;
import dev.pattabiraman.utils.model.SelectedImageModel;
import dev.pattabiraman.utils.webservice.LruBitmapCache;
import dev.pattabiraman.webserviceutils.R;
import dev.pattabiraman.webserviceutils.databinding.InflateBottomsheetContainerBinding;

public class PluginAppUtils {
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static PluginAppUtils mInstance;

    public static final String TAG = PluginAppUtils.class.getSimpleName();

    private static AppCompatActivity activity;

    /**
     * The function returns an instance of PluginAppUtils, creating a new instance if it doesn't
     * already exist.
     *
     * @param appCompaitActivity The parameter "appCompaitActivity" is of type AppCompatActivity and
     *                           represents the current activity in which the PluginAppUtils instance is being created.
     * @return The method is returning an instance of the PluginAppUtils class.
     */
    public static PluginAppUtils getInstance(AppCompatActivity appCompaitActivity) {
        activity = appCompaitActivity;
        return mInstance == null ? mInstance = new PluginAppUtils() : mInstance;
    }

    public void showToast(final AppCompatActivity activity, final String message) {
        final Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
     *            request to be added to the request queue. It can be any type of request, such as a
     *            StringRequest, JsonObjectRequest, or ImageRequest.
     * @param tag The "tag" parameter is a string that is used to identify the request. It can be used
     *            later to cancel or find the request in the request queue. If the "tag" parameter is empty or
     *            null, a default tag will be set.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setRetryPolicy(new DefaultRetryPolicy(
                PluginAppConstant.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    /**
     * The function adds a request to the request queue and assigns a tag to it.
     *
     * @param req The parameter "req" is an instance of the Request class, which represents a network
     *            request to be made. It can be of any type, as indicated by the generic type parameter <T>.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * The function cancels all pending requests with a specific tag in a request queue.
     *
     * @param tag The "tag" parameter is used to identify and cancel all pending requests that have
     *            been added to the request queue with the same tag. It can be any object that uniquely identifies
     *            a group of requests.
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * The function handles different types of Volley errors and displays appropriate error messages
     * based on the HTTP response code.
     *
     * @param act The parameter "act" is of type AppCompatActivity. It represents the current activity
     * in which the method is being called.
     * @param volleyError The `volleyError` parameter is an instance of the `VolleyError` class, which
     * is an error that occurred during a Volley network request. It contains information about the
     * error, such as the network response and any error messages.
     */
   /* public void handleVolleyError(final AppCompatActivity act,
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
                    PluginAppUtils.getInstance(activity).showToast(act,
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
                        PluginAppUtils.getInstance(activity)
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
                        PluginAppUtils.getInstance(activity)
                                .showToast(act, errors.toString());
                    }
                    break;
                case HTTPCodeModel.HTTP_BAD_REQUEST:
                case HTTPCodeModel.HTTP_UN_AUTHORIZED:
                    errors = new StringBuilder(responseObject.optString("message"));
                    PluginAppUtils.getInstance(activity).showToast(act, errors.toString());
                    break;
                case HTTPCodeModel.HTTP_UNAUTHENTICATED:
                    new PluginAppUtils().cancelPendingRequests(PluginAppUtils.TAG);
                    PluginAppUtils.getInstance(activity)
                            .showToast(act, "Session Expired, Login again");
                    break;
                case HTTPCodeModel.HTTP_SERVER_ERROR:
                   *//* AppHelperMethods.getInstance(activity).showToast(act,
                            responseObject.optString("message"));*//*
                    AppHelperMethods.getInstance(activity).traceLog("error", responseObject + "");
                    break;
                case HTTPCodeModel.HTTP_CONNECTION_TIME_OUT:
                case HTTPCodeModel.HTTP_TIME_OUT:
                case HTTPCodeModel.HTTP_UNKNOWN_ERROR:
                    PluginAppUtils.getInstance(activity).showToast(act,
                            "Poor internet connection");
                    break;
                default:
                    errors = new StringBuilder(responseObject.optString("message") + "");
                    AppHelperMethods.getInstance(activity).traceLog("error", responseObject + "");
                    PluginAppUtils.getInstance(activity).showToast(act, errors.toString());
                    break;
            }
        } else {
            if (volleyError.getMessage() != null) {
                PluginAppUtils.getInstance(activity)
                        .showToast(act, volleyError.getMessage());
            }
        }
    }*/

    private static Dialog progressDialog;


    /**
     * The function `showProgressDialog` displays or dismisses a progress dialog in an Android
     * activity.
     *
     * @param activity The activity parameter is the reference to the current AppCompatActivity where
     *                 the progress dialog will be shown.
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

    /**
     * The function showAlert displays an alert dialog with a title, message, and optional negative
     * button, and executes the appropriate callback function when the positive or negative button is
     * clicked.
     *
     * @param activity               The activity parameter is the reference to the current AppCompatActivity. It is
     *                               used to display the AlertDialog on the screen.
     * @param title                  The title of the alert dialog box. It is displayed at the top of the dialog box.
     * @param message                The message parameter is a string that represents the content of the alert
     *                               dialog. It is the main text that will be displayed to the user.
     * @param isToShowNegativeButton A boolean value indicating whether or not to show a negative
     *                               button in the alert dialog. If set to true, a "Cancel" button will be displayed. If set to
     *                               false, no negative button will be displayed.
     * @param onButtonClick          onButtonClick is an interface that defines two methods:
     *                               onPositiveButtonClicked and onNegativeButtonClicked. These methods are called when the positive
     *                               and negative buttons of the AlertDialog are clicked, respectively.
     */
    public void showAlert(final AppCompatActivity activity, final String title, final String message,
                          final boolean isToShowNegativeButton, final
                          OnButtonClick onButtonClick) {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab.setTitle(title);
        ab.setMessage(Html.fromHtml(message));
        ab.setPositiveButton("Ok",
                (dialogInterface, i) -> onButtonClick.onPositiveButtonClicked(dialogInterface));
        if (isToShowNegativeButton) {
            ab.setNegativeButton("Cancel",
                    (dialogInterface, i) -> onButtonClick.onNegativeButtonClicked(dialogInterface));
        }
        ab.show();
    }

    /**
     * The function takes a millisecond value and a date format string as input, and returns a
     * formatted date string.
     *
     * @param milliSeconds The milliSeconds parameter is the number of milliseconds since January 1,
     *                     1970, 00:00:00 GMT.
     * @param dateFormat   The dateFormat parameter is a string that specifies the format in which you
     *                     want the date to be displayed. It follows the pattern of the SimpleDateFormat class in Java. For
     *                     example, "dd/MM/yyyy" represents the date in the format of day/month/year.
     * @return The method is returning a formatted date string based on the provided milliSeconds and
     * dateFormat.
     */
    public String getDate(long milliSeconds, String dateFormat) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * The function retrieves a bitmap image from a given URI and compresses it into a JPEG format with
     * a quality of 80.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     *                 represents the current activity in which the method is being called.
     * @param imageURI The imageURI parameter is the URI (Uniform Resource Identifier) of the image
     *                 file that you want to retrieve as a Bitmap.
     * @return The method is returning a Bitmap object.
     */
    public Bitmap getBitmapFromURI(AppCompatActivity activity, final Uri imageURI) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imageURI);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * The function sets the latitude and longitude values using the GPS coordinates obtained from the
     * device, and returns true if successful, otherwise it prompts the user to enable GPS/network
     * settings and returns false.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     *                 represents the current activity in which this method is being called.
     * @return The method returns a boolean value. If the GPS location can be obtained successfully, it
     * returns true. If the GPS or network is not enabled, it returns false.
     */
    public boolean setLatitudeLongitude(AppCompatActivity activity) {
        GPSTracker gps = new GPSTracker(activity);
        // check if GPS enabled
        if (gps.canGetLocation()) {
            PluginAppConstant.latitude = gps.getLatitude();
            PluginAppConstant.longitude = gps.getLongitude();
            return true;
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
            return false;
        }
    }

    /**
     * The function hides the keyboard on an Android activity.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     *                 represents the current activity in which the keyboard needs to be hidden.
     * @param view     The view parameter is the view that currently has focus and is displaying the
     *                 keyboard.
     */
    public void hideKeyboard(AppCompatActivity activity, View view) {
        InputMethodManager imm = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * The function shows the keyboard for a specific view in an Android activity.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     * represents the current activity in which the keyboard needs to be shown.
     * @param v The view on which the keyboard should be shown.
     */
    public void showKeyboard(final AppCompatActivity activity, final View v) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);

    }

    /**
     * The function creates and displays a bottom sheet dialog in an Android app, with customizable
     * title, cancelable behavior, and height.
     *
     * @param activity                       The activity parameter is the reference to the AppCompatActivity that is
     *                                       currently being displayed. It is used to create the dialog and access the window manager.
     * @param titleText                      The text to be displayed as the title above the items in the bottom sheet.
     * @param isToSetDialogCancelable        The parameter "isToSetDialogCancelable" is a boolean value that
     *                                       determines whether the bottom sheet dialog can be canceled by clicking outside of it. If it is
     *                                       set to true, the dialog can be canceled by clicking outside. If it is set to false, the dialog
     *                                       cannot be canceled by clicking outside
     * @param isBottomSheetToFitScreenHeight A boolean value indicating whether the bottom sheet should
     *                                       fit the height of the screen or not. If set to true, the bottom sheet will occupy the entire
     *                                       height of the screen. If set to false, the height of the bottom sheet will be set to wrap its
     *                                       content.
     * @return The method is returning an instance of the `InflateBottomsheetContainerBinding` class.
     */
    public InflateBottomsheetContainerBinding setBottomSheetDialog(final AppCompatActivity activity, final String titleText, final boolean isToSetDialogCancelable, final boolean isBottomSheetToFitScreenHeight) {
        final Dialog builder = new Dialog(activity);
        final InflateBottomsheetContainerBinding inflateBottomsheetContainerBinding = InflateBottomsheetContainerBinding.inflate(LayoutInflater.from(activity));
        builder.setContentView(inflateBottomsheetContainerBinding.getRoot());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;


        inflateBottomsheetContainerBinding.ivClose.setOnClickListener(v -> {
            builder.dismiss();
        });
        /*title text to display above items*/
        inflateBottomsheetContainerBinding.tvTitle.setText(Html.fromHtml(titleText));

        /*setup and populate recyclerview*/
        setRecyclerViewLayoutManager(activity, inflateBottomsheetContainerBinding.recyclerViewContainer, RecyclerView.VERTICAL, true);
        inflateBottomsheetContainerBinding.nestedScrollViewParent.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> PluginAppUtils.getInstance(activity).hideKeyboard(activity, inflateBottomsheetContainerBinding.nestedScrollViewParent));
        inflateBottomsheetContainerBinding.ivClose.setOnClickListener(v -> {
            builder.dismiss();
        });

        builder.setCancelable(isToSetDialogCancelable);

        Window window = builder.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.dimAmount = 0.75f;
        wlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        if (isBottomSheetToFitScreenHeight) {
            wlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            wlp.height = ViewGroup.LayoutParams.WRAP_CONTENT;//ViewGroup.LayoutParams.MATCH_PARENT;
        }
        /*show dialog at bottom of screen*/
        window.setAttributes(wlp);
        builder.show();
        return inflateBottomsheetContainerBinding;
    }


    /**
     * This function sets the layout manager and adds item dividers to a RecyclerView in a given
     * orientation.
     *
     * @param activity                 An instance of the current activity or context where the RecyclerView is being
     *                                 used.
     * @param recyclerView             The RecyclerView object that needs to be set up with a layout manager and
     *                                 item divider decoration.
     * @param RECYCLERVIEW_ORIENTATION The orientation of the RecyclerView, which can be either
     *                                 LinearLayoutManager.VERTICAL or LinearLayoutManager.HORIZONTAL. This determines whether the
     *                                 items in the RecyclerView are displayed vertically or horizontally.
     * @param isToAddItemDivider       A boolean value that determines whether or not to add a divider
     *                                 between items in the RecyclerView. If set to true, a divider will be added. If set to false, no
     *                                 divider will be added.
     */
    public void setRecyclerViewLayoutManager(final AppCompatActivity activity, final RecyclerView recyclerView, final int RECYCLERVIEW_ORIENTATION, final boolean isToAddItemDivider) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity, RECYCLERVIEW_ORIENTATION, false);
        recyclerView.setLayoutManager(layoutManager);
        if (isToAddItemDivider)
            recyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
    }
}
