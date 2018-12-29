package platinum.whatstheplan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.RestaurantsAdapter;
import platinum.whatstheplan.models.Restaurant;
import platinum.whatstheplan.models.UserInformation;
import platinum.whatstheplan.models.UserLocation;
import platinum.whatstheplan.models.UserProfile;

import static platinum.whatstheplan.utils.Constants.REQUEST_ERROR_DIALOG_CODE_61;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_PERMISSIONS_CODE_52;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_SETTINGS_CODE_51;

public class RestaurantsActivity extends FragmentActivity implements OnMapReadyCallback {

    //todo all the methods which returns or accepts parameter of Context or Activity

    private static final String TAG = "RestaurantsActivityTag";

    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserCurrentLocation;
    private LatLng mLastLatLng;
    private boolean mIsGpsEnabled;
    private boolean mLocationPermissionGranted = false;
    private FirebaseFirestore mDbFirestore;
    private RecyclerView mRestaurantsRV;
    private UserInformation mUserInformation;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        // Obtain the SupportMapFragment and get notified when the mapFragment is ready to be used.

//        initviewsAndVariables ();
        Log.d(TAG, "onCreate: called");
        initViewsAndVariables ();
        performActions ();

//        createLocationRequest ();
//        saveUserLocationIntoFirestore ();

    }

    private void initViewsAndVariables() {
        Log.d(TAG, "initViewsAndVariables: called");
        mRestaurantsRV = findViewById(R.id.restarantsRV);
        mDbFirestore = FirebaseFirestore.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RestaurantsActivity.this);
    }

    private void performActions() {
        Log.d(TAG, "performActions: called");
        mMapActions ();
        mRestaurantsRvActions ();

    }

    private void mMapActions() {
        Log.d(TAG, "mMapActions: called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(RestaurantsActivity.this);
    }

    private void mRestaurantsRvActions() {
        Log.d(TAG, "mRestaurantsRvActions: called");
        mRestaurantsRV.setHasFixedSize(true);
        DividerItemDecoration itemDecorator = new DividerItemDecoration
                (RestaurantsActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(RestaurantsActivity.this, R.drawable.divider));
        mRestaurantsRV.addItemDecoration(itemDecorator);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
        if(checkMapServices()){
//            if(mLocationPermissionGranted){
                //todo
                Log.d(TAG, "onResume: if");
                mMapActions ();
//            }

        } else{
            Log.d(TAG, "onResume: else");
            requestLocationPermission();
        }
    }

    private boolean checkMapServices(){
        Log.d(TAG, "checkMapServices: called");
        if(isPlayServicesOK()){
            if(mIsGpsEnabled()){
                return true;
            }
        }
        return false;
    }

    public boolean isPlayServicesOK(){
        Log.d(TAG, "isPlayServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(RestaurantsActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isPlayServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isPlayServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(RestaurantsActivity.this, available, REQUEST_ERROR_DIALOG_CODE_61);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean mIsGpsEnabled() {
        Log.d(TAG, "mIsGpsEnabled: called");
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
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
                       enableGpsIntent ();
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


    /*1*/
    /*private void createLocationRequest() {

        Log.d(TAG, "createLocationRequest: mIsGpsEnabled = " + mIsGpsEnabled);

        LocationManager locationManager = (LocationManager) getSystemService(LocationManager.GPS_PROVIDER);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mIsGpsEnabled = false;
            enableGpsIntent();
        } else {
            mIsGpsEnabled = true;
            initMap();
        }

        *//*Task<LocationSettingsResponse> taskLocationSettingsResponse = getLocationSettingsTask();

        taskLocationSettingsResponse.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                Log.d(TAG, "onComplete: taskLocationSettingsResponse called");
                mIsGpsEnabled = task.getResult().getLocationSettingsStates().isGpsUsable();
                Log.d(TAG, "onComplete: tLSR mIsGpsEnabled = " + mIsGpsEnabled);
                if (!mIsGpsEnabled) {
                    //TODO Create dialog before going to setting_intent
                    enableGpsIntent();
                } else {
                    Log.d(TAG, "onComplete: tLSR else called");
                    initMap();
                }
            }
        });*//*

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called");

        switch (requestCode) {
            case REQUEST_LOCATION_SETTINGS_CODE_51 :
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: permission granted");
                    mMapActions();
                } else {
                    requestLocationPermission ();
                }
        }
    }

    private void requestLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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
            ActivityCompat.shouldShowRequestPermissionRationale(RestaurantsActivity.this,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
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

    /*private Task<LocationSettingsResponse> getLocationSettingsTask() {
        LocationSettingsRequest.Builder location_settings_request_builder = new LocationSettingsRequest.Builder();
        location_settings_request_builder.setAlwaysShow(true);
        SettingsClient settingsClient = LocationServices.getSettingsClient(RestaurantsActivity.this);

        return settingsClient.checkLocationSettings(location_settings_request_builder.build());

    }*/

    /*2*/
    /*private void initMap() {
        Log.d(TAG, "initMap: called");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: called");
        stylizeMap ();
        getUserCurrentLocationAndSaveIntoRemoteDatabase ();

    }

    private void stylizeMap() {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(RestaurantsActivity.this, R.raw.style_json));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: if called");
            requestLocationPermissions();
        } else {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: else called");
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getUserCurrentLocationAndSaveIntoRemoteDatabase() {
        Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: called");
        final Location[] userCurrentLocationResults = {new Location(LocationManager.GPS_PROVIDER)};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: if called");
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
                        moveCameraToUserCurrentLocation (mUserCurrentLocation);
                        saveUserLocationIntoFirestoreThenDisplayRestaurantsNearUserLocation ();
                        Log.d(TAG, "onComplete: userCurrentLocationResults[0] = " + userCurrentLocationResults[0].getLatitude());
                    } else {
                        Log.d(TAG, "onComplete: else else");
                        addOnMyLocationButtonClickListener ();
                    }
                }
            });
        }

    }

    private void moveCameraToUserCurrentLocation(Location location) {
        Log.d(TAG, "moveCameraToUserCurrentLocation: called");
        mLastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "moveCameraToUserCurrentLocation: latitude = " + mLastLatLng.latitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
        mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(2.0));
    }

    private void addOnMyLocationButtonClickListener() {
        Log.d(TAG, "addOnMyLocationButtonClickListener: called");
        //todo prompting user for clicking my location button on map
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "onMyLocationButtonClick: called");
                if (ActivityCompat.checkSelfPermission(RestaurantsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermissions ();
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
                            moveCameraToUserCurrentLocation (mUserCurrentLocation);
                            saveUserLocationIntoFirestoreThenDisplayRestaurantsNearUserLocation ();
                        }
                    }
                });
                return true;
            }
        });
    }

    private void saveUserLocationIntoFirestoreThenDisplayRestaurantsNearUserLocation() {
        saveUserLocationIntoFirestore ();
        displayRestaurantsNearUserLocationAsListItem();
    }

    private void requestLocationPermissions() {
        Log.d(TAG, "requestLocationPermissions: called");
        ActivityCompat.requestPermissions(RestaurantsActivity.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSIONS_CODE_52);
    }

    private void saveUserLocationIntoFirestore() {

        Log.d(TAG, "saveUserLocationIntoFirestore: called");

                            CollectionReference dbUsersRef = mDbFirestore.collection("Users");
                            final DocumentReference dbUserRef = dbUsersRef.document(mUser.getUid());

                            dbUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
//                                        UserInformation userInformationResult = task.getResult().toObject(UserInformation.class);
//                                        UserProfile userProfile = new UserProfile();
                                        /*UserLocation userLocation = new UserLocation(new GeoPoint(
                                                mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude()),
                                                null);*/
                                        /*userProfile.setName(userInformationResult.getUserProfile().getName());
                                        userProfile.setEmail(userInformationResult.getUserProfile().getEmail());
                                        userProfile.setPassword(userInformationResult.getUserProfile().getPassword());
                                        userProfile.setUid(userInformationResult.getUserProfile().getUid());
                                        userProfile.setAdmin(userInformationResult.getUserProfile().isAdmin());
                                        UserInformation userInformation = new UserInformation(userProfile, userLocation);*/

                                        Map<String, Object> dataUserLocation = new HashMap<>();
                                        UserLocation userLocation = new UserLocation(new GeoPoint(
                                                mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude()), null);
                                        dataUserLocation.put("userLocation.geoPoint", userLocation.getGeoPoint());
                                        dataUserLocation.put("userLocation.timeStamp", null);


                                        dbUserRef.update(dataUserLocation);
                                        Log.d(TAG, "saveUserLocationIntoFirestore: timestamp = " + userLocation.getTimeStamp());

                                    }
                                }
                            });

                        }

                        /*@Override
                        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                            Log.d(TAG, "onRequestPermissionsResult: " + requestCode + " " + permissions.length + " " + grantResults.length);
                            Log.d(TAG, "onRequestPermissionsResult: mLocationPermissionGranted = " + mLocationPermissionGranted);
                            if (requestCode == REQUEST_LOCATION_PERMISSIONS_CODE_52) {
                                if (grantResults.length > 0) {
                                    mLocationPermissionGranted = true;
                                }
                            }
                        }*/

                        private void displayRestaurantsNearUserLocationAsListItem() {
                                Log.d(TAG, "displayRestaurantsNearUserLocationAsListItem: called 1");

                                CollectionReference mDbRestaurantsRef = mDbFirestore.collection("Restaurants");
                                final FirestoreRecyclerOptions<Restaurant> frOptions =
                                        new FirestoreRecyclerOptions.Builder<Restaurant>()
                                                .setQuery(mDbRestaurantsRef, Restaurant.class)
                                                .build();

                                Task<DocumentSnapshot> task = mDbFirestore.collection("Users").document(FirebaseAuth.getInstance().getUid()).get();
                                task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                           UserInformation userInformationResult = task.getResult().toObject(UserInformation.class);
                                           UserProfile userProfile = new UserProfile();
                                           UserLocation userLocation = new UserLocation();
                                           userProfile.setName(userInformationResult.getUserProfile().getName());
                                           userProfile.setEmail(userInformationResult.getUserProfile().getEmail());
                                           userProfile.setPassword(userInformationResult.getUserProfile().getPassword());
                                           userProfile.setUid(userInformationResult.getUserProfile().getUid());
                                           userProfile.setAdmin(userInformationResult.getUserProfile().isAdmin());
                                           userLocation.setGeoPoint(userInformationResult.getUserLocation().getGeoPoint());
                                           userLocation.setTimeStamp(userInformationResult.getUserLocation().getTimeStamp());
                                           mUserInformation = userInformationResult;
                                            Log.d(TAG, "displayRestaurantsNearUserLocationAsListItem: user name = " + mUserInformation.getUserProfile().getName());
                                            Log.d(TAG, "displayRestaurantsNearUserLocationAsListItem: timestamp = " + mUserInformation.getUserLocation().getTimeStamp());
                                            RestaurantsAdapter adapter = new RestaurantsAdapter(frOptions, RestaurantsActivity.this, mUserInformation, mMap);

                                            Log.d(TAG, "displayRestaurantsNearUserLocationAsListItem: called 2");


                                            mRestaurantsRV.setAdapter(adapter);
                                            adapter.startListening();
                                            mRestaurantsRV.setLayoutManager(new LinearLayoutManager(RestaurantsActivity.this));
                                        }
                                    }
                                });


                            }




}
