/*
 * Created by Pattabi Raman on 11/05/23, 5:50 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 11/05/23, 5:50 PM
 */

package dev.pattabiraman.utils.locationutils;

import android.location.Location;
import android.location.LocationManager;

import java.util.Timer;

 /**
  * The abstract class LocationResult defines a method for handling location updates.
  */
 public abstract class LocationResult {

    /**
     * This function takes in a location, timer, and location manager as parameters.
     *
     * @param location The location parameter is an object that represents the current location of the
     * device. It typically contains information such as latitude, longitude, altitude, and accuracy of
     * the location data.
     * @param timer The timer parameter is an instance of the Timer class. It is used to schedule and
     * execute tasks at specified intervals.
     * @param lm The LocationManager object is responsible for providing access to the system location
     * services. It is used to request location updates and manage location providers.
     */
    public abstract void gotLocation(Location location, Timer timer, LocationManager lm);
}
