package platinum.whatstheplan.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.BookedEventsAdapter;
import platinum.whatstheplan.adapters.BookedRestaurantsAdapter;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class Bookings3EventsActivity extends AppCompatActivity {

    private static final String TAG = "BookingsListActivityTag";
    private Context mContext;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private ProgressBar progressBar;
//    private RecyclerView restaurantsRV;
    private RecyclerView eventsRV;
    private TextView noeventTV;
    private TextView toolbarTitleTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_3_events);

        initViewsAndVariables();
    }

    private void initViewsAndVariables() {

        mContext = Bookings3EventsActivity.this;
        bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
        progressBar = findViewById(R.id.progressBar);
//        restaurantsRV = findViewById(R.id.restaurants_RV);
        eventsRV = findViewById(R.id.events_RV);
        noeventTV = findViewById(R.id.no_event_TV);
        toolbarTitleTV = findViewById(R.id.toolbar_title_TV);
        performBottomNavigationViewExActions();
        CalendarDay date = getIntent().getParcelableExtra("selected date");
        Log.d(TAG, "initViewsAndVariables: date = " + date);
        toolbarTitleTV.append(String.valueOf(date.getDay()) + "-" + String.valueOf(date.getMonth()) + "-" + String.valueOf(date.getYear()));
        fetchBookedEvents(date);
//        fetchBookedRestaurants(date);
    }

    private void performBottomNavigationViewExActions() {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
        // ^disable shifting mode of item when clicked

        BottomNavigationViewHelper.settingBottomNavigationViewListener
                (bottomNavigationViewEx, mContext, getResources());
        bottomNavigationViewEx.getIconAt(1).setImageDrawable(getDrawable(R.drawable.ic_calendar_blue));

    }



    private void fetchBookedEvents(CalendarDay selectedDate) {
        Log.d(TAG, "fetchBookedEvents: called");
        progressBar.setVisibility(View.VISIBLE);

        String day_text = String.valueOf(selectedDate.getDay());
        String month_text = String.valueOf(selectedDate.getMonth());
        String year_text = String.valueOf(selectedDate.getYear());
        String date_text = (day_text + "-" + month_text + "-" + year_text);
        Log.d(TAG, "fetchBookedEvents: date_text = " + date_text);
        Query query = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Bookings By Date")
                .document(date_text)
                .collection("Parties Bookings on " + date_text);

        Log.d(TAG, "fetchBookedEvents: query = " + ((CollectionReference) query).getPath());
        Log.d(TAG, "fetchBookedEvents: query = query id = " + (((CollectionReference) query).getId()));

        BookedEventsAdapter bookedEventsAdapter = new BookedEventsAdapter(getFireStoreRecyclerOptionsEvent (query), Bookings3EventsActivity.this, progressBar);

        eventsRV.setHasFixedSize(true);
        eventsRV.setAdapter(bookedEventsAdapter);
        eventsRV.setNestedScrollingEnabled(false);
        bookedEventsAdapter.startListening();
        eventsRV.setLayoutManager(new LinearLayoutManager(mContext));
        Log.d(TAG, "fetchBookedEvents: eventsRV.getLayoutManager().getChildCount = " + eventsRV.getLayoutManager().getChildCount());
        Log.d(TAG, "fetchBookedEvents: bookedEventsAdapter.getItemCount = " + bookedEventsAdapter.getItemCount());
        if (bookedEventsAdapter.getItemCount() < 1) {
            noeventTV.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);

    }

    private FirestoreRecyclerOptions<Event> getFireStoreRecyclerOptionsEvent(Query query) {

        FirestoreRecyclerOptions<Event> options = new FirestoreRecyclerOptions.Builder<Event>()
                .setQuery(query, Event.class)
                .build();
        return options;
    }

        /*private void fetchBookedRestaurants(CalendarDay selectedDate) {
        Log.d(TAG, "fetchBookedRestaurants: called");
        progressBar.setVisibility(View.VISIBLE);

        String day_text = String.valueOf(selectedDate.getDay());
        String month_text = String.valueOf(selectedDate.getMonth());
        String year_text = String.valueOf(selectedDate.getYear());
        String date_text = (day_text + "-" + month_text + "-" + year_text);
        Log.d(TAG, "fetchBookedRestaurants: date_text = " + date_text);
        Query query = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Bookings By Date")
                .document(date_text)
                .collection("Restaurants Bookings on " + date_text);

        Log.d(TAG, "fetchBookedRestaurants: query = " + ((CollectionReference) query).getPath());
        Log.d(TAG, "fetchBookedRestaurants: query = query id = " + (((CollectionReference) query).getId()));

        BookedRestaurantsAdapter bookedRestaurantsAdapter = new BookedRestaurantsAdapter(getFireStoreRecyclerOptions (query), Bookings3EventsActivity.this, progressBar);

        restaurantsRV.setHasFixedSize(true);
        restaurantsRV.setAdapter(bookedRestaurantsAdapter);
        restaurantsRV.setNestedScrollingEnabled(false);
        bookedRestaurantsAdapter.startListening();
        restaurantsRV.setLayoutManager(new LinearLayoutManager(mContext));
        Log.d(TAG, "fetchBookedRestaurants: restaurantsRV.getLayoutManager().getChildCount = " + restaurantsRV.getLayoutManager().getChildCount());
        Log.d(TAG, "fetchBookedRestaurants: bookedRestaurantsAdapter.getItemCount = " + bookedRestaurantsAdapter.getItemCount());
        if (bookedRestaurantsAdapter.getItemCount() < 1) {
            noeventTV.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);

    }*/

   /* private FirestoreRecyclerOptions<RestaurantVenue> getFireStoreRecyclerOptions(Query query) {

        FirestoreRecyclerOptions<RestaurantVenue> options = new FirestoreRecyclerOptions.Builder<RestaurantVenue>()
                .setQuery(query, RestaurantVenue.class)
                .build();
        return options;
    }*/



}
