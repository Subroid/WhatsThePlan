package platinum.whatstheplan.adapters;

import android.content.Context;
import android.location.Location;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.OtherVenue;

public class OtherVenuesAdapter extends RecyclerView.Adapter<OtherVenuesAdapter.VenueViewHolder> {

    private static final String TAG = "VenuesAdapter";
    private Context mContext;
    private List<OtherVenue> mVenueList = new ArrayList<>();
    private OtherVenue mVenue;
    private OtherVenue mOtherVenue;
    private Location mUserCurrentLocation;
    private GoogleMap mMap;
    private ProgressBar mProgressBar;

    float[] distanceResults = new float[2];


    public OtherVenuesAdapter() {
    }

    public OtherVenuesAdapter(Context context, List<OtherVenue> venueList,ProgressBar progressBar) {
        mContext = context;
        mVenueList = venueList;
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
    public void onBindViewHolder(@NonNull OtherVenuesAdapter.VenueViewHolder venueViewHolder, int position) {
        mVenue = mVenueList.get(position);

        venueViewHolder.venue_name_TV.setText(mVenue.getVenue_name());
        Log.d(TAG, "onBindViewHolder: venue name " + mVenue.getVenue_name());
        DecimalFormat decimalFormat = new DecimalFormat("#.00");

        venueViewHolder.venue_name_TV.setText("Venue : " + mVenue.getVenue_name());
        venueViewHolder.venue_address_TV.setText("Address : " + mVenue.getVenue_address());

        Glide.with(mContext).load(mVenue.getVenue_image()).apply(new RequestOptions().fitCenter()).into(venueViewHolder.venue_image_IV);
        Glide.with(mContext).load(mVenue.getVenue_image()).apply(new RequestOptions().fitCenter()).into(venueViewHolder.venue_layout_bg_IV);
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        return mVenueList.size();
    }


    class VenueViewHolder extends RecyclerView.ViewHolder {

        private TextView venue_name_TV;
        private TextView venue_address_TV;
        private ImageView venue_image_IV;
        private ImageView venue_layout_bg_IV;

        public VenueViewHolder(@NonNull View itemView) {
            super(itemView);
            venue_name_TV = itemView.findViewById(R.id.venue_name_TV);
            venue_address_TV = itemView.findViewById(R.id.venue_address_TV);
            venue_image_IV = itemView.findViewById(R.id.venue_image_IV);
            venue_layout_bg_IV = itemView.findViewById(R.id.venue_layout_bg_IV);

        }
    }

}
