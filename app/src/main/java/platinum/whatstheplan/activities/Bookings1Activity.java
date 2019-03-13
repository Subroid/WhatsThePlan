package platinum.whatstheplan.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.List;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.Event;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.utils.DatesDecoration;
import platinum.whatstheplan.utils.BookingDbHandler;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class Bookings1Activity extends AppCompatActivity implements OnDateSelectedListener {

    private static final String TAG = "BookingsActivityTag";
    private Context mContext;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private ProgressBar progressBar;
    private MaterialCalendarView calendarCV;
    private List<String> mBookingDateTextList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_1);

        initViewsAndVariables();

        performActions();

    }

    private void initViewsAndVariables() {
        mContext = Bookings1Activity.this;
        bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
        progressBar = findViewById(R.id.progressBar);
        calendarCV = findViewById(R.id.calendar_CV);
        mBookingDateTextList = new ArrayList<>();

    }

    private void performActions() {
        performBottomNavigationViewExActions();
        calendarCVActions();
        dotNotationsOnBookingDates ();

    }


    private void calendarCVActions() {
        calendarCV.setCurrentDate(CalendarDay.today());
        Log.d(TAG, "calendarCVActions: CalendarDay.today() : " + CalendarDay.today());
        DatesDecoration datesDecoration = new DatesDecoration(CalendarDay.today(), Bookings1Activity.this, DatesDecoration.CIRCLE_DECORATION);
        calendarCV.addDecorator(datesDecoration);
        calendarCV.setOnDateChangedListener(this);
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


    private void getEventsForSelectedDate(CalendarDay date) {
        progressBar.setVisibility(View.VISIBLE);

        BookingDbHandler bookingDbHandler = new BookingDbHandler(Bookings1Activity.this);
        List<Event> eventList = bookingDbHandler.findEvents(date);
        List<RestaurantVenue> restaurantVenueList = bookingDbHandler.findRestaurantVenues(date);
        /*BookedEventsAdapter bookedEventsAdapter = new BookedEventsAdapter(mContext, eventList, progressBar);
        eventsRV.setAdapter(bookedEventsAdapter);
        eventsRV.setLayoutManager(new LinearLayoutManager(mContext));
        progressBar.setVisibility(View.INVISIBLE);
        if (eventsRV.getAdapter().getItemCount() > 0) {
            noeventTV.setVisibility(View.INVISIBLE);
        } else {*/
            /*bookedEventsAdapter = new BookedEventsAdapter(mContext, restaurantVenueList, progressBar, true);
            eventsRV.setAdapter(bookedEventsAdapter);
            eventsRV.setLayoutManager(new LinearLayoutManager(mContext));
            progressBar.setVisibility(View.INVISIBLE);
            if (eventsRV.getAdapter().getItemCount() > 0) {
                noeventTV.setVisibility(View.INVISIBLE);
            }*/
        }


    private void dotNotationsOnBookingDatesUnusable() {
        BookingDbHandler bookingDbHandler = new BookingDbHandler(Bookings1Activity.this);
        List<RestaurantVenue> restaurantVenueList = bookingDbHandler.findAllRestaurantVenues ();
        for (RestaurantVenue restaurantVenue : restaurantVenueList) {
            String booking_date_text = restaurantVenue.getVenue_date();
            String[] booking_date_array = booking_date_text.split("-");
            Log.d(TAG, "dotNotationsOnBookingDates: restaurant date : " + restaurantVenue.getVenue_date());
            Log.d(TAG, "dotNotationsOnBookingDates: booking_date_array = " + booking_date_array);
            String booking_date_calendarday_format = booking_date_array[2] + "-" + booking_date_array[0] + "-" + booking_date_array[1];
            Log.d(TAG, "dotNotationsOnBookingDates: booking_date_calendarday_format = " + booking_date_calendarday_format);
            int booking_date_year = Integer.parseInt(booking_date_array[2]);
            int booking_date_month = Integer.parseInt(booking_date_array[1]);
            int booking_date_day = Integer.parseInt(booking_date_array[0]);
            Log.d(TAG, "dotNotationsOnBookingDates: localdatenow = " + LocalDate.now());
            CalendarDay bookedCalendarDay = CalendarDay.from(booking_date_year, booking_date_month, booking_date_day);
            DatesDecoration datesDecoration = new DatesDecoration(bookedCalendarDay, Bookings1Activity.this, DatesDecoration.DOT_DECORATION);
            calendarCV.addDecorator(datesDecoration);
        }

    }


    private void dotNotationsOnBookingDates() {
        Log.d(TAG, "dotNotationsOnBookingDates: called");

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .child("Bookings by Date")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d(TAG, "onDataChange: datasnapshot size " + dataSnapshot.getChildrenCount());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            Log.d(TAG, "onDataChange: dataSnapshot.getKey() = " + snapshot.getKey());
                            String booking_date_text = snapshot.getKey();
                            String[] booking_date_text_array = booking_date_text.split("-");
                            int booking_date_year = Integer.parseInt(booking_date_text_array[2]);
                            int booking_date_month = Integer.parseInt(booking_date_text_array[1]);
                            int booking_date_day = Integer.parseInt(booking_date_text_array[0]);
                            CalendarDay bookedCalendarDay = CalendarDay.from(booking_date_year, booking_date_month, booking_date_day);
                            DatesDecoration datesDecoration = new DatesDecoration(bookedCalendarDay, Bookings1Activity.this, DatesDecoration.DOT_DECORATION);
                            calendarCV.addDecorator(datesDecoration);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        /*FirebaseFirestore.getInstance()
                .collection("Users")
                .document(FirebaseAuth.getInstance().getUid())
                .collection("Bookings By Date")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: called");
                    Log.d(TAG, "onComplete: task result = " + task.getResult());
                    Log.d(TAG, "onComplete: task.getResult().getDocuments().size() = " + task.getResult().getDocuments().size());
                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Log.d(TAG, "onSuccess: documentSnapshot.getId() = " + documentSnapshot.getId());
                        String booking_date_text = documentSnapshot.getId();
                        String[] booking_date_text_array = booking_date_text.split("-");
                        int booking_date_year = Integer.parseInt(booking_date_text_array[2]);
                        int booking_date_month = Integer.parseInt(booking_date_text_array[1]);
                        int booking_date_day = Integer.parseInt(booking_date_text_array[0]);
                        CalendarDay bookedCalendarDay = CalendarDay.from(booking_date_year, booking_date_month, booking_date_day);
                        DatesDecoration datesDecoration = new DatesDecoration(bookedCalendarDay, Bookings1Activity.this, DatesDecoration.DOT_DECORATION);
                        calendarCV.addDecorator(datesDecoration);
                    }
                }
            }
        });*/

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        switch (widget.getId()) {
            case R.id.calendar_CV :
                calendarCV.removeDecorators();
                dotNotationsOnBookingDates();
                DatesDecoration datesDecoration = new DatesDecoration(date, Bookings1Activity.this, DatesDecoration.CIRCLE_DECORATION);
                calendarCV.addDecorator(datesDecoration);
                Intent intent = new Intent(Bookings1Activity.this, Bookings2SelectorActivity.class);
                intent.putExtra("selected date", date);
                Log.d(TAG, "onDateSelected: date = " + date);
                startActivity(intent);

//                getEventsForSelectedDate(date);
        }
    }

}
