package platinum.whatstheplan.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.Calendar;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.BookedEventsAdapter;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.utils.BookingDbHandler;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class BookingsActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private static final String TAG = "EventsActivityTag";
    private Context mContext;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private ProgressBar progressBar;
    private CalendarView calendarCV;
    private RecyclerView eventsRV;
    private TextView noeventTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        initViewsAndVariables ();

        performActions ();

    }

        private void initViewsAndVariables() {
            mContext = BookingsActivity.this;
            bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
            progressBar = findViewById(R.id.progressBar);
            calendarCV = findViewById(R.id.calendar_CV);
            eventsRV = findViewById(R.id.events_RV);
            noeventTV = findViewById(R.id.no_event_TV);

        }

        private void performActions() {
            performBottomNavigationViewExActions ();
            calendarCVActions();
            eventsRVActions ();

        }

    private void eventsRVActions() {
        eventsRV.setHasFixedSize(true);
        // todo set adapter etc
    }

    private void calendarCVActions() {
        calendarCV.setDate(System.currentTimeMillis(), true, true);
        Calendar calendar = Calendar.getInstance();
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + (calendar.get(Calendar.MONTH)+1)
                + "/" + calendar.get(Calendar.YEAR);
        Log.d(TAG, "calendarCVActions: y m d = " + date);
        getEventsForSelectedDate(date);
        calendarCV.setOnDateChangeListener(this);
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

    @Override
    public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
        switch (calendarView.getId()) {
            case R.id.calendar_CV :
                i1 = i1+1;
                String date = i2 + "/" + i1 + "/" + i;
                getEventsForSelectedDate (date);

        }
    }

    private void getEventsForSelectedDate(String date) {
        progressBar.setVisibility(View.VISIBLE);


        BookingDbHandler bookingDbHandler = new BookingDbHandler(BookingsActivity.this);
        List<Event> eventList = bookingDbHandler.findEvents(date);
        List<RestaurantVenue> restaurantVenueList = bookingDbHandler.findRestaurantVenues(date);
        BookedEventsAdapter bookedEventsAdapter = new BookedEventsAdapter(mContext, eventList, progressBar);
        eventsRV.setAdapter(bookedEventsAdapter);
        eventsRV.setLayoutManager(new LinearLayoutManager(mContext));
        progressBar.setVisibility(View.INVISIBLE);
        if (eventsRV.getAdapter().getItemCount() > 0) {
            noeventTV.setVisibility(View.INVISIBLE);
        } else {
            bookedEventsAdapter = new BookedEventsAdapter(mContext, restaurantVenueList, progressBar, true);
            eventsRV.setAdapter(bookedEventsAdapter);
            eventsRV.setLayoutManager(new LinearLayoutManager(mContext));
            progressBar.setVisibility(View.INVISIBLE);
            if (eventsRV.getAdapter().getItemCount() > 0) {
                noeventTV.setVisibility(View.INVISIBLE);
            }
        }

    }
}
