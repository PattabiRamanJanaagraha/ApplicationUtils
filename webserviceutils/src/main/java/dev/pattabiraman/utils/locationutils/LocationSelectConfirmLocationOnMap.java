package dev.pattabiraman.utils.locationutils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;

import dev.pattabiraman.utils.PluginAppConstant;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.callback.HandleBackgroundThread;
import dev.pattabiraman.utils.callback.HandlerUtils;
import dev.pattabiraman.utils.permissionutils.GetPermissionResult;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;
import dev.pattabiraman.webserviceutils.R;
import dev.pattabiraman.webserviceutils.databinding.ActivityConfirmLocationOnMapBinding;

/**
 * @author Pattabi
 * @apiNote MUST DECLARE<br/> dev.pattabiraman.utils.locationutils.LocationSelectConfirmLocationOnMap <br/>IN YOUR PROJECT MANIFEST TO REQUEST RUNTIME PERMISSIONS
 * <br/>
 * MUST send putExtras("MAP_API_KEY",STRING) - value to be a working autosuggestion map key 
 * <br/>
 * MUST use below line in your app/res/values/strings.xml
 * <string name="mapview_api_key" translatable="false">PLACE_MAP_VIEW_API_KEY_HERE</string>
 * <br/>
 * MUST use below line in your app/AndroidManifest.xml
 *
 */
