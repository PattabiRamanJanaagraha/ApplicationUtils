/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.callback;

import org.json.JSONObject;

public interface OnResponseListener {
    void OnResponseFailure(JSONObject response);
    void OnResponseSuccess(JSONObject response);
}
