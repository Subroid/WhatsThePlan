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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.Venue;
import platinum.whatstheplan.utils.BookingDbHandler;

public class BookedEventsAdapter extends FirestoreRecyclerAdapter<Event, BookedEventsAdapter.EventViewHolder>  {

    private static final String TAG = "BookedAdapterTag";
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
    public BookedEventsAdapter(@NonNull FirestoreRecyclerOptions<Event> options, Context context, ProgressBar progressBar) {
        super(options);
        mContext = context;
        mProgressBar = progressBar;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_bookedevent, viewGroup, false);
        viewHolder = new EventViewHolder(itemView);
        return viewHolder;
    }


    @Override
    protected void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull Event event) {
//        if (mUseSecondConstructor) {
        Log.d(TAG, "onBindViewHolder: event name = " + event.getEvent_name());
        Log.d(TAG, "onBindViewHolder: venue name = " + event.getVenue_name());
        Log.d(TAG, "onBindViewHolder: venue address = " + event.getVenue_address());
        holder.event_name_TV.setText(event.getEvent_name());
        holder.venue_name_TV.setText(event.getVenue_name());
        holder.event_address_TV.setText(event.getVenue_address());

        Glide.with(mContext).load(Uri.parse(event.getEvent_image())).apply(new RequestOptions().fitCenter()).into(holder.event_image_IV);
        Glide.with(mContext).load(Uri.parse(event.getEvent_image())).apply(new RequestOptions().fitCenter()).into(holder.event_layout_bg_IV);
        mProgressBar.setVisibility(View.INVISIBLE);

    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView event_name_TV;
        private TextView venue_name_TV;
        private TextView event_address_TV;
        private ImageView event_image_IV;
        private ImageView event_layout_bg_IV;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            event_name_TV = itemView.findViewById(R.id.event_name_TV);
            venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
            event_address_TV = itemView.findViewById(R.id.venue_address_TV);
            event_image_IV = itemView.findViewById(R.id.event_image_IV);
            event_layout_bg_IV = itemView.findViewById(R.id.event_layout_bg_IV);

            }

        }
    }
