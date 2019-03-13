package platinum.whatstheplan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

import static platinum.whatstheplan.utils.Constants.REQUEST_ERROR_DIALOG_CODE_61;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_PERMISSIONS_CODE_52;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_SETTINGS_CODE_51;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    private static final String TAG = "MapActivityTag";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private Context mContext;
    private GoogleMap mMap;
    private LatLng mLastLatLng;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserCurrentLocation;
    private Marker mUserMarker;
    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initViewsAndVariables ();
        performActions ();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        if (checkMapServices()) {
//            if(mLocationPermissionGranted){
            //todo
            Log.d(TAG, "onResume: if");
            mMapActions();
//            }

        } else {
            Log.d(TAG, "onResume: else");
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {

        Log.d(TAG, "requestLocationPermission: called");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //todo
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS_CODE_52);
            ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION);

        }
    }

        private void initViewsAndVariables() {
            mContext = MapActivity.this;
            bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapActivity.this);

        }

        private void performActions() {
            performBottomNavigationViewExActions ();
            mMapActions ();

    }

    private boolean checkMapServices() {
        Log.d(TAG, "checkMapServices: called");
        if (isPlayServicesOK()) {
            if (mIsGpsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayServicesOK() {
        Log.d(TAG, "isPlayServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MapActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isPlayServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isPlayServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MapActivity.this, available, REQUEST_ERROR_DIALOG_CODE_61);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean mIsGpsEnabled() {
        Log.d(TAG, "mIsGpsEnabled: called");
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "mIsGpsEnabled: if");
            buildAlertMessageNoGps();
            return false;
        }
        Log.d(TAG, "mIsGpsEnabled: true");
        return true;
    }

    private void buildAlertMessageNoGps() {
        Log.d(TAG, "buildAlertMessageNoGps: called");
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        enableGpsIntent();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void enableGpsIntent() {
        Log.d(TAG, "enableGpsIntent: called");
        //todo alert dialog
        Intent location_setting_intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(location_setting_intent, REQUEST_LOCATION_SETTINGS_CODE_51);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called");

        switch (requestCode) {
            case REQUEST_LOCATION_SETTINGS_CODE_51:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: permission granted");
                    mMapActions();
                } else {
                    requestLocationPermission();
                }
        }
    }

            private void performBottomNavigationViewExActions() {
                bottomNavigationViewEx.enableAnimation(false);
                bottomNavigationViewEx.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
                // ^disable shifting mode of item when clicked

                BottomNavigationViewHelper.settingBottomNavigationViewListener
                                            (bottomNavigationViewEx, mContext, getResources());
                bottomNavigationViewEx.getIconAt(2).setImageDrawable(getDrawable(R.drawable.ic_pin_blue));

            }

    private void mMapActions() {
        Log.d(TAG, "mMapActions: called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();

        getUserCurrentLocation ();
    }

    private void initMap() {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MapActivity.this, R.raw.style_json));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: if called");
            requestLocationPermissions();
        } else {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: else called");
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }

    private void requestLocationPermissions() {
        Log.d(TAG, "requestLocationPermissions: called");
        ActivityCompat.requestPermissions(MapActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSIONS_CODE_52);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode + " " + permissions.length + " " + grantResults.length);
        Log.d(TAG, "onRequestPermissionsResult: mLocationPermissionGranted = " + mLocationPermissionGranted);

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS_CODE_52: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    private void moveCameraToUserCurrentLocation(Location location) {
        Log.d(TAG, "moveCameraToUserCurrentLocation: called");
        mLastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "moveCameraToUserCurrentLocation: latitude = " + mLastLatLng.latitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
        mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(2.0));

    }

    private void getUserCurrentLocation() {
        Log.d(TAG, "getUserCurrentLocation: called");
        final Location[] userCurrentLocationResults = {new Location(LocationManager.GPS_PROVIDER)};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocation: if called");
            requestLocationPermissions();
        } else {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: else called");

            Task<Location> taskLastLocation = mFusedLocationProviderClient.getLastLocation();
            taskLastLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: else if");
                        userCurrentLocationResults[0] = task.getResult();
                        mUserCurrentLocation = userCurrentLocationResults[0];
                        moveCameraToUserCurrentLocation(mUserCurrentLocation);
                        setUserMarker();
                        Log.d(TAG, "onComplete: userCurrentLocationResults[0] = " + userCurrentLocationResults[0].getLatitude());
                    } else {
                        Log.d(TAG, "onComplete: else else");
                        addOnMyLocationButtonClickListener();
                    }
                }
            });
        }

    }

    private void setUserMarker() {
        LatLng latLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title("You are here")
                .snippet("Find Location Information around you"));
        mUserMarker.showInfoWindow();
        mMap.setOnCameraIdleListener(MapActivity.this);
    }

    private void addOnMyLocationButtonClickListener() {
        Log.d(TAG, "addOnMyLocationButtonClickListener: called");
        //todo prompting user for clicking my location button on map
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "onMyLocationButtonClick: called");
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermissions();
                    Log.d(TAG, "onMyLocationButtonClick: if");
                    return false;
                }
                mMap.setMyLocationEnabled(true);
                Task<Location> task = mFusedLocationProviderClient.getLastLocation();
                task.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mUserCurrentLocation = task.getResult();
                            moveCameraToUserCurrentLocation(mUserCurrentLocation);
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: called");

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: called");
        mUserMarker = marker;
        mUserMarker.setTitle("Loading...");
        mUserMarker.setSnippet("Address would be updated when dragging completes");
        mUserMarker.showInfoWindow();

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: called");
        mUserMarker = marker;
        LatLng markerPosition = mUserMarker.getPosition();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(markerPosition.latitude, markerPosition.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addressList.get(0);
        mUserMarker.setTitle(address.getFeatureName());
        mUserMarker.setSnippet(address.getAddressLine(0) + "\n" + address.getAddressLine(1));
        mUserMarker.showInfoWindow();

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick: called");
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        marker.setTitle(address.getFeatureName());
        marker.setSnippet(address.getAddressLine(0) + "\n" + address.getAddressLine(1));
        marker.showInfoWindow();

    }

    @Override
    public void onCameraIdle() {
        Log.d(TAG, "onCameraIdle: called");
      LatLng latLng =  mMap.getCameraPosition().target;
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> addressList = new ArrayList<>();
        try {
            addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        if (mUserMarker != null) {
            mUserMarker.remove();
            mUserMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            mUserMarker.setTitle(address.getFeatureName());
            mUserMarker.setSnippet(address.getAddressLine(0) + "\n" + address.getAddressLine(1));
            mUserMarker.showInfoWindow();
        } else {
            mUserMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            mUserMarker.setTitle(address.getFeatureName());
            mUserMarker.setSnippet(address.getAddressLine(0) + "\n" + address.getAddressLine(1));
            mUserMarker.showInfoWindow();
        }

    }
}
