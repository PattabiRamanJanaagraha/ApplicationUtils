/*
 * Created by Pattabi Raman on 11/05/23, 5:50 PM
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 11/05/23, 5:50 PM
 */

package dev.pattabiraman.utils.locationutils;

import android.location.Location;
import android.location.LocationManager;

import java.util.Timer;

public abstract class LocationResult {

    public abstract void gotLocation(Location location, Timer timer, LocationManager lm);
}
