/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.callback;

import org.json.JSONObject;

 /**
  * The code is defining a Java interface called `OnResponseListener`. This interface has two methods:
 `OnResponseFailure` and `OnResponseSuccess`. Both methods take a `JSONObject` parameter named
 `response` and have a void return type. This interface is likely used as a callback mechanism to
 handle responses in an asynchronous operation.*/
public interface OnResponseListener {
    /**
     * The function OnResponseFailure handles the failure response from a server in JSON format.
     *
     * @param response The response parameter is a JSONObject that represents the response received
     * from a server or API. It contains the data and metadata associated with the response.
     */
    void OnResponseFailure(JSONObject response);
    /**
     * The function OnResponseSuccess takes a JSONObject as a parameter and is called when a response
     * is successfully received.
     *
     * @param response The response parameter is a JSONObject, which is a data structure that
     * represents a JSON object. It contains key-value pairs where the keys are strings and the values
     * can be of various types such as strings, numbers, booleans, arrays, or nested JSON objects. In
     * this case, the response parameter represents
     */
    void OnResponseSuccess(JSONObject response);
}
