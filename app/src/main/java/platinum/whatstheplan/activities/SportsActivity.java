package platinum.whatstheplan.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;

import platinum.whatstheplan.R;

public class SportsActivity extends AppCompatActivity {

    private final String TAG = "SportsActivity";
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        initViewsAndVariables ();
        performActions ();
    }

    private void performActions() {
        performMapViewActions ();
    }

    private void performMapViewActions() {
        mapView.getMapAsync(getOnMapReadyCallbackInstance ());
    }

    private OnMapReadyCallback getOnMapReadyCallbackInstance() {
        OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady: googleMap.isMyLocationEnabled() = "
                                                     + googleMap.isMyLocationEnabled());

            }
        };
        return onMapReadyCallback;
    }

    private void initViewsAndVariables() {
        mapView = findViewById(R.id.sa_mapView);

    }

}
