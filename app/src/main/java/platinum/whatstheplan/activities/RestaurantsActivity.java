package platinum.whatstheplan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import platinum.whatstheplan.R;

public class RestaurantsActivity extends FragmentActivity implements OnMapReadyCallback {

    //todo all the methods which returns or accepts parameter of Context or Activity

    private static final String TAG = "RestaurantsActivityTag";

    private static final int REQUEST_LOCATION_SETTINGS_CODE_51 = 51;
    private static final int REQUEST_LOCATION_PERMISSIONS_CODE_52 = 52;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation;
    private LatLng mLastLatLng;
    private boolean mIsGpsEnabled;
    private boolean mLocationPermissionGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

//        initviewsAndVariables ();

        createLocationRequest();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: mIsGpsEnabled = " + mIsGpsEnabled);
        if (!mIsGpsEnabled) {
            createLocationRequest();
        } else {
            initMap();
        }
    }

        private void enableGpsIntent() {
            //todo alert dialog
            Intent setting_intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(setting_intent);
        }

    /*1*/
    private void createLocationRequest() {

       Task<LocationSettingsResponse> taskLocationSettingsResponse = getLocationSettingsTask();

        taskLocationSettingsResponse.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                Log.d(TAG, "onComplete: if called" );
                mIsGpsEnabled = task.getResult().getLocationSettingsStates().isGpsUsable();
                Log.d(TAG, "onComplete: mIsGpsEnabled = " + mIsGpsEnabled);
                if (!mIsGpsEnabled) {
                  //TODO Create dialog before going to setting_intent
                  enableGpsIntent();
              } else {
                  Log.d(TAG, "onComplete: else called");
                  initMap();
              }
            }
        });

        /*taskLocationSettingsResponse.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse location_settings_response = task.getResult(ApiException.class);
                    Log.d(TAG, "onComplete: isGpsUsable = " + location_settings_response.getLocationSettingsStates().isGpsUsable());
                    Log.d(TAG, "onComplete: isGpsPresent = " + location_settings_response.getLocationSettingsStates().isGpsPresent());
                } catch (ApiException apiException) {
                    Log.d(TAG, "onComplete: apiException.getStatusCode() = " + apiException.getStatusCode());
                    switch (apiException.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED :
                            ResolvableApiException resolvableApiException = (ResolvableApiException) apiException;
                            try {
                                resolvableApiException.startResolutionForResult(RestaurantsActivity.this, REQUEST_LOCATION_SETTINGS_CODE_51);
                            } catch (IntentSender.SendIntentException sendEx) {
                                sendEx.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE :

                            break;
                    }
                }


            }
        });*/

    }

            private Task<LocationSettingsResponse> getLocationSettingsTask() {
                LocationSettingsRequest.Builder location_settings_request_builder = new LocationSettingsRequest.Builder();
                location_settings_request_builder.setAlwaysShow(true);
                SettingsClient settingsClient = LocationServices.getSettingsClient(RestaurantsActivity.this);

                return settingsClient.checkLocationSettings(location_settings_request_builder.build());

            }


            /*2*/
            private void initMap() {
                Log.d(TAG, "initMap: called");

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);
            }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d(TAG, "onMapReady: selfPermission = " + ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady: returned" );
            ActivityCompat.requestPermissions(RestaurantsActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS_CODE_52);
        } else {
            mMap.setMyLocationEnabled(true);
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RestaurantsActivity.this);
            Task<Location> taskLastLocation = mFusedLocationProviderClient.getLastLocation();

            taskLastLocation.addOnCompleteListener(RestaurantsActivity.this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    mLastLocation = task.getResult();
                    Log.d(TAG, "onComplete: mLastLocation = " + mLastLocation);
                    mLastLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
                    mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(2.0));
                    mMap.addMarker(new MarkerOptions().position(mLastLatLng));
                }
            });
        }

//

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode + " " + permissions.length + " " + grantResults.length);
        Log.d(TAG, "onRequestPermissionsResult: mLocationPermissionGranted = " + mLocationPermissionGranted);
        if (requestCode == REQUEST_LOCATION_PERMISSIONS_CODE_52) {
            if (grantResults.length > 0) {
                mLocationPermissionGranted = true;
            }
        }
    }
}
