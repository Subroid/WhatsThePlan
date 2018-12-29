package platinum.whatstheplan.adapters;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.Restaurant;
import platinum.whatstheplan.models.UserInformation;

//todo gist this class using naming conventions of model, viewholder etc

public class RestaurantsAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantsAdapter.RestaurantViewHolder> {

    private static final String TAG = "RestaurantsAdapterTag";

    Context mContext;
    UserInformation mUserInformation;
    GoogleMap mMap;
    float[] distanceResults = new float[2];

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param map
     */
    public RestaurantsAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context, UserInformation userInformation, GoogleMap map) {
        super(options);
        Log.d(TAG, "RestaurantsAdapter: costructer called");
        mContext = context;
        mUserInformation = userInformation;
        mMap = map;
    }

    @Override
    protected void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position, @NonNull Restaurant restaurant) {

        Log.d(TAG, "onBindViewHolder: name = " + restaurant.getName());
        Log.d(TAG, "onBindViewHolder: address = " + restaurant.getAddress());
//        Log.d(TAG, "onBindViewHolder: userGeopoint " + mUserInformation.getUserLocation().getGeoLocation());
        /*Log.d(TAG, "onBindViewHolder: userLatitude " + mUserInformation.getUserLocation().getGeoLocation().getLatitude());
        Log.d(TAG, "onBindViewHolder: userLongitude " + mUserInformation.getUserLocation().getGeoLocation().getLongitude());*/
        Log.d(TAG, "onBindViewHolder: restaurantGeopoint " + restaurant.getGeoLocation());
        Log.d(TAG, "onBindViewHolder: user geopoint = " + mUserInformation.getUserLocation().getGeoPoint());

        holder.restaurant_name_TV.setText(restaurant.getName());

            float distance = getDistancBetweenTwoPoints(
                    mUserInformation.getUserLocation().getGeoPoint().getLatitude(),
                    mUserInformation.getUserLocation().getGeoPoint().getLongitude(),
                    restaurant.getGeoLocation().getLatitude(),
                    restaurant.getGeoLocation().getLongitude());
            distance = distance/1000;
            DecimalFormat decimalFormat = new DecimalFormat("#.00");


        holder.distance_TV.setText(String.valueOf(decimalFormat.format(distance)) + " km");
        Glide.with(mContext).load(Uri.parse(restaurant.getImage())).into(holder.restaurant_image_IV);
        LatLng restaurantLatLng = new LatLng(restaurant.getGeoLocation().getLatitude(), restaurant.getGeoLocation().getLongitude());
        mMap.addMarker(new MarkerOptions().position(restaurantLatLng));

    }

            private float getDistancBetweenTwoPoints(double lat1, double long1, double lat2, double long2) {
                Location.distanceBetween(lat1, long1, lat1, long2, distanceResults);
                return distanceResults[0];

            }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_restaurant, viewGroup, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(itemView);
        return viewHolder;
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView restaurant_name_TV;
        TextView distance_TV;
        ImageView restaurant_image_IV;


        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurant_name_TV = itemView.findViewById(R.id.restaurant_name_TV);
            distance_TV = itemView.findViewById(R.id.distance_TV);
            restaurant_image_IV = itemView.findViewById(R.id.restaurant_image_IV);
        }
    }

}
