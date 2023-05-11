package dev.pattabiraman.utils.locationutils;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import dev.pattabiraman.utils.PluginAppConstant;
import dev.pattabiraman.utils.PluginAppUtils;
import dev.pattabiraman.utils.adapter.SearchableAdapter;
import dev.pattabiraman.utils.permissionutils.GetPermissionResult;
import dev.pattabiraman.utils.permissionutils.PluginBaseAppCompatActivity;
import dev.pattabiraman.webserviceutils.R;
import dev.pattabiraman.webserviceutils.databinding.ActivityOtherLocationSearchBinding;

/**
 * @author Pattabi
 * @apiNote MUST DECLARE<br/> dev.pattabiraman.utils.locationutils.LocationSelectConfirmLocationOnMap <br/>IN YOUR PROJECT MANIFEST TO REQUEST RUNTIME PERMISSIONS
 * <br/>
 */
public class LocationSearchAutoCompleteActivity extends PluginBaseAppCompatActivity implements
        LocationListener {

    private AppCompatActivity activity;
    private static Double lat_value;
    private static Double lng_value;
    private SearchableAdapter searchadapter;
    private final List<String> permissionsRequired = new ArrayList<>();
    boolean isToShowDetectMyLocation = false;

    ActivityOtherLocationSearchBinding binding;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOtherLocationSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = LocationSearchAutoCompleteActivity.this;

        binding.locationsearch.setHighlightColor(ContextCompat.getColor(activity, R.color.yellow));
        binding.locationClear.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        binding.btnDetectLocation.setOnClickListener(v -> {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            checkForLocationPermission();
        });
        binding.ivBack.setOnClickListener(v -> {
            activity.finish();
        });

        onClear();
        try {
            isToShowDetectMyLocation = getIntent().getExtras().getBoolean("isToShowDetectMyLocation");
        } catch (Exception e) {
            isToShowDetectMyLocation = false;
        }
        findViewById(R.id.sepratelinelayout)
                .setVisibility(isToShowDetectMyLocation ? View.VISIBLE : View.GONE);
        binding.btnDetectLocation.setVisibility(isToShowDetectMyLocation ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
    }

    private void checkForLocationPermission() {
        permissionsRequired.clear();
        permissionsRequired.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissionsRequired.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

        runtimePermissionManager(LocationSearchAutoCompleteActivity.this, permissionsRequired, new GetPermissionResult() {
            @Override
            public void resultPermissionSuccess() {
                getCurrentLocation();
            }

            @Override
            public void resultPermissionRevoked() {
                PluginAppUtils.getInstance(activity).showToast(activity,
                        "We suggest to allow permissions to make app work as expected");

            }
        });
    }

    private void onClear() {
        binding.locationClear.setOnClickListener(v -> {
            if (binding.locationsearch.getText().toString().trim().length() > 0) {
                binding.locationsearch.setText("");
                binding.locationClear.setVisibility(View.GONE);
            }
        });
        configureEditText();
    }

    private void configureEditText() {
        binding.locationsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 2) {
                    searchadapter.getFilter().filter(s.toString());
                    binding.detectLocationRelativeLayout.setVisibility(View.GONE);
                } else {
                    binding.detectLocationRelativeLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() > 0) {
                    binding.locationClear.setVisibility(View.VISIBLE);
                } else {
                    binding.locationClear.setVisibility(View.GONE);
                }
            }
        });
        try {
            searchadapter = new SearchableAdapter(activity,
                    R.layout.autocomplete_list_item, PluginAppConstant.MAP_API_KEY);
            binding.locationSearch.setAdapter(searchadapter);
            binding.locationsearch.setSelection(binding.locationsearch.getText().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.locationSearch.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            String selectedFromList = (String) (binding.locationSearch
                    .getItemAtPosition(arg2));
            binding.locationsearch.setText(selectedFromList);
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
            PluginAppConstant.isAnyLocationSuggestionClicked = true;
            PluginAppConstant.location = selectedFromList;
            getLatLongFromAddress(selectedFromList);

        });
        binding.locationsearch.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    System.out.println("Hitted here enter===>");
                return false;
            }
            return false;
        });
    }


    private void getCurrentLocation() {
        try {
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, true);
            if (PluginAppUtils.getInstance(activity).setLatitudeLongitude(activity)) {

                LocationResult locationResult = new LocationResult() {
                    @Override
                    public void gotLocation(Location location, final Timer timer, final LocationManager lm) {
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                        if (location != null) {
                            timer.cancel();
                            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                            PluginAppConstant.isAnyLocationSuggestionClicked = true;

                            PluginAppConstant.latitude = location.getLatitude();
                            PluginAppConstant.longitude = location.getLongitude();
                            getAddressFromLatLong();
                        }
                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(activity, locationResult, PluginAppConstant.LOCATION_MOVEMENT_FOOTPATH);


            } else {
                PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
//                Toast.makeText(activity, activity.getResources().getString(R.string.location_capture_failed_please_try_again), Toast.LENGTH_SHORT)
//                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
            PluginAppUtils.getInstance(activity).showToast(activity, "We suggest you to allow permissions");

        }
    }

    private void getAddressFromLatLong() {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(PluginAppConstant.latitude, PluginAppConstant.longitude,
                    1); // Here 1 represent max location result to
            // returned, by

            PluginAppConstant.location = addresses.get(0).getAddressLine(0);
            if (PluginAppConstant.location != null) {
                if (PluginAppConstant.location.trim().length() > 0) {
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                    PluginAppConstant.isAnyLocationSuggestionClicked = true;

                    activity.finish();
                } else {
                    PluginAppUtils.getInstance(activity).showToast(activity,
                            "Location capture failed");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getLatLongFromAddress(final String youraddress) {
        PluginAppConstant.isAnyLocationSuggestionClicked = true;

        final String Searchingforlocation = youraddress.replaceAll(" ", "%20");
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?sensor=false&key="
                + PluginAppConstant.MAP_API_KEY + "&input=" + Searchingforlocation + "&components=country:in";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                response -> {
                    try {
                        PluginAppUtils.getInstance(activity).showProgressDialog(activity, false);
                        lat_value = ((JSONArray) response.get("results")).optJSONObject(0)
                                .optJSONObject("geometry").optJSONObject("location").getDouble("lat");
                        lng_value = ((JSONArray) response.get("results")).optJSONObject(0)
                                .optJSONObject("geometry").optJSONObject("location").getDouble("lng");
                        PluginAppConstant.latitude = lat_value;
                        PluginAppConstant.longitude = lng_value;
                        activity.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }, volleyError -> PluginAppUtils.getInstance(activity)
                .handleVolleyError(activity, volleyError)) {
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                PluginAppConstant.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        PluginAppUtils.getInstance(activity).addToRequestQueue(jsonObjReq,
                TAG);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        PluginAppUtils.getInstance(activity).hideKeyboard(activity, binding.locationsearch);
    }

    @Override
    public void finish() {
        super.finish();
        try {
            PluginAppConstant.isToLoadSelectedLocation = true;
        } catch (Exception ignore) {
        }
        PluginAppUtils.getInstance(activity).hideKeyboard(activity, binding.locationsearch);
    }
}
