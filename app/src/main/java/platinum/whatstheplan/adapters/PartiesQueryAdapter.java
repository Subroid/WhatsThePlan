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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.FoodListActivity;
import platinum.whatstheplan.interfaces.EventItemTapListener;
import platinum.whatstheplan.interfaces.PartyItemTapListener;
import platinum.whatstheplan.models.Party;
import platinum.whatstheplan.models.Event;

public class PartiesQueryAdapter extends RecyclerView.Adapter<PartiesQueryAdapter.PartyViewHolder> {

    private static final String TAG = "PartiesQueryAdapterTag";
    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();
    private Event mEvent;
    private Location mUserCurrentLocation;
    GoogleMap mMap;

    float[] distanceResults = new float[2];

    public PartiesQueryAdapter() {
    }

    public PartiesQueryAdapter(Context mContext, List<Event> mEventList, Event mEvent, Location mUserCurrentLocation, GoogleMap map) {
        this.mContext = mContext;
        this.mEventList = mEventList;
        this.mEvent = mEvent;
        this.mUserCurrentLocation = mUserCurrentLocation;
        this.mMap = map;
    }


    @NonNull
    @Override
    public PartyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_party, viewGroup, false);
        PartyViewHolder viewHolder = new PartyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PartyViewHolder partyViewHolder, int position) {
        partyViewHolder.Party_name_TV.setText(mEvent.getEvent_name());

        float distance = getDistancBetweenTwoPoints(
                mUserCurrentLocation.getLatitude(),
        mUserCurrentLocation.getLongitude(),
                mEvent.getEvent_geopoint().getLatitude(),
                mEvent.getEvent_geopoint().getLongitude());
        distance = distance / 1000;
        DecimalFormat decimalFormat = new DecimalFormat("#.00");


        partyViewHolder.distance_TV.setText(String.valueOf(decimalFormat.format(distance)) + " km");
        Glide.with(mContext).load(Uri.parse(mEvent.getEvent_image())).apply(new RequestOptions().fitCenter()).into(partyViewHolder.Party_image_IV);
        LatLng PartyLatLng = new LatLng(mEvent.getEvent_geopoint().getLatitude(), mEvent.getEvent_geopoint().getLongitude());
        mMap.addMarker(new MarkerOptions().position(PartyLatLng));
        partyViewHolder.show_on_button_BTN.setTag(R.id.TAG_FOR_PARTY, mEvent);
        partyViewHolder.show_on_button_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        partyViewHolder.get_direction_BTN.setTag(R.id.TAG_FOR_PARTY, mEvent);
        partyViewHolder.get_direction_BTN.setTag(R.id.TAG_FOR_POSITION, position);
        partyViewHolder.get_direction_BTN.setEnabled(false);
        partyViewHolder.visit_BTN.setTag(R.id.TAG_FOR_PARTY, mEvent);

    }

    private float getDistancBetweenTwoPoints(double lat1, double long1, double lat2, double long2) {
        Location.distanceBetween(lat1, long1, lat1, long2, distanceResults);
        return distanceResults[0];

    }


    @Override
    public int getItemCount() {
        return mEventList.size();
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
                case R.id.visit_BTN:
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
            mEvent = (Event) view.getTag(R.id.TAG_FOR_PARTY);
            Log.d(TAG, "setTapListener: Party.getEvent_name() = " + mEvent.getEvent_name());
            int position = (int) view.getTag(R.id.TAG_FOR_POSITION);
            int viewId = view.getId();
            EventItemTapListener partyItemTapListener = (EventItemTapListener) mContext;
            partyItemTapListener.onTap(mEvent, viewId, position);
        }
    }
}
