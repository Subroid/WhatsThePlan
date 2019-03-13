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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.PaymentActivity;
import platinum.whatstheplan.interfaces.EventItemTapListener;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.Guest;
import platinum.whatstheplan.utils.BookingDbHandler;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private static final String TAG = "EventsAdapter";
    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();
    private Event mEvent;
    private Location mUserCurrentLocation;
    private GoogleMap mMap;
    private ProgressBar mProgressBar;

    float[] distanceResults = new float[2];

    public EventsAdapter() {
    }

    public EventsAdapter(Context context, List<Event> eventList, Location userCurrentLocation, GoogleMap map, ProgressBar progressBar) {
        mContext = context;
        mEventList = eventList;
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
        mEvent = mEventList.get(position);

        eventViewHolder.event_name_TV.setText(mEvent.getEvent_name());

        float distance = getDistancBetweenTwoPoints(
                mUserCurrentLocation.getLatitude(),
                mUserCurrentLocation.getLongitude(),
                mEvent.getEvent_geopoint().getLatitude(),
                mEvent.getEvent_geopoint().getLongitude());
        distance = distance / 1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        eventViewHolder.venue_name_TV.setText("Venue : " + mEvent.getVenue_name() + " (" +String.valueOf(decimalFormat.format(distance)) + " km)" );
        eventViewHolder.venue_address_TV.setText("Address : " + mEvent.getVenue_address());
        eventViewHolder.event_tickets_TV.setText("Tickets : " + String.valueOf(mEvent.getEvent_tickets()));
        eventViewHolder.event_date_TV.setText(mEvent.getEvent_date());
        eventViewHolder.event_time_TV.setText(mEvent.getEvent_time());

        Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_image_IV);
        Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(eventViewHolder.event_layout_bg_IV);
        LatLng eventLatLng = new LatLng(mEvent.getEvent_geopoint().getLatitude(), mEvent.getEvent_geopoint().getLongitude());
        mMap.addMarker(new MarkerOptions().position(eventLatLng));
        eventViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);
        eventViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        eventViewHolder.booking_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);
        eventViewHolder.booking_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        eventViewHolder.booking_BTN.setTag(R.id.TAG_FOR_EVENT, mEvent);
        mProgressBar.setVisibility(View.GONE);
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
        private ImageView event_layout_bg_IV;

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
            event_layout_bg_IV = itemView.findViewById(R.id.event_layout_bg_IV);

            show_on_map_BTN.setOnClickListener(this);
            booking_BTN.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_on_map_BTN:
                    Log.d(TAG, "onClick: show_on_map_BTN");
                    setTapListener(view);
                    break;
                case R.id.booking_BTN:
                    mEvent = (Event) view.getTag(R.id.TAG_FOR_EVENT);
                    Log.d(TAG, "onClick: event date = " + mEvent.getEvent_date().replaceAll("/", "-"));
                    mEvent.setEvent_date(mEvent.getEvent_date().replaceAll("/", "-"));
                    showConfrimationDialog (mEvent);
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
                            mProgressBar.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(mContext, PaymentActivity.class);
                            intent.putExtra("event", event);
                            mContext.startActivity(intent);
//                            saveEventBookingLocally (event);
//                            saveEventBookingRemotely (event);
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

            BookingDbHandler bookingDbHandler = new BookingDbHandler(mContext);
            bookingDbHandler.addEvent(event);
        }

        private void saveEventBookingRemotely(final Event event) {
            final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();

            final CollectionReference dbRefEventType = dbFirestore.collection(event.getEvent_type());
            final DocumentReference dbRefEvent = dbRefEventType.document(event.getEvent_id());
            dbRefEvent.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    final Event gotEvent =  documentSnapshot.toObject(Event.class);
                    final String adminId = gotEvent.getAdmin_id();
                    final int gotEvent_eventTickets = gotEvent.getEvent_tickets();
                    dbRefEvent.update("event_tickets", gotEvent_eventTickets-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            final DocumentReference dbRefAdmin = dbFirestore.collection("Admins")
                                    .document(adminId);
                            DocumentReference dbRefEvent = dbRefAdmin.collection("Events")
                                    .document(event.getEvent_id());
                            dbRefEvent.update("event_tickets", gotEvent_eventTickets-1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    CollectionReference dbRefBookingsAdminSide = dbRefAdmin.collection("Bookings");
                                    CollectionReference dbRefGuests = dbRefBookingsAdminSide.document(event.getEvent_id()).collection("Guests");
                                    dbRefGuests.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(getGuestInstance())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
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
                            });

                        }
                    });


                }

                private Guest getGuestInstance() {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Guest guest =  new Guest(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid());
                    return guest;
                }

            });
        }

        public void setTapListener(View view) {
            mEvent = (Event) view.getTag(R.id.TAG_FOR_EVENT);
            Log.d(TAG, "setTapListener: getEvent_name() = " + mEvent.getEvent_name());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            EventItemTapListener eventItemTapListener = (EventItemTapListener) mContext;
            eventItemTapListener.onTap(mEvent, viewId, position);
        }
    }
}
