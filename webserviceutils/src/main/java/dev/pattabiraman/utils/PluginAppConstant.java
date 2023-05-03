/*
 * Created by Pattabi Raman on 03/05/23, 2:33 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 03/05/23, 2:33 PM
 */

package dev.pattabiraman.utils;

import dev.pattabiraman.utils.model.SelectedImageModel;

public class PluginAppConstant {
    private static PluginAppConstant mInstance;

    public static PluginAppConstant getInstance() {
        return mInstance == null ? mInstance = new PluginAppConstant() : mInstance;
    }

    public static final int MY_SOCKET_TIMEOUT_MS = 864000;
    public static final int OPEN_SINGLE_MEDIA_PICKER = 2;
    public static final int CROP_PIC_REQUEST_CODE = 3;
    public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public SelectedImageModel selectedImageModel = new SelectedImageModel();

    public SelectedImageModel getSelectedImageModel() {
        return selectedImageModel;
    }
}
