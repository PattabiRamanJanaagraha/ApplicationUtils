/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.webservice;

import static dev.pattabiraman.utils.PluginAppConstant.MY_SOCKET_TIMEOUT_MS;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import dev.pattabiraman.utils.AppHelperMethods;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.callback.OnResponseListener;
import dev.pattabiraman.utils.model.HTTPCodeModel;


 public class PluginWebserviceHelper {
    private static PluginWebserviceHelper mInstance;

    public static PluginWebserviceHelper getInstance() {
        return mInstance == null ? mInstance = new PluginWebserviceHelper() : mInstance;
    }

    public PluginWebserviceHelper() {
    }

    public static final int METHOD_GET = 1;
    public static final int METHOD_POST = 2;
    public static final int METHOD_PUT = 3;
    public static final int METHOD_DELETE = 4;
    public static final int METHOD_PATCH = 5;

    public static final int METHOD_POST_AS_JSON = 6;

    public static final String TAG = PluginWebserviceHelper.class.getSimpleName();


    /**
     * The function `runWebService` is used to make HTTP requests (GET, POST, PUT, PATCH, DELETE) to a
     * specified URL with optional parameters, headers, and progress dialog.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It is used
     * to access the current activity context and perform UI-related operations if needed.
     * @param methodType The methodType parameter is an integer that represents the type of HTTP method
     * to be used in the web service request. It can have the following values:
     * @param url The URL of the web service that you want to call.
     * @param params A HashMap containing the parameters to be sent in the request. The keys represent
     * the parameter names, and the values represent the parameter values.
     * @param onResponseListener The `onResponseListener` is an interface that allows you to handle the
     * response from the web service. It typically includes methods such as `onSuccess` and `onFailure`
     * to handle the success and failure scenarios respectively. You can implement this interface and
     * pass it as a parameter to the `run
     * @param isToShowProgressDialog A boolean value indicating whether or not to show a progress
     * dialog while the web service request is being processed.
     * @param requestHeaders A HashMap containing the headers to be included in the HTTP request. The
     * keys represent the header names, and the values represent the header values.
     */
    public void runWebService(final AppCompatActivity activity, final int methodType, final String url, HashMap<String, String> params, OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        AppHelperMethods.getInstance(activity).traceLog("requestURL", url);
        if (params != null)
            AppHelperMethods.getInstance(activity).traceLog("requestParams", params.toString());
        switch (methodType) {
            case METHOD_GET:
                doGet(activity, url, onResponseListener, isToShowProgressDialog, requestHeaders);
                break;
            case METHOD_POST:

                doPost(activity, url, params, onResponseListener, isToShowProgressDialog, requestHeaders);
                break;
            case METHOD_PUT:

                doPut(activity, url, params, onResponseListener, isToShowProgressDialog, requestHeaders);
                break;
            case METHOD_PATCH:
                doPatch(activity, url, onResponseListener, isToShowProgressDialog, requestHeaders);
                break;
            case METHOD_DELETE:

                doDelete(activity, url, params, onResponseListener, isToShowProgressDialog, requestHeaders);
                break;
            default:
                break;
        }
    }

    /**
     * The function `doPatch` sends a PATCH request to a specified URL with optional request headers,
     * handles the response, and provides callbacks for success and failure.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It is used
     * to access the current activity context and perform UI-related operations.
     * @param url The URL to send the PATCH request to.
     * @param onResponseListener The `onResponseListener` is an interface that defines two methods:
     * `OnResponseSuccess` and `OnResponseFailure`. These methods are called when the response from the
     * server is successful or when there is a failure, respectively. The `OnResponseSuccess` method is
     * called with the response as a
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * the request is being processed.
     * @param requestHeaders A HashMap containing the request headers to be included in the PATCH
     * request.
     */
    private static void doPatch(final AppCompatActivity activity, final String url, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PATCH, url, null, response -> {
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            AppHelperMethods.getInstance(activity).traceLog("URL : ", url);
            AppHelperMethods.getInstance(activity).traceLog("Response : ", response + "");
            if (response.has("httpCode")) {
                if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                    try {
                        onResponseListener.OnResponseSuccess(response);
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onResponseListener.OnResponseFailure(response);
                        PluginAppUtils.getInstance(activity).showToast(activity, e.getMessage());
                    }
                } else if (response.optInt("httpCode") == HTTPCodeModel.HTTP_UNAUTHENTICATED) {
                    PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                    try {
                        onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", response.optInt("httpCode")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PluginAppUtils.getInstance(activity).cancelPendingRequests(PluginAppUtils.TAG);
                } else if (response.optInt("httpCode") == 404) {

                    PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));

                    onResponseListener.OnResponseFailure(response);
                } else {
                    try {
                        onResponseListener.OnResponseFailure(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        PluginAppUtils.getInstance(activity).showToast(activity, e.getMessage());
                    }
                }
            } else {
                try {
                    onResponseListener.OnResponseSuccess(response);
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    PluginAppUtils.getInstance(activity).showToast(activity, e.getMessage());
                    onResponseListener.OnResponseFailure(response);
                }
            }
        }, volleyError -> {
            NetworkResponse response = volleyError.networkResponse;
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            try {
                onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", response.statusCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
            PluginAppUtils.getInstance(activity).handleVolleyError(activity, volleyError);
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;
            }

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        PluginAppUtils.getInstance(activity).addToRequestQueue(jsonObjReq, PluginAppUtils.TAG);
    }

    /**
     * The function performs a JSON POST request using Volley library in Android, including handling of
     * response and error cases, and optional display of progress dialog.
     *
     * @param activity The activity parameter is the reference to the current activity in which the
     * request is being made. It is usually passed as "this" from the calling activity.
     * @param url The URL to which the JSON request will be sent.
     * @param requestBodyParams The requestBodyParams parameter is a JSONObject that contains the
     * parameters to be sent in the request body of the JSON request. These parameters can be used to
     * pass data to the server for processing.
     * @param onResponseListener The onResponseListener is an interface that defines two methods:
     * OnResponseSuccess and OnResponseFailure. These methods will be called when the JSON request is
     * successful or when there is an error in the response, respectively. You can implement this
     * interface in your activity or fragment to handle the response accordingly.
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * the request is being processed.
     * @param requestHeaders A HashMap containing the headers to be included in the request. The keys
     * represent the header names, and the values represent the header values.
     */
    public void doPostJSONRequest(final AppCompatActivity activity, final String url, final JSONObject requestBodyParams, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        AppHelperMethods.getInstance(activity).traceLog("requestUrl", url);
        AppHelperMethods.getInstance(activity).traceLog("requestBody", requestBodyParams + "");
        AppHelperMethods.getInstance(activity).traceLog("requestHeaders", requestHeaders + "");

        JsonObjectRequest request_json = new JsonObjectRequest(url, requestBodyParams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (isToShowProgressDialog) {
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    }
                    onResponseListener.OnResponseSuccess(response);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (isToShowProgressDialog) {
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                }
                NetworkResponse response = error.networkResponse;
                try {
                    if (response != null) {
                        PluginAppUtils.getInstance(activity).showToast(activity, new JSONObject(new String(response.data)).optString("message"));
                    }
                    onResponseListener.OnResponseFailure(new JSONObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;
            }
        };

        // add the request object to the queue to be executed
        request_json.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(activity).addToRequestQueue(request_json);
    }

    /**
     * The `doPut` function is a Java method that performs a PUT request to a specified URL with
     * optional parameters, headers, and callbacks, and handles the response accordingly.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     * represents the current activity in which the method is being called.
     * @param url The URL to which the PUT request will be sent.
     * @param params A HashMap containing the parameters to be sent in the PUT request.
     * @param onResponseListener The `onResponseListener` is an interface that defines two methods:
     * `OnResponseSuccess` and `OnResponseFailure`. These methods are called when the response from the
     * server is successful or when there is an error, respectively. You can implement this interface
     * in your activity or fragment to handle the response
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * the request is being processed.
     * @param requestHeaders A HashMap containing the headers to be included in the PUT request. The
     * keys represent the header names, and the values represent the header values.
     */
    private void doPut(final AppCompatActivity activity, final String url, final HashMap<String, String> params, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, response0 -> {
            try {
                JSONObject response = new JSONObject(response0);
                if (isToShowProgressDialog) {
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                }
                if (response.has("httpCode")) {
                    if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                        PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                        onResponseListener.OnResponseSuccess(response);
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    } else {
                        onResponseListener.OnResponseFailure(response);
                        PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                    }
                } else {
                    PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                    onResponseListener.OnResponseSuccess(response);
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            PluginAppUtils.getInstance(activity).handleVolleyError(activity, error);
            NetworkResponse response = error.networkResponse;
            try {
                onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", response.statusCode));
            } catch (Exception e) {
                e.printStackTrace();
                onResponseListener.OnResponseFailure(new JSONObject());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PluginAppUtils.getInstance(activity).addToRequestQueue(stringRequest, PluginAppUtils.TAG);
    }

    /**
     * The function `doDelete` sends a DELETE request to a specified URL with optional parameters and
     * headers, and handles the response accordingly.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It
     * represents the current activity in which the method is being called.
     * @param url The URL of the API endpoint you want to send the DELETE request to.
     * @param params A HashMap containing the parameters to be sent in the DELETE request.
     * @param onResponseListener The `onResponseListener` is an interface that defines two methods:
     * `OnResponseSuccess` and `OnResponseFailure`. These methods are called when the response from the
     * server is successful or when there is a failure, respectively. You can implement this interface
     * in your activity or fragment to handle the response
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * the request is being processed.
     * @param requestHeaders A HashMap containing the headers to be included in the DELETE request. The
     * keys represent the header names, and the values represent the header values.
     */
    private void doDelete(final AppCompatActivity activity, final String url, final HashMap<String, String> params, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, response -> {
            try {
                if (isToShowProgressDialog) {
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                }
                if (response.has("httpCode")) {
                    if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                        PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                        onResponseListener.OnResponseSuccess(response);
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    } else {
                        onResponseListener.OnResponseFailure(response);
                        PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                    }
                } else {
                    PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                    onResponseListener.OnResponseSuccess(response);
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            PluginAppUtils.getInstance(activity).handleVolleyError(activity, volleyError);


        }) {

            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PluginAppUtils.getInstance(activity).addToRequestQueue(stringRequest, PluginAppUtils.TAG);
    }

    /**
     * The above function is a Java method that performs a GET request to a specified URL, handles the
     * response, and includes caching functionality.
     *
     * @param activity The activity parameter is the current instance of the AppCompatActivity class.
     * It is used to access the activity context and perform UI-related operations.
     * @param url The URL of the API endpoint you want to make a GET request to.
     * @param onResponseListener The `onResponseListener` is an interface that defines two methods:
     * `OnResponseSuccess` and `OnResponseFailure`. These methods are called when the response is
     * successful or when there is a failure in the response, respectively. You can implement this
     * interface to handle the response data or any error that
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * the request is being made.
     * @param requestHeaders A HashMap containing the headers to be included in the request.
     */
    private static void doGet(final AppCompatActivity activity, final String url, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            AppHelperMethods.getInstance(activity).traceLog("URL : ", url);
            AppHelperMethods.getInstance(activity).traceLog("Response : ", response + "");
            if (response.has("httpCode")) {
                if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                    try {
                        onResponseListener.OnResponseSuccess(response);
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onResponseListener.OnResponseFailure(response);
//                                     AppHelperMethods.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                } else if (response.optInt("httpCode") == HTTPCodeModel.HTTP_UNAUTHENTICATED) {
                    PluginAppUtils.getInstance(activity).showToast(activity, response.optString("message"));
                    try {
                        onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", response.optInt("httpCode")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PluginAppUtils.getInstance(activity).cancelPendingRequests(PluginAppUtils.TAG);
                } else if (response.optInt("httpCode") == 404) {
                    onResponseListener.OnResponseFailure(response);
                } else {
                    try {
                        onResponseListener.OnResponseFailure(response);
                    } catch (Exception e) {
                        e.printStackTrace();
//                                     AppHelperMethods.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                }
            } else {
                try {
                    onResponseListener.OnResponseSuccess(response);
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                } catch (Exception e) {
                    e.printStackTrace();
//                                 AppHelperMethods.getInstance(activity).showToast(activity,  e.getMessage());
                    onResponseListener.OnResponseFailure(response);
                }
            }
        }, volleyError -> {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);

            NetworkResponse response = volleyError.networkResponse;
            try {
                onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", response.statusCode));
            } catch (Exception e) {
                e.printStackTrace();
                onResponseListener.OnResponseFailure(new JSONObject());
            }

            PluginAppUtils.getInstance(activity).handleVolleyError(activity, volleyError);
        })
                //Beginning of Cache
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                        AppHelperMethods.getInstance(activity).traceLog("Cache", "Fresh Data");
                    } else {
                        AppHelperMethods.getInstance(activity).traceLog("Cache", "Cache Load" + "\n" + url);
                    }
                    final long cacheHitButRefreshed = 60 * 1000; // in 1 minute cache will be hit, but also refreshed on background
                    final long cacheExpired = 2 * 60 * 60 * 1000; // in 2 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                    AppHelperMethods.getInstance(activity).traceLog("CACHE_RESPONSE", "----------->" + jsonString);
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }

            // Ending of Cache


            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;

            }


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        PluginAppUtils.getInstance(activity).addToRequestQueue(jsonObjReq, PluginAppUtils.TAG);

    }

    /**
     * The `doPost` function is a Java method that sends a POST request to a specified URL with request
     * parameters and headers, and handles the response using a listener.
     *
     * @param activity The activity parameter is an instance of the AppCompatActivity class. It is used
     * to access the activity context and perform UI-related operations.
     * @param url The URL to which the POST request will be sent.
     * @param requestParams A HashMap containing the request parameters to be sent in the POST request.
     * The keys represent the parameter names and the values represent the parameter values.
     * @param onResponseListener The `onResponseListener` is an interface that defines two methods:
     * `OnResponseSuccess` and `OnResponseFailure`. These methods are called when the response from the
     * server is successful or when there is a failure, respectively. You can implement this interface
     * to handle the response in your activity or fragment
     * @param isToShowProgressDialog A boolean value indicating whether to show a progress dialog while
     * making the request.
     * @param requestHeaders A HashMap containing the headers to be included in the request. The keys
     * are the header names and the values are the header values.
     */
    private static void doPost(final AppCompatActivity activity, final String url, final HashMap<String, String> requestParams, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final HashMap<String, String> requestHeaders) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            AppHelperMethods.getInstance(activity).traceLog("URL : ", url);
            AppHelperMethods.getInstance(activity).traceLog("Response : ", response + "");
            try {
                if (isToShowProgressDialog) {
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                }
                final JSONObject mJsonObject = new JSONObject(response);

                if (mJsonObject.optInt("httpCode") == 201 || mJsonObject.optInt("httpCode") == 200) {
                    try {
                        onResponseListener.OnResponseSuccess(mJsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
//                   AppHelperMethods.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                } else if (mJsonObject.optInt("httpCode") == HTTPCodeModel.HTTP_UNAUTHENTICATED) {
                    PluginAppUtils.getInstance(activity).showToast(activity, mJsonObject.optString("message"));
                    try {
                        onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", mJsonObject.optInt("httpCode")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PluginAppUtils.getInstance(activity).cancelPendingRequests(PluginAppUtils.TAG);
                } else if (mJsonObject.optInt("httpCode") == 404) {
                    onResponseListener.OnResponseFailure(mJsonObject);
                } else if (!mJsonObject.has("httpCode")) { //event create success
                    try {
                        onResponseListener.OnResponseSuccess(mJsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
//                   AppHelperMethods.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                } else {
                    try {
                        PluginAppUtils.getInstance(activity).showToast(activity, mJsonObject.optString("message"));
                        onResponseListener.OnResponseFailure(mJsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
//                             AppHelperMethods.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, volleyError -> {
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            try {
                NetworkResponse response = volleyError.networkResponse;
                onResponseListener.OnResponseFailure(new JSONObject().put("httpCode", response.statusCode));
            } catch (Exception e) {
                e.printStackTrace();
                onResponseListener.OnResponseFailure(new JSONObject());
            }
            PluginAppUtils.getInstance(activity).handleVolleyError(activity, volleyError);

        }) {
            @Override
            protected Map<String, String> getParams() {
                return requestParams;
            }

            @Override
            public Map<String, String> getHeaders() {
                return requestHeaders;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PluginAppUtils.getInstance(activity).addToRequestQueue(stringRequest, PluginAppUtils.TAG);
    }

}