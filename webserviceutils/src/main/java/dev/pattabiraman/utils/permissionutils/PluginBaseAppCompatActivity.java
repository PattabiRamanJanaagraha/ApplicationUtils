/*
 * Created by Pattabi Raman on 03/05/23, 12:14 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 03/05/23, 12:14 PM
 */

package dev.pattabiraman.utils.permissionutils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.util.List;

/**
 * @author Pattabi
 * @apiNote MUST DECLARE<br/> dev.pattabiraman.permissionutils.PluginBaseAppCompatActivity <br/>IN YOUR PROJECT MANIFEST TO REQUEST RUNTIME PERMISSIONS
 **/
public class PluginBaseAppCompatActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();

    ProgressBar pb;
    private GetPermissionResult onGetPermissionResult;
    public static AppCompatActivity activity;
    private final int REQUEST_CODE_REQUEST_PERMISSION = 100;
    private int retryPermissionCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = PluginBaseAppCompatActivity.this;
    }


    /**
     * The function `runtimePermissionManager` checks the status of permissions in an Android activity
     * and calls a callback function with the result.
     *
     * @param activity              The activity parameter is an instance of the AppCompatActivity class. It
     *                              represents the current activity in which the permission manager is being used.
     * @param customPermission      The customPermission parameter is a List of String values that
     *                              represents the permissions you want to request from the user. These permissions can be any valid
     *                              Android permissions such as CAMERA, READ_EXTERNAL_STORAGE, etc.
     * @param onGetPermissionResult It is an interface that defines a callback method to handle the
     *                              result of the permission request. The method in the interface will be called with the result of
     *                              the permission request, indicating whether the permission was granted or denied by the user.
     */
    public void runtimePermissionManager(final AppCompatActivity activity, final List<String> customPermission, final GetPermissionResult onGetPermissionResult) {
        this.retryPermissionCount = 0;
        this.onGetPermissionResult = onGetPermissionResult;
        checkPermissionStatus(activity, customPermission);
    }


    /**
     * @param activity       AppCompatActivity object of calling class
     * @param permissionList List<String> of permissions to request user to allow
     */
    private void checkPermissionStatus(final AppCompatActivity activity, final List<String> permissionList) {
        String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        boolean isAnyPermissionDenied = false;
        // Check if the permissions are granted or not
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (allPermissionsGranted) {
            // Permissions are already granted, do your work here
            proceedAfterPermissionSuccess();
        } else {
            // Permissions are not granted, request them
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allPermissionsGranted = true;
        String deniedPermission = "";
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                deniedPermission = permissions[i];
                break;
            }
        }

        if (allPermissionsGranted) {
            // All Permissions granted
            onGetPermissionResult.resultPermissionSuccess();
        } else {
            // Permission is denied, show an explanation or request again
            // onGetPermissionResult.resultPermissionRevoked();
            retryPermissionCount += 1;
            if (retryPermissionCount <= 2)
                ActivityCompat.requestPermissions(this, new String[]{deniedPermission}, REQUEST_CODE_REQUEST_PERMISSION);
            else {
                onGetPermissionResult.resultPermissionRevoked();

            }

        }
    }

    /**
     * The function "proceedAfterPermissionSuccess" calls the "resultPermissionSuccess" method after a
     * successful permission request.
     */
    private void proceedAfterPermissionSuccess() {
        onGetPermissionResult.resultPermissionSuccess();
    }

    /**
     * The function "proceedAfterPermissionFailure" calls a method to handle the case when a permission
     * is revoked.
     */
    private void proceedAfterPermissionFailure() {
        onGetPermissionResult.resultPermissionRevoked();
    }

    /**
     * The function attempts to free up memory by running finalization, invoking the garbage collector,
     * and explicitly requesting garbage collection.
     */
    protected void freeMemory() {
        try {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The function deletes the cache directory in the given context.
     *
     * @param context The context parameter is an object that provides access to application-specific
     *                resources and information, such as the app's package name, resources, and system services. It is
     *                typically passed as an argument to methods that require access to these resources or need to
     *                perform operations related to the application's context. In this case
     */
    protected void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The function recursively deletes a directory and its contents if it exists.
     *
     * @param dir The "dir" parameter is a File object representing the directory that you want to
     *            delete.
     * @return The method `deleteDir` returns a boolean value. It returns `true` if the directory or
     * file is successfully deleted, and `false` otherwise.
     */
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < (children != null ? children.length : 0); i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void runtimePermissionManager(final AppCompatActivity activity, final List<String> customPermission, final GetPermissionResult onGetPermissionResult, final int uniqueRequestCodeForPermission) {
        this.retryPermissionCount = 0;
        this.onGetPermissionResult = onGetPermissionResult;
        checkPermissionStatus(activity, customPermission, uniqueRequestCodeForPermission);
    }


    private void checkPermissionStatus(final AppCompatActivity activity, final List<String> permissionList, final int uniqueRequestCodeForPermission) {
        String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        boolean isAnyPermissionDenied = false;
        // Check if the permissions are granted or not
        boolean allPermissionsGranted = true;
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }
        if (allPermissionsGranted) {
            // Permissions are already granted, do your work here
            proceedAfterPermissionSuccess();
        } else {
            // Permissions are not granted, request them
            ActivityCompat.requestPermissions(this, permissions, uniqueRequestCodeForPermission);
        }
    }
}
