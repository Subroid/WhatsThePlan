package platinum.whatstheplan.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.security.auth.login.LoginException;

import platinum.whatstheplan.R;

/**
 * Right now, an activity that displays a map showing the place at the device's current location.
 */

public class OpenEventsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = "OpenEventsActivity";
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private final String KEY_LOCATION = "location";
    private final String CAMERA_POSITION = "camera_position";

    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION);
        }
        // ^Retrieving location and camera position from saved instance state.

        setContentView(R.layout.activity_open_events);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        initViewsAndVariables ();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            outState.putParcelable(CAMERA_POSITION, mMap.getCameraPosition());
            super.onSaveInstanceState(outState);
        }
    }

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

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Return null here, so that getInfoContents() is called next.
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (ViewGroup) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

       /* // Add a marker in Sydney and move the camera
        *//*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        // Prompt the user for permission.
        getLocationPermission ();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation ();

    }

    private void getLocationPermission() {
            /*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (ContextCompat.checkSelfPermission
                    (this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
            else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

    /**
     * Handles the result of the request for location permissions.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;

        }
        updateLocationUI ();

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        Log.d(TAG, "updateLocationUI: mMap = " + mMap);
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
                getLocationPermission();
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
            try {
                if (mLocationPermissionGranted) {
                  Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                  locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                      @Override
                      public void onComplete(@NonNull Task<Location> task) {
                          if (task.isSuccessful() && task.getResult() != null) {
                              Log.d(TAG, "onComplete: task = " + task.toString());
                              // Set the map's camera position to the current location of the device.
                              mLastKnownLocation = task.getResult();
                              Log.d(TAG, "onComplete: mLastKnownLocation = " + mLastKnownLocation);
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
