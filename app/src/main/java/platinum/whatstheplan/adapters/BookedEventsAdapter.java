package platinum.whatstheplan.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.interfaces.EventItemTapListener;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.Guest;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.utils.BookingDbHandler;

public class BookedEventsAdapter extends RecyclerView.Adapter<BookedEventsAdapter.EventViewHolder> {

    private static final String TAG = "PartiesQueryAdapterTag";
    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();
    private List<RestaurantVenue> mRestaurantVenueList = new ArrayList<>();
    private Event mEvent;
    private RestaurantVenue mRestaurantVenue;
    private ProgressBar mProgressBar;
    private boolean mUseSecondConstructor;

    float[] distanceResults = new float[2];

    public BookedEventsAdapter() {
    }

    public BookedEventsAdapter(Context context, List<Event> eventList, ProgressBar progressBar) {
        mContext = context;
        mEventList = eventList;
        mProgressBar = progressBar;
    }

    public BookedEventsAdapter(Context context, List<RestaurantVenue> restaurantVenueList, ProgressBar progressBar, boolean useThisConstructor) {
        mContext = context;
        mRestaurantVenueList = restaurantVenueList;
        mProgressBar = progressBar;
        mUseSecondConstructor = useThisConstructor;
    }

    EventViewHolder viewHolder;
    View itemView;
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (mUseSecondConstructor) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_bookedrestaurant, viewGroup, false);
        } else {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_bookedevent, viewGroup, false);
        }
        viewHolder = new EventViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int position) {

        if (mUseSecondConstructor) {
            mRestaurantVenue = mRestaurantVenueList.get(position);

            eventViewHolder.venue_name_TV.setText(mRestaurantVenue.getVenue_name());
            eventViewHolder.venue_address_TV.setText(mRestaurantVenue.getVenue_address());

            Glide.with(mContext).load(Uri.parse(mRestaurantVenue.getVenue_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_image_IV);
            Glide.with(mContext).load(Uri.parse(mRestaurantVenue.getVenue_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_layout_bg_IV);
            mProgressBar.setVisibility(View.INVISIBLE);

        } else {
            mEvent = mEventList.get(position);

            eventViewHolder.event_name_TV.setText(mEvent.getEvent_name());
            eventViewHolder.venue_name_TV.setText(mEvent.getVenue_name());
            eventViewHolder.venue_address_TV.setText(mEvent.getVenue_address());
            eventViewHolder.event_date_TV.setText(mEvent.getEvent_date());
            eventViewHolder.event_time_TV.setText(mEvent.getEvent_time());

            Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_image_IV);
            Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_layout_bg_IV);
            mProgressBar.setVisibility(View.INVISIBLE);

        }


    }


    @Override
    public int getItemCount() {
        if (mUseSecondConstructor) {
            return mRestaurantVenueList.size();
        } else {
            return mEventList.size();
        }
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
