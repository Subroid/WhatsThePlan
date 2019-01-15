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
import platinum.whatstheplan.utils.BookingDbHandler;

public class BookedEventsAdapter extends RecyclerView.Adapter<BookedEventsAdapter.EventViewHolder> {

    private static final String TAG = "PartiesQueryAdapterTag";
    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();
    private Event mEvent;
    private ProgressBar mProgressBar;

    float[] distanceResults = new float[2];

    public BookedEventsAdapter() {
    }

    public BookedEventsAdapter(Context context, List<Event> eventList, ProgressBar progressBar) {
        mContext = context;
        mEventList = eventList;
        mProgressBar = progressBar;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_bookedevent, viewGroup, false);
        EventViewHolder viewHolder = new EventViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int position) {

        mEvent = mEventList.get(position);

        eventViewHolder.event_name_TV.setText(mEvent.getEvent_name());
        eventViewHolder.venue_name_TV.setText(mEvent.getVenue_name());
        eventViewHolder.venue_address_TV.setText(mEvent.getVenue_address());
        eventViewHolder.event_date_TV.setText(mEvent.getEvent_date());
        eventViewHolder.event_time_TV.setText(mEvent.getEvent_time());

        Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_image_IV);
        mProgressBar.setVisibility(View.INVISIBLE);

    }


    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView event_name_TV;
        private TextView venue_name_TV;
        private TextView venue_address_TV;
        private TextView event_date_TV;
        private TextView event_time_TV;
        private ImageView event_image_IV;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            event_name_TV = itemView.findViewById(R.id.event_name_TV);
            venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
            venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
            event_date_TV = itemView.findViewById(R.id.event_date_TV);
            event_time_TV = itemView.findViewById(R.id.event_time_TV);
            event_image_IV = itemView.findViewById(R.id.event_image_IV);

        }
    }
}
