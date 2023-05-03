/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.callback;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by pattabi.raman on 01-09-2017.
 */

public interface OnTaskCompleted {
    void onTaskSuccess(JSONObject jsonObject);
    void onTaskFailure(VolleyError error);
}
