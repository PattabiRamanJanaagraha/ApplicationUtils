/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.webservice;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.HashMap;
import java.util.Map;

public class InputStreamVolleyRequest extends Request<byte[]> {

  private final Response.Listener<byte[]> mListener;
  private final Map<String, String> mParams;

  //create a static map for directly accessing headers
  public Map<String, String> responseHeaders;

  public InputStreamVolleyRequest(int method, String mUrl, Response.Listener<byte[]> listener,
      Response.ErrorListener errorListener, HashMap<String, String> params) {
    // TODO Auto-generated constructor stub

    super(method, mUrl, errorListener);
    // this request would never use cache.
    setShouldCache(false);
    mListener = listener;
    mParams = params;
  }

  @Override
  protected Map<String, String> getParams()
      throws com.android.volley.AuthFailureError {
    return mParams;
  }


    @Override
  protected void deliverResponse(byte[] response) {
    mListener.onResponse(response);
  }

  @Override
  protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

    //Initialise local responseHeaders map with response headers received
    responseHeaders = response.headers;

    //Pass the response data here
    return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
  }
}