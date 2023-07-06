/*
 * Created by Pattabi Raman on 11/05/23, 5:12 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 11/05/23, 5:12 PM
 */

package dev.pattabiraman.utils.locationutils;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dev.pattabiraman.utils.AppHelperMethods;
import dev.pattabiraman.utils.permissionutils.GetPermissionResult;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;

 /**
  * The `MyLocation` class is a Java class that provides methods to get the current location of a
  * device using GPS or network providers and pass the location value to the user code through a
  * callback.
  */
 public class MyLocation {

    private Timer timer1;
    private LocationManager lm;
    private LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;
    private static AppCompatActivity activity;
    private Location net_loc = null, gps_loc = null;

    private int locationToMoveInMetres;

    /**
     * The function `getLocation` is used to get the current location of the device using GPS or
     * network providers and pass the location value to the user code through a callback.
     *
     * @param context The context parameter is an instance of the AppCompatActivity class, which
     * represents the current activity or context in which the getLocation method is being called. It
     * is used to access system services and resources.
     * @param result The "result" parameter is an instance of the LocationResult interface. It is used
     * to pass the location value from the MyLocation class to the user code. The user code should
     * implement the LocationResult interface and override its onLocationResult() method to receive the
     * location updates.
     * @param locationToMoveInMetres The parameter `locationToMoveInMetres` is the minimum distance (in
     * meters) that the device needs to move before the location is updated. This value is used in the
     * `requestLocationUpdates` method to specify the minimum distance between location updates.
     */
    public void getLocation(AppCompatActivity context, LocationResult result, int locationToMoveInMetres) {
        MyLocation.activity = context;
        this.locationToMoveInMetres = locationToMoveInMetres;

        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {
        }

        //don't start listeners if no provider is enabled
        if (!gps_enabled && !network_enabled) {
            return;
        }

        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat
                            .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, this.locationToMoveInMetres,
                        locationListenerGps);
                if (network_enabled) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, this.locationToMoveInMetres,
                            locationListenerNetwork);
                }
            }
        }


        timer1 = new Timer();
        timer1.schedule(new GetLastLocation(), 10000);

    }

    /**
     *  The above code is defining a private final LocationListener called locationListenerGps. This
     LocationListener is used to listen for changes in the GPS location.*/
    private final LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
//      AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_INFO,
//          "GPS : Your current location is accurate to " + location.getAccuracy() + " metres");
            locationResult.gotLocation(location, timer1, lm);
            lm.removeUpdates(locationListenerGps);


        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     *  The  code is defining a LocationListener for network-based location updates. When the
     location is changed, it cancels a timer, calls a method to handle the location result, and
     requests runtime permissions for accessing fine and coarse location. If the permissions are
     granted, it removes the network location updates.*/
    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
//      AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_INFO,
//          "Network : Your current location is accurate to " + location.getAccuracy() + " metres");
            locationResult.gotLocation(location, timer1, lm);
            final List<String> permissionsRequired = new ArrayList<String>();
            permissionsRequired.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsRequired.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            ((PluginBaseAppCompatActivity) activity).runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
                @SuppressLint("MissingPermission")
                @Override
                public void resultPermissionSuccess() {
//                  lm.removeUpdates(locationListenerGps);
                    lm.removeUpdates(locationListenerNetwork);
                }

                @Override
                public void resultPermissionRevoked() {
                }
            });

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetLastLocation extends TimerTask {

        @Override
        public void run() {
            final List<String> permissionsRequired = new ArrayList<String>();
            permissionsRequired.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsRequired.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            ((PluginBaseAppCompatActivity) activity)
                    .runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void resultPermissionSuccess() {
                            lm.removeUpdates(locationListenerGps);
                            lm.removeUpdates(locationListenerNetwork);
                        }

                        @Override
                        public void resultPermissionRevoked() {

                        }
                    });


            if (gps_enabled) {
                if (ActivityCompat
                        .checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat
                                .checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
            }

            ((PluginBaseAppCompatActivity) activity)
                    .runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void resultPermissionSuccess() {
                            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (network_enabled) {
                                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }

                            //if there are both values use the latest one
                            if (gps_loc != null && net_loc != null) {
                                if (gps_loc.getTime() > net_loc.getTime()) {
                                    locationResult.gotLocation(gps_loc, timer1, lm);
                                } else {
                                    locationResult.gotLocation(net_loc, timer1, lm);
                                }
                                return;
                            }

                            if (gps_loc != null) {
                                locationResult.gotLocation(gps_loc, timer1, lm);
                                return;
                            }
                            if (net_loc != null) {
                                locationResult.gotLocation(net_loc, timer1, lm);
                                return;
                            }
                            locationResult.gotLocation(null, timer1, lm);


                        }

                        @Override
                        public void resultPermissionRevoked() {
                        }
                    });
        }

    }

    /**
     * The removeLocationUpdates() function removes location updates from both GPS and network
     * providers, cancels a timer, and logs the action.
     */
    public void removeLocationUpdates() {
        try {
            this.lm.removeUpdates(locationListenerGps);
            this.lm.removeUpdates(locationListenerNetwork);
            AppHelperMethods.getInstance(activity)
                    .traceLog(activity.getClass().getSimpleName() + " : Removed Location Updates",
                            "locationListener GPS & Network");
            this.timer1.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

