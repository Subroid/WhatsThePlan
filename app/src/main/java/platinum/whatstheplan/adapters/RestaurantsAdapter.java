package platinum.whatstheplan.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import platinum.whatstheplan.activities.FoodListActivity;
import platinum.whatstheplan.interfaces.RestaurantItemTapListener;
import platinum.whatstheplan.models.Restaurant;
import platinum.whatstheplan.models.UserInformation;

//todo gist this class using naming conventions of model, viewholder etc

public class RestaurantsAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantsAdapter.RestaurantViewHolder> {

    private static final String TAG = "RestaurantsAdapterTag";

    Context mContext;
    UserInformation mUserInformation;
    GoogleMap mMap;
    float[] distanceResults = new float[2];
    Restaurant mRestaurant;

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
//        Log.d(TAG, "onBindViewHolder: userGeopoint " + mUserInformation.getUserLocation().getEvent_geopoint());
        /*Log.d(TAG, "onBindViewHolder: userLatitude " + mUserInformation.getUserLocation().getEvent_geopoint().getLatitude());
        Log.d(TAG, "onBindViewHolder: userLongitude " + mUserInformation.getUserLocation().getEvent_geopoint().getLongitude());*/
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
        holder.show_on_button_BTN.setTag(R.id.TAG_FOR_RESTAURANT, restaurant);
        holder.show_on_button_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        holder.get_direction_BTN.setTag(R.id.TAG_FOR_RESTAURANT, restaurant);
        holder.get_direction_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        holder.get_direction_BTN.setEnabled(false);
        holder.visit_BTN.setTag(R.id.TAG_FOR_RESTAURANT, restaurant);

    }
            private float getDistancBetweenTwoPoints(double lat1, double long1, double lat2, double long2) {
                Location.distanceBetween(lat1, long1, lat1, long2, distanceResults);
                return distanceResults[0];

            }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_restaurant_notused, viewGroup, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(itemView);
        return viewHolder;
    }

    class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView restaurant_name_TV;
        TextView distance_TV;
        ImageView restaurant_image_IV;
        Button show_on_button_BTN;
        Button get_direction_BTN;
        Button visit_BTN;


        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurant_name_TV = itemView.findViewById(R.id.restaurant_name_TV);
            distance_TV = itemView.findViewById(R.id.distance_TV);
            restaurant_image_IV = itemView.findViewById(R.id.restaurant_image_IV);
            show_on_button_BTN = itemView.findViewById(R.id.show_on_map_BTN);
            get_direction_BTN = itemView.findViewById(R.id.get_direction_BTN);
            visit_BTN = itemView.findViewById(R.id.visit_BTN);

            show_on_button_BTN.setOnClickListener(this);
            get_direction_BTN.setEnabled(false);
            get_direction_BTN.setAlpha(0.5f);
            get_direction_BTN.setOnClickListener(this);
            visit_BTN.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_on_map_BTN :
                    Log.d(TAG, "onClick: show_on_map_BTN");
                    setTapListener (view);
                    get_direction_BTN.setEnabled(true);
                    get_direction_BTN.setAlpha(1.0f);
                    break;
                case R.id.get_direction_BTN :
                    Log.d(TAG, "onClick: getdirection_btn");
                    setTapListener(view);
                    break;
                case R.id.visit_BTN :
                    Log.d(TAG, "onClick: visit_btn");
                    Restaurant restaurant = (Restaurant) view.getTag(R.id.TAG_FOR_RESTAURANT);
                    Log.d(TAG, "onClick: restaurant id = " + restaurant.getId());
                    Intent intent = new Intent(mContext, FoodListActivity.class);
                    intent.putExtra("restaurant_name", restaurant_name_TV.getText());
                    intent.putExtra("restaurant_id", restaurant.getId());
                    mContext.startActivity(intent);
                    break;
            }
        }

        public void setTapListener(View view) {
            mRestaurant = (Restaurant) view.getTag(R.id.TAG_FOR_RESTAURANT);
            Log.d(TAG, "setTapListener: restaurant.getEvent_name() = " + mRestaurant.getName());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            RestaurantItemTapListener restaurantItemTapListener = (RestaurantItemTapListener) mContext;
            restaurantItemTapListener.onTap (mRestaurant, viewId, position);
        }
    }
}
