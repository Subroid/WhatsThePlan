package platinum.whatstheplan.adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.PartyEventsActivity;
import platinum.whatstheplan.activities.PaymentActivity;
import platinum.whatstheplan.interfaces.RestaurantVenueItemTapListener;
import platinum.whatstheplan.models.Guest;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.utils.BookingDbHandler;

public class RestaurantVenuesAdapter extends RecyclerView.Adapter<RestaurantVenuesAdapter.VenueViewHolder> {

    private static final String TAG = "VenuesAdapter";
    private Context mContext;
    private List<RestaurantVenue> mVenueList = new ArrayList<>();
    private RestaurantVenue mVenue;
    private RestaurantVenue mRestaurantVenue;
    private Location mUserCurrentLocation;
    private GoogleMap mMap;
    private ProgressBar mProgressBar;

    float[] distanceResults = new float[2];


    public RestaurantVenuesAdapter() {
    }

    public RestaurantVenuesAdapter(Context context, List<RestaurantVenue> venueList, Location userCurrentLocation, GoogleMap map, ProgressBar progressBar) {
        mContext = context;
        mVenueList = venueList;
        mUserCurrentLocation = userCurrentLocation;
        mMap = map;
        mProgressBar = progressBar;
    }


    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_restaurant_venue, viewGroup, false);
        VenueViewHolder viewHolder = new VenueViewHolder(itemView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RestaurantVenuesAdapter.VenueViewHolder venueViewHolder, int position) {
        mVenue = mVenueList.get(position);

        venueViewHolder.venue_name_TV.setText(mVenue.getVenue_name());
        Log.d(TAG, "onBindViewHolder: venue name " + mVenue.getVenue_name());
        float distance = getDistancBetweenTwoPoints(
                mUserCurrentLocation.getLatitude(),
                mUserCurrentLocation.getLongitude(),
                mVenue.getVenue_geopoint().getLatitude(),
                mVenue.getVenue_geopoint().getLongitude());
        distance = distance / 1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        venueViewHolder.venue_name_TV.setText("Venue : " + mVenue.getVenue_name() + " (" +String.valueOf(decimalFormat.format(distance)) + " km)" );
        venueViewHolder.venue_address_TV.setText("Address : " + mVenue.getVenue_address());

       venueViewHolder.venue_type_TV.setText("Type : \n" + mVenue.getVenue_type());
       venueViewHolder.venue_main_event_TV.setText("Main Event : \n" + mVenue.getVenue_main_event());
       venueViewHolder.venue_sub_events_TV.setText("Sub Events : " + mVenue.getVenue_sub_events());
       venueViewHolder.show_events_BTN.setTag(mVenue.getVenue_id());

        Glide.with(mContext).load(Uri.parse(mVenue.getVenue_image())).apply(new RequestOptions().fitCenter()).into(venueViewHolder.venue_image_IV);
        Glide.with(mContext).load(Uri.parse(mVenue.getVenue_image())).apply(new RequestOptions().fitCenter()).into(venueViewHolder.venue_layout_bg_IV);
        LatLng venueLatLng = new LatLng(mVenue.getVenue_geopoint().getLatitude(), mVenue.getVenue_geopoint().getLongitude());
        mMap.addMarker(new MarkerOptions().position(venueLatLng));
        Log.d(TAG, "onBindViewHolder: venueid : " + mVenue.getVenue_id());
        venueViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_EVENT, mVenue);
        venueViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        venueViewHolder.booking_BTN.setTag(R.id.TAG_FOR_EVENT, mVenue);
        venueViewHolder.booking_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        mProgressBar.setVisibility(View.GONE);
    }


    private float getDistancBetweenTwoPoints(double lat1, double long1, double lat2, double long2) {
        Location.distanceBetween(lat1, long1, lat2, long2, distanceResults);
        return distanceResults[0];

    }


    @Override
    public int getItemCount() {
        return mVenueList.size();
    }


    class VenueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

        private TextView venue_name_TV;
        private TextView venue_type_TV;
        private TextView venue_main_event_TV;
        private TextView venue_sub_events_TV;
        private TextView venue_address_TV;
        private ImageView venue_image_IV;
        private ImageView venue_layout_bg_IV;
        private Button show_on_map_BTN;
        private Button show_events_BTN;
        private Button booking_BTN;
        private String mBookingDate;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
            venue_type_TV = itemView.findViewById(R.id.venue_type_TV);
            venue_main_event_TV = itemView.findViewById(R.id.venue_main_event_TV);
            venue_sub_events_TV = itemView.findViewById(R.id.venue_sub_events_TV);
            venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
            venue_image_IV = itemView.findViewById(R.id.venue_image_IV);
            venue_layout_bg_IV = itemView.findViewById(R.id.venue_layout_bg_IV);
            show_on_map_BTN = itemView.findViewById(R.id.show_on_map_BTN);
            show_events_BTN = itemView.findViewById(R.id.show_events_BTN);
            booking_BTN = itemView.findViewById(R.id.booking_BTN);

            show_on_map_BTN.setOnClickListener(this);
            show_events_BTN.setOnClickListener(this);
            booking_BTN.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_on_map_BTN:
                    Log.d(TAG, "onClick: show_on_map_BTN");
                    setTapListener(view);
                    break;
                case R.id.show_events_BTN:
                    Log.d(TAG, "onClick: show_events_BTN");
                    String venueId = (String) show_events_BTN.getTag();
                    navigateToActivity (mContext, PartyEventsActivity.class);
                    break;
                case R.id.booking_BTN:
                    // date picker then time picker
                    // book event locally with retrieved date and time
                    // book event remotely with retrieved date and time
                    mRestaurantVenue = (RestaurantVenue) view.getTag(R.id.TAG_FOR_EVENT);
                    Log.d(TAG, "onClick: venueid : " + mRestaurantVenue.getVenue_id());
                    showDatePickerDialog ();
