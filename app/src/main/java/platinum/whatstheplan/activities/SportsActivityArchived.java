package platinum.whatstheplan.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPhotoResponse;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;

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
import java.util.Arrays;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.OtherVenuesAdapter;
import platinum.whatstheplan.models.OtherVenue;
import platinum.whatstheplan.models.UserInformation;

import static platinum.whatstheplan.utils.Constants.REQUEST_ERROR_DIALOG_CODE_61;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_PERMISSIONS_CODE_52;
import static platinum.whatstheplan.utils.Constants.REQUEST_LOCATION_SETTINGS_CODE_51;

public class SportsActivityArchived extends FragmentActivity {

    private static final String TAG = "SportsActivityTag";

    private ProgressBar mProgressBarPB;
    private TextView mNoEventTV;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mUserCurrentLocation;
    private LatLng mLastLatLng;
    private boolean mIsGpsEnabled;
    private boolean mLocationPermissionGranted = false;
    private RecyclerView mSportsRV;
    private UserInformation mUserInformation;
    private FirebaseUser mUser;
    private LatLng mUserLatLng;
    private LatLng mTargetLatLng;
    private GeoPoint mQueryCenter;
    private PlacesClient mPlacesClient;
    List<OtherVenue> mListOtherVenue;
    int i = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        Log.d(TAG, "onCreate: called");
        initViewsAndVariables();
        performActions();

    }

    private void initViewsAndVariables() {
        Log.d(TAG, "initViewsAndVariables: called");
        mListOtherVenue = new ArrayList<>();
        mSportsRV = findViewById(R.id.sports_RV);
        mNoEventTV = findViewById(R.id.no_event_TV);
        mProgressBarPB = findViewById(R.id.progressBar);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SportsActivityArchived.this);
        Places.initialize(getApplicationContext(), "AIzaSyA4y9-d-Hl1Qm54HUGlvkRRntcapocFBns");
        mPlacesClient = Places.createClient(SportsActivityArchived.this);

    }

    private void performActions() {
        Log.d(TAG, "performActions: called");
        mSportsRvActions();

    }

    private void mSportsRvActions() {
        Log.d(TAG, "mSportsRvActions: called");
        mSportsRV.setHasFixedSize(true);
        DividerItemDecoration itemDecorator = new DividerItemDecoration
                (SportsActivityArchived.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(SportsActivityArchived.this, R.drawable.divider));
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
//            }
            getUserCurrentLocation();
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

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SportsActivityArchived.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isPlayServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isPlayServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SportsActivityArchived.this, available, REQUEST_ERROR_DIALOG_CODE_61);
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
            ActivityCompat.shouldShowRequestPermissionRationale(SportsActivityArchived.this,
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

    private void initMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocation: if called");
            requestLocationPermissions();
        } else {
            Log.d(TAG, "getUserCurrentLocation: else called");
        }

    }

    private void getUserCurrentLocation() {
        Log.d(TAG, "getUserCurrentLocation: called");
        final Location[] userCurrentLocationResults = {new Location(LocationManager.GPS_PROVIDER)};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getUserCurrentLocation: if called");
            requestLocationPermissions();
        } else {
            Log.d(TAG, "getUserCurrentLocation: else called");
            Task<Location> taskLastLocation = mFusedLocationProviderClient.getLastLocation();
            taskLastLocation.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Log.d(TAG, "onComplete: else if");
                        userCurrentLocationResults[0] = task.getResult();
                        mUserCurrentLocation = userCurrentLocationResults[0];
                        Log.d(TAG, "onComplete: userCurrentLocationResults[0] = " + userCurrentLocationResults[0].getLatitude());
                        displaySportPlacesNearUserLocation();
                    } else {
                        Log.d(TAG, "onComplete: else else");
                    }
                }
            });
        }

    }

    double lat = 19.0255306;
    double lng = 72.8642131;
    private void displaySportPlacesNearUserLocation() {
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                "&rankby=distance&sensor=true" +
                "&types=stadium"+
                "&key=\n" +
                "AIzaSyAvXGr1Kt3gF7Zt1rWI3hUYJcVVtyLB-LE";
        PlacesTask placesTask = new PlacesTask();
        placesTask.execute(placesSearchStr);

    }

    private void requestLocationPermissions() {
        Log.d(TAG, "requestLocationPermissions: called");
        ActivityCompat.requestPermissions(SportsActivityArchived.this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSIONS_CODE_52);
    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... placesUrl) {

            StringBuilder placesBuilder = new StringBuilder();
            for (String placeSearchUrl : placesUrl) {
                try {
                    URL requestUrl = new URL(placeSearchUrl);
                    HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = null;
                        InputStream inputStream = connection.getInputStream();
                        if (inputStream == null) {
                            return "";
                        }
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            placesBuilder.append(line + "\n");
                        }
                        if (placesBuilder.length() == 0) {
                            return "";
                        }
                        Log.d(TAG, "doInBackground: placesBuilder = " + placesBuilder.toString());
                    } else {
                        Log.i(TAG, "doInBackground: Unsuccessful HTTP Response Code: " + responseCode);
                    }
                } catch (MalformedURLException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return placesBuilder.toString();

        }

        @Override
        protected void onPostExecute(String placesResult) {
            super.onPostExecute(placesResult);

            final List<String> listPlaceId = new ArrayList<>();

            try {
                JSONObject placesResultJsonObj = new JSONObject(placesResult);
                JSONArray placesResultJsonArr = placesResultJsonObj.getJSONArray("results");
                for (int i = 0; i < placesResultJsonArr.length(); i++) {

                    JSONObject placeJObj = placesResultJsonArr.getJSONObject(i);
                    final String placeId = placeJObj.getString("place_id");
                    Log.d(TAG, "onPostExecute: placeId = " + placeId);
                    listPlaceId.add(placeId);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            
            for (String placeId :
                    listPlaceId) {
                Log.d(TAG, "onPostExecute: listPlaceId size = " + listPlaceId.size());
                i++;
                Log.d(TAG, "onPostExecute: i = " + i);
                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS);
                FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                mPlacesClient.fetchPlace(placeRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        final Place place = fetchPlaceResponse.getPlace();
                        Log.d(TAG, "onSuccess: placeName = " + place.getName());
                        try {
                            if (place.getPhotoMetadatas().get(0) != null) {
                                PhotoMetadata photoMetadata = place.getPhotoMetadatas().get(0);
                                FetchPhotoRequest fetchPlaceRequest = FetchPhotoRequest.builder(photoMetadata)
                                        .setMaxWidth(500).setMaxHeight(300).build();
                                mPlacesClient.fetchPhoto(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPhotoResponse>() {
                                    @Override
                                    public void onSuccess(FetchPhotoResponse fetchPhotoResponse) {
                                        String venueName = place.getName();
                                        Log.d(TAG, "onSuccess: venueName = " + venueName);
                                        String venueAddress = place.getAddress();
                                        Bitmap venueImage = fetchPhotoResponse.getBitmap();
                                        OtherVenue otherVenue = new OtherVenue(venueName, venueAddress, venueImage);
                                        Log.d(TAG, "onSuccess: adding venue to the list");
                                        mListOtherVenue.add(otherVenue);
                                        if (i == listPlaceId.size()-1) {
                                            OtherVenuesAdapter otherVenuesAdapter = new OtherVenuesAdapter(SportsActivityArchived.this, mListOtherVenue, mProgressBarPB);
                                            Log.d(TAG, "onPostExecute: setting adapter");
                                            Log.d(TAG, "onPostExecute: mListOtherVenue.size = " + mListOtherVenue.size());
                                            mSportsRV.setAdapter(otherVenuesAdapter);
                                            mSportsRV.setLayoutManager(new LinearLayoutManager(SportsActivityArchived.this));
                                        }
                                    }
                                });
                            } else {
                                String venueName = place.getName();
                                Log.d(TAG, "onSuccess: venueName = " + venueName);
                                String venueAddress = place.getAddress();
                                Bitmap venueImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.venues);
                                OtherVenue otherVenue = new OtherVenue(venueName, venueAddress, venueImage);
                                Log.d(TAG, "onSuccess: adding venue to the list");
                                mListOtherVenue.add(otherVenue);
                                if (i == listPlaceId.size()-1) {
                                    OtherVenuesAdapter otherVenuesAdapter = new OtherVenuesAdapter(SportsActivityArchived.this, mListOtherVenue, mProgressBarPB);
                                    Log.d(TAG, "onPostExecute: setting adapter");
                                    Log.d(TAG, "onPostExecute: mListOtherVenue.size = " + mListOtherVenue.size());
                                    mSportsRV.setAdapter(otherVenuesAdapter);
                                    mSportsRV.setLayoutManager(new LinearLayoutManager(SportsActivityArchived.this));
                                }

                            }
                        } catch (NullPointerException e) {
                            Log.d(TAG, "onSuccess: catch = " + e.getMessage());
                        } finally {
                            Log.d(TAG, "onSuccess: finally");
                            String venueName = place.getName();
                            Log.d(TAG, "onSuccess: venueName = " + venueName);
                            String venueAddress = place.getAddress();
                            Bitmap venueImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.venues);
                            OtherVenue otherVenue = new OtherVenue(venueName, venueAddress, venueImage);
                            Log.d(TAG, "onSuccess: adding venue to the list");
                            mListOtherVenue.add(otherVenue);
                            if (i == listPlaceId.size()-1) {
                                OtherVenuesAdapter otherVenuesAdapter = new OtherVenuesAdapter(SportsActivityArchived.this, mListOtherVenue, mProgressBarPB);
                                Log.d(TAG, "onPostExecute: setting adapter");
                                Log.d(TAG, "onPostExecute: mListOtherVenue.size = " + mListOtherVenue.size());
                                mSportsRV.setAdapter(otherVenuesAdapter);
                                mSportsRV.setLayoutManager(new LinearLayoutManager(SportsActivityArchived.this));
                            }
                        }

                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: e = " + e.toString());
                            }
                        });

        }

        }

    }


}
