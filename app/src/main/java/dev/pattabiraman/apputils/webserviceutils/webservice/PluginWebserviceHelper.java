package dev.pattabiraman.apputils.webserviceutils.webservice;

import static dev.pattabiraman.webserviceutils.model.HTTPCodeModel.HTTP_UNAUTHENTICATED;

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

import dev.pattabiraman.webserviceutils.PluginAppUtils;
import dev.pattabiraman.webserviceutils.callback.OnResponseListener;


/**
 * Created by pattabi.raman on 20-02-2018.
 */

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

    public static final int HEADER_TYPE_NORMAL = 0;
    public static final int HEADER_TYPE_EVENTS = 1;
    public static final int HEADER_TYPE_NONE = 2;
    public static final int HEADER_TYPE_ANNOUNCEMENT = 3;
    public static final int HEADER_TYPE_ROLE = 4;
    public static final int HEADER_TYPE_NOTIFICATION = 5;
    public static final int HEADER_TYPE_PUBLIC_TOILETS = 6;
    public static final int HEADER_TYPE_AUTH = 7;
    public static final int HEADER_TYPE_PROFILE = 8;
    public static final int HEADER_TYPE_CONVERT_COMPLAINT_TO_EVENT = 9;
    public static final int HEADER_TYPE_WQS_SAVE_ROAD_SIDE = 10;
    public static final int HEADER_TYPE_GENERATE_QR_CODE = 11;
    public static final int HEADER_TYPE_GET_STATS_DASHBOARD = 12;


    public static final String TAG = PluginWebserviceHelper.class.getSimpleName();

    /**
     * @param activity               activity of calling API
     * @param methodType             either METHOD_GET, METHOD_POST,METHOD_PUT,METHOD_DELETE,METHOD_PATCH
     * @param url                    API url
     * @param params                 HashMap<String,String> if other than GET/PATCH method
     * @param onResponseListener     OnResponseListener callback for success/failure response
     * @param isToShowProgressDialog isToShowProgressDialog boolean-value if needed to show loader
     */

    public void runWebService(final AppCompatActivity activity, final int methodType, final String url, HashMap<String, String> params, OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        PluginAppUtils.getInstance(activity).traceLog("requestURL", url);
        if (params != null)
            PluginAppUtils.getInstance(activity).traceLog("requestParams", params.toString());
        switch (methodType) {
            case METHOD_GET:
                doGet(activity, url, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_POST:

                doPost(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_PUT:

                doPut(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_PATCH:
                doPatch(activity, url, onResponseListener, isToShowProgressDialog, headerType);
                break;
            case METHOD_DELETE:

                doDelete(activity, url, params, onResponseListener, isToShowProgressDialog, headerType);
                break;
            default:
                break;
        }
    }

    private static void doPatch(final AppCompatActivity activity, final String url, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PATCH, url, null, response -> {
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            PluginAppUtils.getInstance(activity).traceLog("URL : ", url);
            PluginAppUtils.getInstance(activity).traceLog("Response : ", response + "");
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
                } else if (response.optInt("httpCode") == HTTP_UNAUTHENTICATED) {
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
                HashMap<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("Accept", "application/json");
                return requestHeaders;
            }

            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(PluginAppUtils.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        PluginAppUtils.getInstance(activity).addToRequestQueue(jsonObjReq, PluginAppUtils.TAG);
    }

    private void doPut(final AppCompatActivity activity, final String url, final HashMap<String, String> params, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
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
                HashMap<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("Accept", "application/json");
                return requestHeaders;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(PluginAppUtils.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PluginAppUtils.getInstance(activity).addToRequestQueue(stringRequest, PluginAppUtils.TAG);
    }

    private void doDelete(final AppCompatActivity activity, final String url, final HashMap<String, String> params, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
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
                HashMap<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("Accept", "application/json");
                return requestHeaders;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(PluginAppUtils.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PluginAppUtils.getInstance(activity).addToRequestQueue(stringRequest, PluginAppUtils.TAG);
    }

    private static void doGet(final AppCompatActivity activity, final String url, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            if (isToShowProgressDialog) {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            }
            PluginAppUtils.getInstance(activity).traceLog("URL : ", url);
            PluginAppUtils.getInstance(activity).traceLog("Response : ", response + "");
            if (response.has("httpCode")) {
                if (response.optInt("httpCode") == 200 || response.optInt("httpCode") == 201) {
                    try {
                        onResponseListener.OnResponseSuccess(response);
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onResponseListener.OnResponseFailure(response);
//                                     PluginAppUtils.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                } else if (response.optInt("httpCode") == HTTP_UNAUTHENTICATED) {
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
//                                     PluginAppUtils.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                }
            } else {
                try {
                    onResponseListener.OnResponseSuccess(response);
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                } catch (Exception e) {
                    e.printStackTrace();
//                                 PluginAppUtils.getInstance(activity).showToast(activity,  e.getMessage());
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
                        PluginAppUtils.getInstance(activity).traceLog("Cache", "Fresh Data");
                    } else {
                        PluginAppUtils.getInstance(activity).traceLog("Cache", "Cache Load" + "\n" + url);
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
                    PluginAppUtils.getInstance(activity).traceLog("CACHE_RESPONSE", "----------->" + jsonString);
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
                HashMap<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("Accept", "application/json");
                return requestHeaders;

            }


        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(PluginAppUtils.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        jsonObjReq.setShouldCache(false);
        PluginAppUtils.getInstance(activity).addToRequestQueue(jsonObjReq, PluginAppUtils.TAG);

    }

    private static void doPost(final AppCompatActivity activity, final String url, final HashMap<String, String> requestParams, final OnResponseListener onResponseListener, final boolean isToShowProgressDialog, final int headerType) {
        if (isToShowProgressDialog) {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            PluginAppUtils.getInstance(activity).traceLog("URL : ", url);
            PluginAppUtils.getInstance(activity).traceLog("Response : ", response + "");
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
//                   PluginAppUtils.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                } else if (mJsonObject.optInt("httpCode") == HTTP_UNAUTHENTICATED) {
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
//                   PluginAppUtils.getInstance(activity).showToast(activity,  e.getMessage());
                    }
                } else {
                    try {
                        PluginAppUtils.getInstance(activity).showToast(activity, mJsonObject.optString("message"));
                        onResponseListener.OnResponseFailure(mJsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
//                             PluginAppUtils.getInstance(activity).showToast(activity,  e.getMessage());
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
                HashMap<String, String> requestHeaders = new HashMap<>();
                requestHeaders.put("Accept", "application/json");
                return requestHeaders;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(PluginAppUtils.MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PluginAppUtils.getInstance(activity).addToRequestQueue(stringRequest, PluginAppUtils.TAG);
    }

}