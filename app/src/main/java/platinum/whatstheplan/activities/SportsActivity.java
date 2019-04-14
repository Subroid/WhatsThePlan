package platinum.whatstheplan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.EventsAdapter;
import platinum.whatstheplan.adapters.VenuesAdapter;
import platinum.whatstheplan.interfaces.EventItemTapListener;
import platinum.whatstheplan.interfaces.PageLoadingListener;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.UserInformation;
import platinum.whatstheplan.models.UserLocation;
import platinum.whatstheplan.models.Venue;

import static platinum.whatstheplan.utils.Constants.REQUEST_ERROR_DIALOG_CODE_61;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_PERMISSIONS_CODE_52;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_SETTINGS_CODE_51;

public class SportsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        EventItemTapListener,
        GeoQueryEventListener, View.OnClickListener,
        PageLoadingListener {

    private static final String TAG = "SportsActivityTag";

    private GoogleMap mMap;
    private ProgressBar mProgressBarPB;
    private EditText mRadiusET;
    private Button mFindBTN;
    private TextView mNoEventTV;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserCurrentLocation;
    private LatLng mLastLatLng;
    private boolean mIsGpsEnabled;
    private boolean mLocationPermissionGranted = false;
    private FirebaseFirestore mDbFirestore;
    private FirebaseDatabase mDbFirebase;
    private RecyclerView mSportsRV;
    private UserInformation mUserInformation;
    private FirebaseUser mUser;
    private Marker mMarker;
    private Marker mUserMarker;
    private LatLng mUserLatLng;
    private LatLng mTargetLatLng;
    private GeoPoint mQueryCenter;
    private GeoFirestore mGeoFirestore;
    private GeoFire mGeoFirebase;
    private GeoQuery mGeoQuery;
    private com.firebase.geofire.GeoQuery mGeoFireQuery;
    private GeoLocation mGeoLocation;
    private Venue mVenue;
    private List<Venue> mVenueList;
    private List<String> mKeyList;
    private int mRadius;
    private PlacesClient mPlacesClient;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        Log.d(TAG, "onCreate: called");
        initViewsAndVariables();
        performActions();

    }

    private void initViewsAndVariables() {
        Log.d(TAG, "initViewsAndVariables: called");
        mKeyList = new ArrayList<>();
        mVenueList = new ArrayList<>();
        mSportsRV = findViewById(R.id.sports_RV);
        mRadiusET = findViewById(R.id.radius_ET);
        mNoEventTV = findViewById(R.id.no_event_TV);
        mRadius = Integer.parseInt(mRadiusET.getText().toString());
        mFindBTN = findViewById(R.id.find_BTN);
        mProgressBarPB = findViewById(R.id.progressBar);
        mDbFirestore = FirebaseFirestore.getInstance();
        mDbFirebase = FirebaseDatabase.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SportsActivity.this);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));

    }

    private void performActions() {
        Log.d(TAG, "performActions: called");
        setClickListeners();
        mMapActions();
        mSportsRvActions();

    }

    private void setClickListeners() {
        mFindBTN.setOnClickListener(this);
    }

    private void mMapActions() {
        Log.d(TAG, "mMapActions: called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(SportsActivity.this);
    }

    private void mSportsRvActions() {
        Log.d(TAG, "mSportsRvActions: called");
        mSportsRV.setHasFixedSize(true);
        DividerItemDecoration itemDecorator = new DividerItemDecoration
                (SportsActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(SportsActivity.this, R.drawable.divider));
        mSportsRV.addItemDecoration(itemDecorator);

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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SportsActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isPlayServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isPlayServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SportsActivity.this, available, REQUEST_ERROR_DIALOG_CODE_61);
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

    private void requestLocationPermission() {

        Log.d(TAG, "requestLocationPermission: called");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            //todo
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSIONS_CODE_52);
            ActivityCompat.shouldShowRequestPermissionRationale(SportsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d(TAG, "onMapReady: called");
        initMap();
        getUserCurrentLocationAndSaveIntoRemoteDatabase();

    }

    private void initMap() {
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(SportsActivity.this, R.raw.style_json));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: if called");
            requestLocationPermissions();
        } else {
            Log.d(TAG, "getUserCurrentLocationAndSaveIntoRemoteDatabase: else called");
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
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
                        moveCameraToUserCurrentLocation(mUserCurrentLocation);
                        setUserMarker();
                        saveUserLocationIntoFirestoreThenDisplaySportsNearUserLocation();
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
                .title("You are here")
                .snippet("Find Sports around you"));
        mUserMarker.showInfoWindow();
    }

    private void moveCameraToUserCurrentLocation(Location location) {
        Log.d(TAG, "moveCameraToUserCurrentLocation: called");
        mLastLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "moveCameraToUserCurrentLocation: latitude = " + mLastLatLng.latitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLastLatLng, 15));
        mMap.addCircle(new CircleOptions().center(mLastLatLng).radius(2.0));

    }

    private void addOnMyLocationButtonClickListener() {
        Log.d(TAG, "addOnMyLocationButtonClickListener: called");
        //todo prompting user for clicking my location button on map
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "onMyLocationButtonClick: called");
                if (ActivityCompat.checkSelfPermission(SportsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            saveUserLocationIntoFirestoreThenDisplaySportsNearUserLocation();
                        }
                    }
                });
                return true;
            }
        });
    }

    private void saveUserLocationIntoFirestoreThenDisplaySportsNearUserLocation() {
        saveUserLocationIntoFirestore();
        displaySportsNearUserLocation(mRadius);
//        displaySportsWithin5km();
//        displaySportsNearUserLocation();
    }



    private void requestLocationPermissions() {
        Log.d(TAG, "requestLocationPermissions: called");
        ActivityCompat.requestPermissions(SportsActivity.this, new String[]
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


    private void displaySportsNearUserLocation(int radius) {
        mKeyList.clear();
        mVenueList.clear();
        Log.d(TAG, "displaySportsNearUserLocation: radius = " + radius);
        mGeoLocation = new GeoLocation(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
        Log.d(TAG, "displaySportsInTheRangeOf5km: mUserCurrentLocation.getLatitude = " + mUserCurrentLocation.getLatitude());
        Log.d(TAG, "displaySportsInTheRangeOf5km: mUserCurrentLocation.getLongitude = " + mUserCurrentLocation.getLongitude());
        DatabaseReference mDbSportsFirebase = mDbFirebase.getReference("SportsVenues");
        mGeoFirebase = new GeoFire(mDbSportsFirebase);
        mGeoFireQuery = mGeoFirebase.queryAtLocation(mGeoLocation, radius);
        mGeoFireQuery.removeAllListeners();
        mGeoFireQuery.addGeoQueryEventListener(this);

    }

    private void setUserMarkerWithoutUpdatingCamera() {
        LatLng latLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
        mUserMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You are here")
                .snippet("Find Sports around you"));
        mUserMarker.showInfoWindow();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private void getDirection(Event event, Location userCurrentLocation, int itemPosition) {
        Log.d(TAG, "getDirection: called");
        Log.d(TAG, "getDirection: itemPosition = " + itemPosition);
        Log.d(TAG, "getDirection: markerTag = " + mMarker.getTag());
        if (mMarker != null) {
            if (itemPosition == (int) mMarker.getTag()) {
                nowGetDirection(event);
            } else {
                setTargetMarker(event, itemPosition);
                nowGetDirection(event);
            }
        }

    }

    private void nowGetDirection(Event event) {
        Log.d(TAG, "nowGetDirection: called");
        mUserLatLng = new LatLng(mUserCurrentLocation.getLatitude(), mUserCurrentLocation.getLongitude());
        mTargetLatLng = new LatLng(event.getEvent_geopoint().getLatitude(), event.getEvent_geopoint().getLongitude());
        mMap.addPolyline(new PolylineOptions().add(mUserLatLng, mTargetLatLng).clickable(true));
        Log.d(TAG, "nowGetDirection: done");
        if (mUserLatLng.latitude > mTargetLatLng.latitude) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(mTargetLatLng, mUserLatLng), 50));
        } else {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(mTargetLatLng, mUserLatLng), 50));
        }
    }


    private void setTargetMarker(Event event, int itemPosition) {
        if (mMarker != null) {
            Log.d(TAG, "onTap: marker not null");
            mMarker.remove(); // todo : this method is not working maybe because map is getting instantiated twice (onCreate & onResume)
            mMarker = null;
            mMap.clear();
        }
        setUserMarkerWithoutUpdatingCamera();
        LatLng latLng = new LatLng(event.getEvent_geopoint().getLatitude(), event.getEvent_geopoint().getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        mMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(event.getEvent_name())
                .snippet(event.getVenue_address()));
        mMarker.setTag(itemPosition);
        mMarker.showInfoWindow();
        Log.d(TAG, "setTargetMarker: done");
    }


    private String newKey = "";

    @Override
    public void onKeyEntered(String key, GeoLocation location) {

        newKey = key;
        String oldKey = "";

        if (mKeyList.size() > 0) {
            oldKey = mKeyList.get(mKeyList.size() - 1);
        }

        Log.d(TAG, "onKeyEntered: called");
        Log.d(TAG, "onKeyEntered: key = " + key);
        Log.d(TAG, "onKeyEntered: newKey = " + newKey);
        Log.d(TAG, "onKeyEntered: oldKey = " + oldKey);

        if (newKey != oldKey) {
            mKeyList.add(key);
        }

        Log.d(TAG, "onKeyEntered: mKeyList.size = " + mKeyList.size());

    }

    @Override
    public void onKeyExited(String s) {
        Log.d(TAG, "onKeyExited: called");
    }

    @Override
    public void onKeyMoved(String key, GeoLocation location) {
        Log.d(TAG, "onKeyMoved: key");
    }


    private boolean mLoopFinished = false;
    private int i = 0;
    private boolean mLoopStarted = false;

    @Override
    public void onGeoQueryReady() {
        Log.d(TAG, "onGeoQueryReady: called ");
        Log.d(TAG, "onGeoQueryReady: mLoopStarted = " + mLoopStarted);

        if (mKeyList.size() == 0) {
            mProgressBarPB.setVisibility(View.GONE);
            mNoEventTV.setVisibility(View.VISIBLE);
        }

        if (!mLoopStarted) {
            for (i = 0; i < mKeyList.size(); i++) {
                mLoopStarted = true;
                Log.d(TAG, "onGeoQueryReady: i = " + i);
                String key = mKeyList.get(i);
                Log.d(TAG, "onGeoQueryReady: key = " + key);
                if (i == mKeyList.size() - 1) {
                    mLoopFinished = true;
                }
                Log.d(TAG, "onGeoQueryReady: loopFinished " + mLoopFinished);

                mDbFirestore.collection("SportsVenues").document(key).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        mVenue = documentSnapshot.toObject(Venue.class);
                        mVenueList.add(mVenue);


                        if (mLoopFinished) {
                            Log.d(TAG, "onSuccess: isTrueNow " + mLoopFinished);

                            Log.d(TAG, "onGeoQueryReady: mVenueList.size() = " + mVenueList.size());

                            VenuesAdapter venuesAdapter = new VenuesAdapter(SportsActivity.this, mVenueList, mUserCurrentLocation, mMap, mProgressBarPB, SportsActivity.this);
                            Log.d(TAG, "onSuccess: adapter called");
                            mSportsRV.setAdapter(venuesAdapter);
                            mProgressBarPB.setVisibility(View.GONE);
                            mSportsRV.setLayoutManager(new LinearLayoutManager(SportsActivity.this));

                            hideSoftKeyboard(SportsActivity.this, mRadiusET);

                        }

                    }
                });

            }
        }

    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d(TAG, "onGeoQueryError: called");
    }

    @Override
    public void onTap(Event event, int viewId, int tappedItemPosition) {
        Log.d(TAG, "onTap: viewId = " + viewId);
        switch (viewId) {
            case R.id.show_on_map_BTN:
                Log.d(TAG, "onTap: getEvent_name() = " + event.getEvent_name());
                setTargetMarker(event, tappedItemPosition);
                break;
            case R.id.get_direction_BTN:
                Log.d(TAG, "onTap: mMarker.getEvent_id() = " + mMarker.getId());
                getDirection(event, mUserCurrentLocation, tappedItemPosition);

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_BTN:
                hideSoftKeyboard(SportsActivity.this, mRadiusET);
                mProgressBarPB.setVisibility(View.VISIBLE);
                mRadius = Integer.parseInt(mRadiusET.getText().toString());

                mLoopStarted = false;
                i = 0;
                mLoopFinished = false;
                displaySportsNearUserLocation(mRadius);
        }
    }

    public void hideSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }


    @Override
    public void onPageLoad(boolean pageLoaded) {
        // does nothing
    }
}
