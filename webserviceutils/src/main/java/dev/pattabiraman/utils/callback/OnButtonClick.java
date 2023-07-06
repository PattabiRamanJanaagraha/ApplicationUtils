/*
 * Created by Pattabi Raman on 03/05/23, 2:08 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 02/05/23, 3:11 PM
 */

package dev.pattabiraman.utils.callback;

import android.content.DialogInterface;

 /**
  * The code is defining a Java interface called `OnButtonClick`. This interface has two methods:
 `onPositiveButtonClicked` and `onNegativeButtonClicked`. These methods take an argument of type
 `DialogInterface`.*/
public interface OnButtonClick {
    /**
     * The function is called when the positive button is clicked on a dialog interface.
     *
     * @param dialogInterface The DialogInterface object that represents the dialog interface.
     */
    void onPositiveButtonClicked(DialogInterface dialogInterface);
    /**
     * The function is called when the negative button is clicked on a dialog interface.
     *
     * @param dialogInterface The DialogInterface object representing the dialog interface that was
     * clicked.
     */
    void onNegativeButtonClicked(DialogInterface dialogInterface);
}