public class LocationSelectConfirmLocationOnMap extends PluginBaseAppCompatActivity implements
        OnMapReadyCallback {


    private static final String REQUEST_ALLOW_PERMISSION = "We suggest to allow permissions to make app work as expected";
    public static AppCompatActivity activity;
    private static GoogleMap mMap;
    MyLocation myLocation;
    LatLngBounds.Builder builder;
    boolean isFirstTime = true;

    LatLng tempLatLng;
    String tempLocationString;
    boolean isSuccess;
    Geocoder geocoder;
    List<Address> addresses = null;
    ActivityConfirmLocationOnMapBinding binding;
    public static boolean isAnyLocationSuggestionClicked;

    private final String UPDATING_ADDRESS_STRING = "Updating address...";
    private final String LOADING = "Loading...";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConfirmLocationOnMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PluginAppConstant.isToLoadSelectedLocation = false;
        activity = LocationSelectConfirmLocationOnMap.this;
        PluginAppConstant.MAP_API_KEY = getIntent().getExtras().getString("MAP_API_KEY");

        builder = new Builder();
        initiateGoogleMaps();

        setToolbarAndCustomizeTitle();
        binding.change.setOnClickListener(v -> activity.startActivity(new Intent(activity,
                LocationSearchAutoCompleteActivity.class).putExtra("isToShowDetectMyLocation", false).putExtra("MAP_API_KEY", PluginAppConstant.MAP_API_KEY)));

        PluginAppConstant.location = UPDATING_ADDRESS_STRING;
        binding.confirmLocation.setVisibility(View.INVISIBLE);

        binding.confirmLocation.setOnClickListener(v -> {
            if (PluginAppConstant.latitude != 0.0) {
                PluginAppConstant.isAnyLocationSuggestionClicked = true;

                activity.finish();
            } else {
                //pick a location
            }
        });
        binding.infoWindow.setVisibility(View.INVISIBLE);
        binding.selectedLocationText.setText(REQUEST_ALLOW_PERMISSION);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PluginAppConstant.isToLoadSelectedLocation && PluginAppConstant.latitude != 0.0) {
            binding.selectedLocationText.setText(UPDATING_ADDRESS_STRING);
            builder = new Builder();
            builder.include(new LatLng(PluginAppConstant.latitude, PluginAppConstant.longitude));
            animateToLocation(builder);
        }
    }

    @SuppressWarnings("deprecation")
    private void setToolbarAndCustomizeTitle() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> {
            activity.finish();
            retainOriginalLatLngLocationData();
        });
        final Drawable upArrow = ContextCompat.getDrawable(activity, R.drawable.ic_baseline_arrow_back_ios_24);
        if (upArrow != null) {
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        String CONFIRM_LOCATION = "Confirm Location";
        getSupportActionBar().setTitle(CONFIRM_LOCATION);
        binding.toolbar.setTitleTextColor(Color.WHITE);

    }

    private void initiateGoogleMaps() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    List<String> permissionsRequired = new ArrayList<>();

    private void checkForLocationPermission() {
        permissionsRequired.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsRequired.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
            @SuppressLint("MissingPermission")
            @Override
            public void resultPermissionSuccess() {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                binding.selectedLocationText.setText(LOADING);

                getCurrentLocation();
            }

            @Override
            public void resultPermissionRevoked() {
                PluginAppUtils.getInstance(activity).showToast(activity, REQUEST_ALLOW_PERMISSION);
                binding.selectedLocationText
                        .setText(REQUEST_ALLOW_PERMISSION);
                PluginAppConstant.latitude = 0.0;
                PluginAppConstant.longitude = 0.0;
                startActivity(new Intent(activity, LocationSearchAutoCompleteActivity.class)
                        .putExtra("isToShowDetectMyLocation", false));
                mMap.setOnCameraIdleListener(() -> {
                    if (PluginAppConstant.latitude != 0.0) {
                        builder = new Builder();
                        builder.include(new LatLng(PluginAppConstant.latitude, PluginAppConstant.longitude));
                        animateToLocation(builder);
                    } else {
                        startActivity(new Intent(activity, LocationSearchAutoCompleteActivity.class)
                                .putExtra("isToShowDetectMyLocation", false));
                    }
                });
            }
        });
    }

    private void getCurrentLocation() {
        try {
            LocationResult locationResult = new LocationResult() {

                @Override
                public void gotLocation(final Location location, final Timer timer1, LocationManager lm) {
                    if (location != null && isFirstTime) {
                        PluginAppConstant.latitude = location.getLatitude();
                        PluginAppConstant.longitude = location.getLongitude();
                        builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(PluginAppConstant.latitude, PluginAppConstant.longitude));
                        timer1.cancel();
                        myLocation.removeLocationUpdates();
                        onGetCurrentLocation();
                        //AppController.getInstance().showProgressDialog(activity, false);
                    }
                }
            };

            myLocation = new MyLocation();
            myLocation.getLocation(activity, locationResult, PluginAppConstant.LOCATION_MOVEMENT_FOOTPATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onGetCurrentLocation() {
        runOnUiThread(() -> {
            mMap.setOnCameraIdleListener(() -> {
                binding.confirmLocation.setVisibility(View.VISIBLE);
                binding.infoWindow.setVisibility(View.VISIBLE);
                PluginAppConstant.latitude = mMap.getCameraPosition().target.latitude;
                PluginAppConstant.longitude = mMap.getCameraPosition().target.longitude;
                getAddressFromLatLong();
//                if (PluginAppConstant.isToLoadSelectedLocation) {
//                    PluginAppConstant.isToLoadSelectedLocation = false;
//                }
            });
            mMap.setOnCameraMoveListener(() -> {
                binding.infoWindow.setVisibility(View.INVISIBLE);
                binding.confirmLocation.setVisibility(View.INVISIBLE);
            });
            animateToLocation(builder);
        });
        isFirstTime = false;
    }

//  Marker marker;

    private void animateToLocation(final Builder builder) {
        try {
            if (mMap != null) {
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                if (isFirstTime || PluginAppConstant.isToLoadSelectedLocation) {
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 8);
                    mMap.animateCamera(cu);
                }
                binding.confirmLocation.setVisibility(View.VISIBLE);
                binding.selectedLocationText.setText(PluginAppConstant.location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        checkForLocationPermission();
        mMap.setOnMyLocationClickListener(location -> {
            PluginAppConstant.latitude = location.getLatitude();
            PluginAppConstant.longitude = location.getLongitude();
            getCurrentLocation();
        });
    }


    private void getAddressFromLatLong() {
        //AppController.getInstance().showProgressDialog(activity, false);
        if (!PluginAppConstant.isToLoadSelectedLocation) {
            binding.selectedLocationText.setText(LOADING);
        }
        new HandlerUtils().handleAsyncOperation(activity, new HandleBackgroundThread() {
            @Override
            public void handleDoInBackground() {
                geocoder = new Geocoder(activity, Locale.getDefault());
                try {
                    addresses = geocoder.getFromLocation(PluginAppConstant.latitude, PluginAppConstant.longitude, 1); // Here 1 represent max location result to returned, by
                    if (!isAnyLocationSuggestionClicked) {
                        PluginAppConstant.location = addresses.get(0).getAddressLine(0);
                    } else {
                        isAnyLocationSuggestionClicked = false;
                    }
                    if (PluginAppConstant.location != null) {
                        if (PluginAppConstant.location.trim().length() > 0) {
                            isSuccess = true;
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void handlePostExecute() {
                //AppController.getInstance().showProgressDialog(activity, false);
                if (!PluginAppConstant.isToLoadSelectedLocation) {
                    if (isSuccess) {
                        binding.selectedLocationText.setText(LOADING);
                        builder = new LatLngBounds.Builder();
                        builder.include(new LatLng(PluginAppConstant.latitude, PluginAppConstant.longitude));
                        animateToLocation(builder);
                    } else {
                        PluginAppUtils.getInstance(activity).showToast(activity, "Location capture failed, try again...");
                    }
                } else {
                    binding.selectedLocationText.setText(PluginAppConstant.location);
                }
            }

            @Override
            public void handleException(@Nullable String exceptionMessage) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        retainOriginalLatLngLocationData();
    }


    private void retainOriginalLatLngLocationData() {
        if (!TextUtils.isEmpty(tempLocationString) && tempLatLng.latitude != 0.0) {
            PluginAppConstant.latitude = tempLatLng.latitude;
            PluginAppConstant.longitude = tempLatLng.longitude;
            PluginAppConstant.location = tempLocationString;
        }
        isAnyLocationSuggestionClicked = false;

    }
}