//                    showTimePickerDialog ();
//                    showConfrimationDialog (mVenue);
                    break;
            }
        }

        private void showDatePickerDialog() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, this, year, month, day);

            datePickerDialog.show();
        }

            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1+1;
//                mEventDateET.setText(i2 + "/" + i1 + "/" + i);
                mBookingDate = i2 + "/" + i1 + "/" + i;
                mRestaurantVenue.setVenue_date(mBookingDate);
                showConfrimationDialog (mRestaurantVenue);
            }


        private void showConfrimationDialog(final RestaurantVenue venue) {
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle(venue.getVenue_name())
                    .setMessage("Do you want to book this Restaurant?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mProgressBar.setVisibility(View.VISIBLE);
                            Intent intent = new Intent(mContext, PaymentActivity.class);
                            intent.putExtra("venue", venue);
                            mContext.startActivity(intent);
//                            saveVenueBookingLocally (venue);
//                            saveVenueBookingRemotely (venue);
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

        private void navigateToActivity(Context mContext, Class classname) {
            Intent intent = new Intent(mContext, classname);
            mContext.startActivity(intent);
        }


        private void saveVenueBookingLocally(RestaurantVenue venue) {

            BookingDbHandler bookingDbHandler = new BookingDbHandler(mContext);
            bookingDbHandler.addRestaurantVenue(venue);
        }

        private void saveVenueBookingRemotely(final RestaurantVenue venue) {
            final FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();

            final CollectionReference dbRefVenueType = dbFirestore.collection("Foods DrinksVenues");
            Log.d(TAG, "saveVenueBookingRemotely: path : " + dbRefVenueType.getPath());
            Log.d(TAG, "saveVenueBookingRemotely: venueid : " + venue.getVenue_id());
            Log.d(TAG, "saveVenueBookingRemotely: adminid : " + venue.getAdmin_id());
            Log.d(TAG, "saveVenueBookingRemotely: dbRefVenue Path : " + dbRefVenueType.document(venue.getVenue_id()).toString());
            final DocumentReference dbRefVenue = dbRefVenueType.document(venue.getVenue_id());
            dbRefVenue.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    final RestaurantVenue gotVenue =  documentSnapshot.toObject(RestaurantVenue.class);
//                    final String adminId = gotVenue.getAdmin_id();
//                    final int gotVenue_venueTickets = gotVenue.getVenue_tickets();
//                    dbRefVenue.update("venue_tickets", gotVenue_venueTickets-1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
                            final DocumentReference dbRefAdmin = dbFirestore.collection("Admins")
                                    .document(venue.getAdmin_id());
                           /* DocumentReference dbRefVenue = dbRefAdmin.collection("Venues")
                                    .document(venue.getVenue_id());*/
//                            dbRefVenue.update("venue_tickets", gotVenue_venueTickets-1).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
                                    CollectionReference dbRefBookingsAdminSide = dbRefAdmin.collection("Bookings");
                                    CollectionReference dbRefGuests = dbRefBookingsAdminSide.document(venue.getVenue_id()).collection("Guests");
                                    dbRefGuests.document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(getGuestInstance())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    CollectionReference dbRefBookingsClientSide = dbFirestore.collection("Users")
                                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .collection("Bookings");
                                                    dbRefBookingsClientSide.document(venue.getVenue_id()).set(venue).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mProgressBar.setVisibility(View.INVISIBLE);
                                                            FancyToast.makeText(mContext, "Restaurant booked successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                                        }

                                                    });
                                                }
                                            });
//                                }
//                            });

//                        }
//                    });


                }

                private Guest getGuestInstance() {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    Guest guest =  new Guest(currentUser.getDisplayName(), currentUser.getEmail(), currentUser.getUid());
                    return guest;
                }

            });
        }


        public void setTapListener(View view) {
            mVenue = (RestaurantVenue) view.getTag(R.id.TAG_FOR_EVENT);
            Log.d(TAG, "setTapListener: getVenue_name() = " + mVenue.getVenue_name());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            RestaurantVenueItemTapListener venueItemTapListener = (RestaurantVenueItemTapListener) mContext;
            venueItemTapListener.onTap(mVenue, viewId, position);
        }
    }

}
