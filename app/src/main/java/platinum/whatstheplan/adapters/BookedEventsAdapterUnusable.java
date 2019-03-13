package platinum.whatstheplan.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.RestaurantVenue;

public class BookedEventsAdapterUnusable extends FirestoreRecyclerAdapter {

    private static final String TAG = "PartiesQueryAdapterTag";
    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();
    private List<RestaurantVenue> mRestaurantVenueList = new ArrayList<>();
    private Event mEvent;
    private RestaurantVenue mRestaurantVenue;
    private ProgressBar mProgressBar;
    private boolean mUseSecondConstructor;

    float[] distanceResults = new float[2];

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookedEventsAdapterUnusable(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Object model) {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView event_name_TV;
        private TextView venue_name_TV;
        private TextView venue_address_TV;
        private TextView event_date_TV;
        private TextView event_time_TV;
        private ImageView event_image_IV;
        private ImageView event_layout_bg_IV;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            if (mUseSecondConstructor) {
                venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
                venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
//                event_date_TV = itemView.findViewById(R.id.event_date_TV);
//                event_time_TV = itemView.findViewById(R.id.event_time_TV);
                event_image_IV = itemView.findViewById(R.id.venue_image_IV);
                event_layout_bg_IV = itemView.findViewById(R.id.venue_layout_bg_IV);
            } else {
                event_name_TV = itemView.findViewById(R.id.event_name_TV);
                venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
                venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
                event_date_TV = itemView.findViewById(R.id.event_date_TV);
                event_time_TV = itemView.findViewById(R.id.event_time_TV);
                event_image_IV = itemView.findViewById(R.id.event_image_IV);
                event_layout_bg_IV = itemView.findViewById(R.id.event_layout_bg_IV);
            }

        }
    }
}
