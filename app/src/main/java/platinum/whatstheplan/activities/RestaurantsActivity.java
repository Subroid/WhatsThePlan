package platinum.whatstheplan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.RestaurantsAdapter;
import platinum.whatstheplan.models.Restaurant;
import platinum.whatstheplan.models.UserInformation;
import platinum.whatstheplan.models.UserLocation;
import platinum.whatstheplan.models.UserProfile;

public class RestaurantsActivity extends FragmentActivity implements OnMapReadyCallback {

    //todo all the methods which returns or accepts parameter of Context or Activity

    private static final String TAG = "RestaurantsActivityTag";

    private static final int REQUEST_LOCATION_SETTINGS_CODE_51 = 51;
    private static final int REQUEST_LOCATION_PERMISSIONS_CODE_52 = 52;
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserCurrentLocation;
    private LatLng mLastLatLng;
    private boolean mIsGpsEnabled;
    private boolean mLocationPermissionGranted;
    private FirebaseFirestore mDbFirestore;
    private RecyclerView mRestaurantsRV;
    private UserInformation mUserInformation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        // Obtain the SupportMapFragment and get notified when the mapFragment is ready to be used.

//        initviewsAndVariables ();
        Log.d(TAG, "onCreate: mIsGpsEnabled = " + mIsGpsEnabled);

        mRestaurantsRV = findViewById(R.id.restarantsRV);

        createLocationRequest();
//        saveUserLocationIntoFirestore ();

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

