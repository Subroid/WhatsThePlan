package platinum.whatstheplan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import platinum.whatstheplan.R;

/**
 * Right now, an activity that displays a map showing the place at the device's current location.
 */

public class OpenEventsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS_CODE_9003 = 9003;

    // todo 201218 flowchart of this location checking and requesting process OR inline block numbering

    private final String TAG = "OpenEventsActivityTag";
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private final String KEY_LOCATION = "location";
    private final String CAMERA_POSITION = "camera_position";

    LocationRequest mLocationRequest;

    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE_1 = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION_CODE_2 = 2;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
   /*0.01*/ protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION);
        }*/
        // ^Retrieving location and camera position from saved instance state.

        setContentView(R.layout.activity_open_events);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        initViewsAndVariables ();

    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            outState.putParcelable(CAMERA_POSITION, mMap.getCameraPosition());
            super.onSaveInstanceState(outState);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace ();
        }
        return true;
    }

    private void showCurrentPlace() {

    }

    private void initViewsAndVariables() {

            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mMapFragment.getMapAsync(this);

        }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        /**1*/  createLocationRequest ();
      /**2*/  checkLocationPermissions ();
    }

   /*1*/ protected void createLocationRequest() {
       Log.d(TAG, "createLocationRequest: ");
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

  /*2*/  private void checkLocationPermissions() {
      Log.d(TAG, "checkLocationPermissions: ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
             /**3**/   checkLocationPermission ();
            }
        }
    }

   /*3*/ private void checkLocationPermission() {
       Log.d(TAG, "checkLocationPermission:");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
          /**4**/      buildAlertMessageNoGps ();
            } else  {
                ActivityCompat.requestPermissions(OpenEventsActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE_1);
            }
        }

    }

   /**4**/ private void buildAlertMessageNoGps() {
        // ^prompting user for GPS activation request
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("GPS Permission Required")
                .setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        ActivityCompat.requestPermissions(OpenEventsActivity.this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE_1);
                                    /*Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS_CODE_9003);*/
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    /*private void getLocationPermission() {
            *//*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             *//*
        Log.d(TAG, "getLocationPermission: mLocationPermissionGranted = " + mLocationPermissionGranted);

            if (ActivityCompat.checkSelfPermission
                    (this.getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            else {

                buildAlertMessageNoGps();
                Log.d(TAG, "getLocationPermission: ActivityCompat.requestPermissions got called");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE_1);
            }
        }*/

    /**
     * Handles the result of the request for location permissions.
     */

    @Override
  /**5**/  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: grantResults.length = " + grantResults.length);
        Log.d(TAG, "onRequestPermissionsResult: grantResults = " + grantResults);

        mLocationPermissionGranted = false;
        switch(requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION_CODE_1: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
            updateLocationUI();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS_CODE_9003 : {
                if (resultCode == RESULT_OK) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
//                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void  getDeviceLocation() {
            /*
             * Get the best and most recent location of the device,
             * which may be null in rare
             * cases when a location is not available.
             */
        Log.d(TAG, "getDeviceLocation: mLocationPermissionGranted = " + mLocationPermissionGranted);
            try {
                if (mLocationPermissionGranted) {
                  Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                  locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                      @Override
                      public void onComplete(@NonNull Task<Location> task) {
                          if (task.isSuccessful() && task.getResult() != null) {
                              mLastKnownLocation = task.getResult();
                              Log.d(TAG, "onComplete: mLastKnownLocation = " + mLastKnownLocation);
                              // Set the map's camera position to the current location of the device.
                              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                                      mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()
                              ), DEFAULT_ZOOM));
                          } else {
                              Log.d(TAG, "Current location is null. Using defaults.");
                              Log.e(TAG, "Exception: %s", task.getException());
                              mMap.moveCamera(CameraUpdateFactory
                                      .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                              mMap.getUiSettings().setMyLocationButtonEnabled(false);
                          }
                      }
                  });

                }
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            } catch (NullPointerException e) {
                Log.e("Exception: %s", e.getMessage());
            }
        }

}
