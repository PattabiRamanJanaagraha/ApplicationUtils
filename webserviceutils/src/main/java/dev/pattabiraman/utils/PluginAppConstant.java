/*
 * Created by Pattabi Raman on 03/05/23, 2:33 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 03/05/23, 2:33 PM
 */

package dev.pattabiraman.utils;

import dev.pattabiraman.utils.model.SelectedImageModel;

public class PluginAppConstant {
    public static final int LOCATION_MOVEMENT_FOOTPATH = 5; //5 metres
    public static boolean isToLoadSelectedLocation;
    public static final String REQUEST_ALLOW_PERMISSION_STRING = "We suggest to allow permissions to make app work as expected";

    public static String location;
    public static String MAP_API_KEY = "";
    public static double latitude, longitude;
    public static boolean isAnyLocationSuggestionClicked;
    private static PluginAppConstant mInstance;


    /**
     * The function returns an instance of the PluginAppConstant class, creating a new instance if one
     * does not already exist.
     *
     * @return The method is returning an instance of the PluginAppConstant class.
     */
    public static PluginAppConstant getInstance() {
        return mInstance == null ? mInstance = new PluginAppConstant() : mInstance;
    }

    public static final int MY_SOCKET_TIMEOUT_MS = 864000;
    public static final int OPEN_SINGLE_MEDIA_PICKER = 2;
    public static final int CROP_PIC_REQUEST_CODE = 3;
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    /*click types to automate*/
    /**
     * @apiNote Directly performClick of Camera button in image picker's BottomSheetDialog.
     * (internal library based layout)
     */
    public static final int CLICK_TYPE_CAMERA = 0;
    /**
     * @apiNote Directly performClick of Gallery button in image picker's BottomSheetDialog.
     * (internal library based layout)
     */
    public static final int CLICK_TYPE_GALLERY = 1;
    /**
     * @apiNote Directly performClick of Cancel button in image picker's BottomSheetDialog.
     * (internal library based layout)
     */
    public static final int CLICK_TYPE_CANCEL = 2;
    /**
     * @apiNote show internal library based layout for image picker
     */
    public static final int CLICK_TYPE_NONE = -4;

   /**
    * Creates a new instance of the `SelectedImageModel` class and assigning it to the `selectedImageModel`
     variable. This allows the `selectedImageModel` variable to hold an object of type
     `SelectedImageModel` and access its properties and methods.*/
    public SelectedImageModel selectedImageModel = new SelectedImageModel();

    /**
     * The function returns the selected image model.
     *
     * @return The method is returning an object of type SelectedImageModel.
     */
    public SelectedImageModel getSelectedImageModel() {
        return selectedImageModel;
    }
}
