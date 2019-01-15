package platinum.whatstheplan.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.w3c.dom.Document;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.FoodListActivity;
import platinum.whatstheplan.interfaces.EventItemTapListener;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.Guest;
import platinum.whatstheplan.models.Party;
import platinum.whatstheplan.models.UserProfile;
import platinum.whatstheplan.utils.BookingDbHandler;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private static final String TAG = "PartiesQueryAdapterTag";
    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();
    private Event mEvent;
    private Location mUserCurrentLocation;
    GoogleMap mMap;
    ProgressBar mProgressBar;

    float[] distanceResults = new float[2];

    public EventAdapter() {
    }

    public EventAdapter(Context context, List<Event> eventList, Event event, Location userCurrentLocation, GoogleMap map, ProgressBar progressBar) {
        mContext = context;
        mEventList = eventList;
        mEvent = event;
        mUserCurrentLocation = userCurrentLocation;
        mMap = map;
        mProgressBar = progressBar;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_event, viewGroup, false);
        EventViewHolder viewHolder = new EventViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder eventViewHolder, int position) {
        eventViewHolder.event_name_TV.setText(mEvent.getEvent_name());

        float distance = getDistancBetweenTwoPoints(
                mUserCurrentLocation.getLatitude(),
                mUserCurrentLocation.getLongitude(),
                mEvent.getEvent_geopoint().getLatitude(),
                mEvent.getEvent_geopoint().getLongitude());
        distance = distance / 1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        eventViewHolder.venue_name_TV.setText(mEvent.getVenue_name() + " (" +String.valueOf(decimalFormat.format(distance)) + " km)" );
        eventViewHolder.venue_address_TV.setText(mEvent.getVenue_address());
        eventViewHolder.event_date_TV.setText(mEvent.getEvent_date());
        eventViewHolder.event_time_TV.setText(mEvent.getEvent_time());

        Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_image_IV);
        LatLng PartyLatLng = new LatLng(mEvent.getEvent_geopoint().getLatitude(), mEvent.getEvent_geopoint().getLongitude());
        mMap.addMarker(new MarkerOptions().position(PartyLatLng));
        eventViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);
        eventViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        eventViewHolder.get_direction_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);
        eventViewHolder.get_direction_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        eventViewHolder.get_direction_BTN.setEnabled(false);
        eventViewHolder.booking_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);
        eventViewHolder.booking_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        eventViewHolder.booking_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);

    }

    private float getDistancBetweenTwoPoints(double lat1, double long1, double lat2, double long2) {
        Location.distanceBetween(lat1, long1, lat2, long2, distanceResults);
        return distanceResults[0];

    }


    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView event_name_TV;
        private TextView venue_name_TV;
        private TextView venue_address_TV;
        private TextView event_date_TV;
        private TextView event_time_TV;
        private ImageView event_image_IV;
        private TextView event_tickets_TV;
        private Button show_on_map_BTN;
        private Button booking_BTN;
        private Button get_direction_BTN;


        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            event_name_TV = itemView.findViewById(R.id.event_name_TV);
            venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
            venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
            event_date_TV = itemView.findViewById(R.id.event_date_TV);
            event_time_TV = itemView.findViewById(R.id.event_time_TV);
            event_image_IV = itemView.findViewById(R.id.event_image_IV);
            event_tickets_TV = itemView.findViewById(R.id.event_tickets_TV);
            show_on_map_BTN = itemView.findViewById(R.id.show_on_map_BTN);
            booking_BTN = itemView.findViewById(R.id.booking_BTN);
            get_direction_BTN = itemView.findViewById(R.id.get_direction_BTN);

            show_on_map_BTN.setOnClickListener(this);
            get_direction_BTN.setEnabled(false);
            get_direction_BTN.setAlpha(0.5f);
            get_direction_BTN.setOnClickListener(this);
            booking_BTN.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_on_map_BTN:
                    Log.d(TAG, "onClick: show_on_map_BTN");
                    setTapListener(view);
                    get_direction_BTN.setEnabled(true);
                    get_direction_BTN.setAlpha(1.0f);
                    break;
                case R.id.get_direction_BTN:
                    Log.d(TAG, "onClick: getdirection_btn");
                    setTapListener(view);
                    break;
                case R.id.booking_BTN:
                    mEvent = (Event) view.getTag(R.id.TAG_FOR_EVENT);
                    showConfrimationDialog (mEvent);

                    //todo save event related data in local database
                    //todo send event related data to remote database
                    //todo tickets number reduction
                    break;
            }
        }

        private void showConfrimationDialog(final Event event) {
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle(event.getEvent_name())
                    .setMessage("Do you want to book this event?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //todo save event locally and remotely
                            saveEventBookingLocally (event);
                            saveEventBookingRemotely (event);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //todo do nothing
                        }
                    })
                    .create();
            dialog.show();
        }

        private void saveEventBookingLocally(Event event) {
            mProgressBar.setVisibility(View.VISIBLE);
            BookingDbHandler bookingDbHandler = new BookingDbHandler(mContext);
            bookingDbHandler.addEvent(event);
        }

        private void saveEventBookingRemotely(final Event event) {
            final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();

            CollectionReference dbRefParties = dbFirestore.collection("Parties");
            dbRefParties.document(event.getEvent_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Event gotEvent =  documentSnapshot.toObject(Event.class);
                    String adminId = gotEvent.getAdmin_id();
                    Log.d(TAG, "onSuccess: adminId = " + adminId);

                    CollectionReference dbRefBookingsAdminSide = dbFirestore.collection("Admins")
                            .document(adminId)
                            .collection("Bookings");
                    dbRefBookingsAdminSide.document(event.getEvent_id()).set(getGuestInstance()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CollectionReference dbRefBookingsClientSide = dbFirestore.collection("Users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("Bookings");
                            dbRefBookingsClientSide.document(event.getEvent_id()).set(event).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    FancyToast.makeText(mContext, "Event booked successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                }
                            });
                        }
                    });
                }

                private Guest getGuestInstance() {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Guest guest =  new Guest(currentUser.getDisplayName(), currentUser.getEmail());
                    return guest;
                }

            });
        }

        public void setTapListener(View view) {
            mEvent = (Event) view.getTag(R.id.TAG_FOR_EVENT);
            Log.d(TAG, "setTapListener: Party.getEvent_name() = " + mEvent.getEvent_name());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            EventItemTapListener eventItemTapListener = (EventItemTapListener) mContext;
            eventItemTapListener.onTap(mEvent, viewId, position);
        }
    }
}
