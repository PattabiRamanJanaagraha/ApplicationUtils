/*
 * Created by Pattabi Raman on 11/05/23, 6:29 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 11/05/23, 6:29 PM
 */

package dev.pattabiraman.utils.locationutils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;
import java.util.List;

import dev.pattabiraman.utils.PluginAppConstant;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.permissionutils.GetPermissionResult;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;

public class GPSTracker extends Service implements LocationListener {
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000/* 1000 * 60 * 1 */; // 1
    // minute
    private final AppCompatActivity activity;
    // Declaring a Location Manager
    private LocationManager locationManager;
    // flag for GPS status
    private boolean canGetLocation = false;
    private Location location; // location
// double latitude; // latitude
// double longitude; // longitude

    public GPSTracker(AppCompatActivity context) {
        this.activity = context;
        getLocation();
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
            // getting GPS status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
//                // if GPS Enabled get lat/long using GPS Services
//                if (isGPSEnabled) {
//                    if (location == null) {
//                        /*try {
//                            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
//                            .PERMISSION_GRANTED &&
//                                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
//                                            .PERMISSION_GRANTED) {
//
//                            }
//                        }catch (Exception e){e.printStackTrace();}*/
//                        List<String> permissionsRequired = new ArrayList<>();
//                        permissionsRequired.clear();
//                        permissionsRequired.add(Manifest.permission.ACCESS_COARSE_LOCATION);
//                        permissionsRequired.add(Manifest.permission.ACCESS_FINE_LOCATION);
//                        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
//                            @Override
//                            public void resultPermissionSuccess() {
//                                Toast.makeText(activity, "Thanks for allowing permissions", Toast.LENGTH_SHORT).show();
//
//
//                            }
//
//                            @Override
//                            public void resultPermissionRevoked() {
//
//                            }
//                        });
//
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                        // Log.d("GPS Enabled", "GPS Enabled");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                AppController.latitude = location.getLatitude();
//                                AppController.longitude = location.getLongitude();
//                            }
//                        }
//                    }
//                }
//                if (isNetworkEnabled) {
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
//                    // Log.d("Network", "Network");
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            AppController.latitude = location.getLatitude();
//                            AppController.longitude = location.getLongitude();
//                            stopUsingGPS();
//                        }
//                    }
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            final List<String> permissionsRequired = new ArrayList<String>();
            permissionsRequired.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsRequired.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            ((PluginBaseAppCompatActivity) activity).runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
                @SuppressLint("MissingPermission")
                @Override
                public void resultPermissionSuccess() {
                    locationManager.removeUpdates(GPSTracker.this);
                }

                @Override
                public void resultPermissionRevoked() {
                    PluginAppUtils.getInstance(activity).showToast(activity, "We suggest to allow permissions to make app work as expected");

                }
            });
        }


    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            PluginAppConstant.latitude = location.getLatitude();
        }
        // return latitude
        return PluginAppConstant.latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            PluginAppConstant.longitude = location.getLongitude();
        }
        // return longitude
        return PluginAppConstant.longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    @SuppressWarnings("deprecation")
    public void showSettingsAlert() {/*
     * AlertDialog.Builder alertDialog = new
     * AlertDialog.Builder(activity);
     *
     * // Setting Dialog Title
     * alertDialog.setTitle("GPS  settings");
     *
     * // Setting Dialog Message alertDialog
     * .setMessage(
     * "GPS is not enabled. Do you want to go to settings menu?"
     * );
     *
     * // On pressing Settings button alertDialog
     * .setPositiveButton("Settings", new
     * DialogInterface.OnClickListener() { public
     * void onClick(DialogInterface dialog, int
     * which) { Intent intent = new Intent(
     * Settings.ACTION_LOCATION_SOURCE_SETTINGS );
     * activity.startActivity(intent); } });
     *
     * // on pressing cancel button
     * alertDialog.setNegativeButton("Cancel", new
     * DialogInterface.OnClickListener() { public
     * void onClick(DialogInterface dialog, int
     * which) { dialog.cancel(); } });
     *
     * // Showing Alert Message
     * alertDialog.show();
     */
        GoogleApiClient googleApiClient = null;
        googleApiClient = new GoogleApiClient.Builder(activity).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnectionSuspended(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onConnected(Bundle arg0) {
                // TODO Auto-generated method stub
            }
        }).addOnConnectionFailedListener(arg0 -> {
            // TODO Auto-generated method stub
        }).build();
        googleApiClient.connect();
        com.google.android.gms.location.LocationRequest locationRequest = com.google.android.gms.location.LocationRequest.create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationRequest.setPriority(LocationRequest.QUALITY_BALANCED_POWER_ACCURACY);
        }
        locationRequest.setInterval(20 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            // final LocationSettingsStates state = result.getLocationSettingsStates();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    // All location settings are satisfied. The client can
                    // initialize location
                    // requests here.
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    // Location settings are not satisfied. But could be
                    // fixed by showing the user
                    // a dialog.
                    try {
                        // Show the dialog by calling
                        // startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(activity, 1000);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    // Location settings are not satisfied. However, we have
                    // no way to fix the
                    // settings so we won't show the dialog.
                    break;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

