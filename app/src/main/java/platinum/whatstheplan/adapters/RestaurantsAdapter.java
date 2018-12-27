package platinum.whatstheplan.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.Restaurant;
import platinum.whatstheplan.models.UserInformation;

//todo gist this class using naming conventions of model, viewholder etc

public class RestaurantsAdapter extends FirestoreRecyclerAdapter<Restaurant, RestaurantsAdapter.RestaurantViewHolder> {

    private static final String TAG = "RestaurantsAdapterTag";

    Context mContext;
    UserInformation mUserInformation;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public RestaurantsAdapter(@NonNull FirestoreRecyclerOptions<Restaurant> options, Context context, UserInformation userInformation) {
        super(options);
        Log.d(TAG, "RestaurantsAdapter: costructer called");
        mContext = context;
        mUserInformation = userInformation;

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
        holder.restaurant_address_TV.setText(restaurant.getAddress());

        int distanceInt = restaurant.getGeoLocation().compareTo(mUserInformation.getUserLocation().getGeoPoint());
        if (distanceInt < 0) {
           Math.abs(distanceInt);
        }
        Log.d(TAG, "onBindViewHolder: distanceInt = " + distanceInt);
        holder.distance_TV.setText(String.valueOf(distanceInt) + " km");

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
        TextView restaurant_address_TV;
        TextView distance_TV;


        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurant_name_TV = itemView.findViewById(R.id.restaurant_name_TV);
            restaurant_address_TV = itemView.findViewById(R.id.restaurant_address_TV);
            distance_TV = itemView.findViewById(R.id.distance_TV);
        }
    }

}
