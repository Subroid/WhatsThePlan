package platinum.whatstheplan.adapters;

import android.app.Activity;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.PartyEventsActivity;
import platinum.whatstheplan.interfaces.VenueItemTapListener;
import platinum.whatstheplan.models.Venue;

public class VenuesAdapter extends RecyclerView.Adapter<VenuesAdapter.VenueViewHolder> {

    private static final String TAG = "VenuesAdapter";
    Activity mActivity;
    private Context mContext;
    private List<Venue> mVenueList = new ArrayList<>();
    private Venue mVenue;
    private Location mUserCurrentLocation;
    private GoogleMap mMap;
    private ProgressBar mProgressBar;

    float[] distanceResults = new float[2];


    public VenuesAdapter() {
    }

    public VenuesAdapter(Context context, List<Venue> venueList, Location userCurrentLocation, GoogleMap map, ProgressBar progressBar, Activity activity) {
        mContext = context;
        mVenueList = venueList;
        mUserCurrentLocation = userCurrentLocation;
        mMap = map;
        mProgressBar = progressBar;
        mActivity = activity;
    }


    @NonNull
    @Override
    public VenueViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_venue, viewGroup, false);
        VenueViewHolder viewHolder = new VenueViewHolder(itemView);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull VenuesAdapter.VenueViewHolder venueViewHolder, int position) {
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
        venueViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_EVENT, mVenue);
        venueViewHolder.show_on_map_BTN.setTag(R.id.TAG_FOR_POSITION, position);
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

    class VenueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView venue_name_TV;
        private TextView venue_type_TV;
        private TextView venue_main_event_TV;
        private TextView venue_sub_events_TV;
        private TextView venue_address_TV;
        private ImageView venue_image_IV;
        private ImageView venue_layout_bg_IV;
        private Button show_on_map_BTN;
        private Button show_events_BTN;

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

            show_on_map_BTN.setOnClickListener(this);
            show_events_BTN.setOnClickListener(this);

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
                    mActivity.finish();
                    //^had to finish the activity because of IndexOutOfBoundException
                    navigateToActivity (mContext, PartyEventsActivity.class);
                    break;
            }
        }

        private void navigateToActivity(Context mContext, Class classname) {
            Intent intent = new Intent(mContext, classname);
            mContext.startActivity(intent);
        }


        public void setTapListener(View view) {
            mVenue = (Venue) view.getTag(R.id.TAG_FOR_EVENT);
            Log.d(TAG, "setTapListener: getVenue_name() = " + mVenue.getVenue_name());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            VenueItemTapListener venueItemTapListener = (VenueItemTapListener) mContext;
            venueItemTapListener.onTap(mVenue, viewId, position);
        }
    }

}
