package platinum.whatstheplan.activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.CalendarView;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import platinum.whatstheplan.R;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class EventsActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener {

    private static final String TAG = "EventsActivityTag";
    private Context mContext;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private CalendarView calendarCV;
    private RecyclerView eventsRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        initViewsAndVariables ();

        performActions ();
    }

        private void initViewsAndVariables() {
            mContext = EventsActivity.this;
            bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
            calendarCV = findViewById(R.id.calendar_CV);
            eventsRV = findViewById(R.id.events_RV);

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

                Toast.makeText(mContext,
                        "The Date you selected is " + String.valueOf(i2)  + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i),
                        Toast.LENGTH_LONG)
                .show();
        }
    }
}
