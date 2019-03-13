package platinum.whatstheplan.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.RestaurantVenue;

public class BookedRestaurantsAdapter extends FirestoreRecyclerAdapter<RestaurantVenue, BookedRestaurantsAdapter.EventViewHolder>  {

    private static final String TAG = "BookedRstnAdapterTag";
    private Context mContext;
    private ProgressBar mProgressBar;
    EventViewHolder viewHolder;
    View itemView;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookedRestaurantsAdapter(@NonNull FirestoreRecyclerOptions<RestaurantVenue> options, Context context, ProgressBar progressBar) {
        super(options);
        mContext = context;
        mProgressBar = progressBar;
        Log.d(TAG, "BookedRestaurantsAdapter: called");
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_bookedrestaurant, viewGroup, false);
        viewHolder = new EventViewHolder(itemView);
        return viewHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull RestaurantVenue venue) {
        Log.d(TAG, "onBindViewHolder: restaurant name = " + venue.getVenue_name());
        holder.venue_name_TV.setText(venue.getVenue_name());
        holder.venue_address_TV.setText(venue.getVenue_address());

        Glide.with(mContext).load(Uri.parse(venue.getVenue_image())).apply(new RequestOptions().fitCenter()).into(holder.event_image_IV);
        Glide.with(mContext).load(Uri.parse(venue.getVenue_image())).apply(new RequestOptions().fitCenter()).into(holder.event_layout_bg_IV);
        mProgressBar.setVisibility(View.INVISIBLE);

    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView venue_name_TV;
        private TextView venue_address_TV;
        private ImageView event_image_IV;
        private ImageView event_layout_bg_IV;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
            venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
            event_image_IV = itemView.findViewById(R.id.venue_image_IV);
            event_layout_bg_IV = itemView.findViewById(R.id.venue_layout_bg_IV);

        }
    }
}
