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

/**
 *  The code is defining an interface called `OnTaskCompleted`. This interface has two methods:
 `onTaskSuccess` and `onTaskFailure`.*/
public interface OnTaskCompleted {
    /**
     * The function onTaskSuccess takes a JSONObject as a parameter and does something with it.
     *
     * @param jsonObject The jsonObject parameter is a JSONObject object that represents the JSON data
     * returned from a successful task.
     */
    void onTaskSuccess(JSONObject jsonObject);
    /**
     * The function is called when a task fails and takes a VolleyError object as a parameter.
     *
     * @param error The error parameter is an instance of the VolleyError class, which represents an
     * error that occurred during a network request made using the Volley library. It contains
     * information about the error, such as the error message and the cause of the error.
     */
    void onTaskFailure(VolleyError error);
}
