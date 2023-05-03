package dev.pattabiraman.webserviceutils.callback;

import org.json.JSONObject;

public interface OnResponseListener {
    void OnResponseFailure(JSONObject response);
    void OnResponseSuccess(JSONObject response);
}