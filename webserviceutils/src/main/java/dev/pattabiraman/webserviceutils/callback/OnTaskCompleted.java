package dev.pattabiraman.webserviceutils.callback;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by pattabi.raman on 01-09-2017.
 */

public interface OnTaskCompleted {
    void onTaskSuccess(JSONObject jsonObject);
    void onTaskFailure(VolleyError error);
}
