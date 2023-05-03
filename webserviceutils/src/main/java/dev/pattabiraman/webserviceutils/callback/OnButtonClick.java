/*
 * Created by Pattabi Raman on 02/05/23, 11:27 AM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 11:21 AM
 */

package dev.pattabiraman.webserviceutils.callback;

import android.content.DialogInterface;

public interface OnButtonClick {
    void onPositiveButtonClicked(DialogInterface dialogInterface);
    void onNegativeButtonClicked(DialogInterface dialogInterface);
}
