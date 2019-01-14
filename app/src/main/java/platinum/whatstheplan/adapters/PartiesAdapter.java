package platinum.whatstheplan.adapters;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.FoodListActivity;
import platinum.whatstheplan.interfaces.PartyItemTapListener;
import platinum.whatstheplan.models.Party;
import platinum.whatstheplan.models.UserInformation;

//todo gist this class using naming conventions of model, viewholder etc

public class PartiesAdapter extends FirestoreRecyclerAdapter<Party, PartiesAdapter.PartyViewHolder> {

    private static final String TAG = "PartiesAdapterTag";

    Context mContext;
    UserInformation mUserInformation;
    GoogleMap mMap;
    float[] distanceResults = new float[2];
    Party mParty;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     * @param map
     */
    public PartiesAdapter(@NonNull FirestoreRecyclerOptions<Party> options, Context context, UserInformation userInformation, GoogleMap map) {
        super(options);
        Log.d(TAG, "PartiesAdapter: costructer called");
        mContext = context;
        mUserInformation = userInformation;
        mMap = map;
    }

    @Override
    protected void onBindViewHolder(@NonNull PartyViewHolder holder, int position, @NonNull Party party) {

        Log.d(TAG, "onBindViewHolder: name = " + party.getEvent_name());
        Log.d(TAG, "onBindViewHolder: address = " + party.getVenue_address());
//        Log.d(TAG, "onBindViewHolder: userGeopoint " + mUserInformation.getUserLocation().getEvent_geopoint());
        /*Log.d(TAG, "onBindViewHolder: userLatitude " + mUserInformation.getUserLocation().getEvent_geopoint().getLatitude());
        Log.d(TAG, "onBindViewHolder: userLongitude " + mUserInformation.getUserLocation().getEvent_geopoint().getLongitude());*/
//        Log.d(TAG, "onBindViewHolder: PartyGeopoint " + party.getEvent_geopoint());
        Log.d(TAG, "onBindViewHolder: user geopoint = " + mUserInformation.getUserLocation().getGeoPoint());

        holder.Party_name_TV.setText(party.getEvent_name());

           /* float distance = getDistancBetweenTwoPoints(
                    mUserInformation.getUserLocation().getGeoPoint().getLatitude(),
                    mUserInformation.getUserLocation().getGeoPoint().getLongitude(),
                    party.getEvent_geopoint().getLatitude(),
                    party.getEvent_geopoint().getLongitude());*/
//            distance = distance/1000;
//            DecimalFormat decimalFormat = new DecimalFormat("#.00");


//        holder.distance_TV.setText(String.valueOf(decimalFormat.format(distance)) + " km");
        Glide.with(mContext).load(Uri.parse(party.getEvent_image())).apply(new RequestOptions().fitCenter()).into(holder.Party_image_IV);
//        LatLng PartyLatLng = new LatLng(party.getEvent_geopoint().getLatitude(), party.getEvent_geopoint().getLongitude());
//        mMap.addMarker(new MarkerOptions().position(PartyLatLng));
        holder.show_on_button_BTN.setTag(R.id.TAG_FOR_PARTY, party);
        holder.show_on_button_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        holder.get_direction_BTN.setTag(R.id.TAG_FOR_PARTY, party);
        holder.get_direction_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        holder.get_direction_BTN.setEnabled(false);
        holder.visit_BTN.setTag(R.id.TAG_FOR_PARTY, party);

    }
            private float getDistancBetweenTwoPoints(double lat1, double long1, double lat2, double long2) {
                Location.distanceBetween(lat1, long1, lat1, long2, distanceResults);
                return distanceResults[0];

            }

    @NonNull
    @Override
    public PartyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_party, viewGroup, false);
        PartyViewHolder viewHolder = new PartyViewHolder(itemView);
        return viewHolder;
    }

    class PartyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView Party_name_TV;
        TextView distance_TV;
        ImageView Party_image_IV;
        Button show_on_button_BTN;
        Button get_direction_BTN;
        Button visit_BTN;


        public PartyViewHolder(@NonNull View itemView) {
            super(itemView);
            Party_name_TV = itemView.findViewById(R.id.party_name_TV);
            distance_TV = itemView.findViewById(R.id.distance_TV);
            Party_image_IV = itemView.findViewById(R.id.party_image_IV);
            show_on_button_BTN = itemView.findViewById(R.id.show_on_map_BTN);
            get_direction_BTN = itemView.findViewById(R.id.get_direction_BTN);
            visit_BTN = itemView.findViewById(R.id.visit_BTN);

            show_on_button_BTN.setOnClickListener(this);
            get_direction_BTN.setEnabled(false);
            get_direction_BTN.setAlpha(0.5f);
            get_direction_BTN.setOnClickListener(this);
            visit_BTN.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.show_on_map_BTN :
                    Log.d(TAG, "onClick: show_on_map_BTN");
                    setTapListener (view);
                    get_direction_BTN.setEnabled(true);
                    get_direction_BTN.setAlpha(1.0f);
                    break;
                case R.id.get_direction_BTN :
                    Log.d(TAG, "onClick: getdirection_btn");
                    setTapListener(view);
                    break;
                case R.id.visit_BTN :
                    Log.d(TAG, "onClick: visit_btn");
                    Party Party = (Party) view.getTag(R.id.TAG_FOR_PARTY);
                    Log.d(TAG, "onClick: Party id = " + Party.getEvent_id());
                    Intent intent = new Intent(mContext, FoodListActivity.class);
                    intent.putExtra("Party_name", Party_name_TV.getText());
                    intent.putExtra("Party_id", Party.getEvent_id());
                    mContext.startActivity(intent);
                    break;
            }
        }

        public void setTapListener(View view) {
            mParty = (Party) view.getTag(R.id.TAG_FOR_PARTY);
            Log.d(TAG, "setTapListener: Party.getEvent_name() = " + mParty.getEvent_name());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            PartyItemTapListener partyItemTapListener = (PartyItemTapListener) mContext;
            partyItemTapListener.onTap (mParty, viewId, position);
        }
    }
}