    /*1*/
    private void createLocationRequest() {

        Log.d(TAG, "createLocationRequest: mIsGpsEnabled = " + mIsGpsEnabled);

        Task<LocationSettingsResponse> taskLocationSettingsResponse = getLocationSettingsTask();

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
        });

    }

    private void enableGpsIntent() {
        //todo alert dialog
        Intent setting_intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(setting_intent);
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
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: called");
        getUserCurrentLocationAndSaveIntoRemoteDatabase();

    }

        private void getUserCurrentLocationAndSaveIntoRemoteDatabase() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: if called");
                ActivityCompat.requestPermissions(RestaurantsActivity.this, new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_LOCATION_PERMISSIONS_CODE_52);
            } else {
                Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: else called");
                mMap.setMyLocationEnabled(true);
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RestaurantsActivity.this);
                Task<Location> taskLastLocation = mFusedLocationProviderClient.getLastLocation();

                taskLastLocation.addOnCompleteListener(RestaurantsActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: task is successful");
                        Log.d(TAG, "onComplete: isMyLocationEnabled = " + mMap.isMyLocationEnabled());
                        mUserCurrentLocation = task.getResult();
                        Log.d(TAG, "onComplete: mUserCurrentLocation latitude = " + mUserCurrentLocation.getLatitude());
                        mLastLatLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
                        mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(0.5));
                        mMap.addMarker(new MarkerOptions().position(mLastLatLng));

                        saveUserLocationIntoFirestore();

                        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                Log.d(TAG, "onMyLocationButtonClick: called");
                                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RestaurantsActivity.this);
                                if (ActivityCompat.checkSelfPermission(RestaurantsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(RestaurantsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return false;
                                }
                                mMap.setMyLocationEnabled(true);
                                Task<Location> task = mFusedLocationProviderClient.getLastLocation();
                                task.addOnCompleteListener(new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            mUserCurrentLocation = task.getResult();
                                            mLastLatLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
                                            Log.d(TAG, "onMyLocationButtonClick: latitude = " + mLastLatLng.latitude);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
                                            mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(0.5));
                                            mMap.addMarker(new MarkerOptions().position(mLastLatLng));

                                            saveUserLocationIntoFirestore();

                                        }
                                    }
                                });
                                return true;
                            }
                        });

                    } else {
                        //todo asking user to click mylocation button on map
                        Log.d(TAG, "onComplete: task not successful");
                        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                            @Override
                            public boolean onMyLocationButtonClick() {
                                Log.d(TAG, "onMyLocationButtonClick: called");
                                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RestaurantsActivity.this);
                                if (ActivityCompat.checkSelfPermission(RestaurantsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                        && ActivityCompat.checkSelfPermission(RestaurantsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return false;
                                }
                                mMap.setMyLocationEnabled(true);
                                Task<Location> task = mFusedLocationProviderClient.getLastLocation();
                                task.addOnCompleteListener(new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            mUserCurrentLocation = task.getResult();
                                            mLastLatLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
                                            Log.d(TAG, "onMyLocationButtonClick: latitude = " + mLastLatLng.latitude);
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
                                            mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(0.5));
                                            mMap.addMarker(new MarkerOptions().position(mLastLatLng));

                                            saveUserLocationIntoFirestore();

                                        }
                                    }
                                });
                                return true;
                            }
                        });

                            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                                @Override
                                public void onMyLocationClick(@NonNull Location location) {
                                    Log.d(TAG, "onMyLocationClick: called");
                                    mUserCurrentLocation = location;
                                    mLastLatLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
                                    Log.d(TAG, "onMyLocationClick: latitude = " + mLastLatLng.latitude);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
                                    mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(0.5));
                                    mMap.addMarker(new MarkerOptions().position(mLastLatLng));

                                    saveUserLocationIntoFirestore();
                                }
                            });

                        }

                    }
                });

            }
        }

                        private void saveUserLocationIntoFirestore() {
                            Log.d(TAG, "saveUserLocationIntoFirestore: mIsGpsEnabled = " + mIsGpsEnabled);
                            Log.d(TAG, "saveUserLocationIntoFirestore: mIsGpsEnabled = " + mIsGpsEnabled);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            CollectionReference dbUsersRef = db.collection("Users");
                            final DocumentReference dbUserRef = dbUsersRef.document(FirebaseAuth.getInstance().getUid());

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

                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(FirebaseAuth.getInstance().getUid()).update(dataUserLocation);
//                                        Log.d(TAG, "saveUserLocationIntoFirestore: timestamp = " + userLocation.getTimeStamp());

                                        displayRestaurantsNearUserLocation ();
                                    }
                                }
                            });

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

                            private void displayRestaurantsNearUserLocation() {
                                Log.d(TAG, "displayRestaurantsNearUserLocation: called 1");

                                mDbFirestore = FirebaseFirestore.getInstance();
                                CollectionReference mDbRestaurantsRef = mDbFirestore.collection("Restaurants");
                                final FirestoreRecyclerOptions<Restaurant> frOptions =
                                        new FirestoreRecyclerOptions.Builder<Restaurant>()
                                                .setQuery(mDbRestaurantsRef, Restaurant.class)
                                                .build();

                                Task<DocumentSnapshot> task = mDbFirestore.collection("Users").document(FirebaseAuth.getInstance().getUid()).get();
                                task.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
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
                                            Log.d(TAG, "displayRestaurantsNearUserLocation: user name = " + mUserInformation.getUserProfile().getName());
                                            Log.d(TAG, "displayRestaurantsNearUserLocation: timestamp = " + mUserInformation.getUserLocation().getTimeStamp());
                                            RestaurantsAdapter adapter = new RestaurantsAdapter(frOptions, RestaurantsActivity.this, mUserInformation);

                                            Log.d(TAG, "displayRestaurantsNearUserLocation: called 2");

                                            mRestaurantsRV.setHasFixedSize(true);
                                            DividerItemDecoration itemDecorator = new DividerItemDecoration
                                                    (RestaurantsActivity.this, DividerItemDecoration.VERTICAL);
                                            itemDecorator.setDrawable(ContextCompat.getDrawable(RestaurantsActivity.this, R.drawable.divider));
                                            mRestaurantsRV.addItemDecoration(itemDecorator);

                                            mRestaurantsRV.setAdapter(adapter);
                                            adapter.startListening();
                                            mRestaurantsRV.setLayoutManager(new LinearLayoutManager(RestaurantsActivity.this));
                                        }
                                    }
                                });


                            }
}
