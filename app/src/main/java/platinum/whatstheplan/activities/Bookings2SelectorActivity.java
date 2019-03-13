package platinum.whatstheplan.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import platinum.whatstheplan.R;

public class Bookings2SelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Bookings2SelectorTag";
    private Button m_booked_restaurants_BTN;
    private Button m_booked_events_BTN;
    private CalendarDay m_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings_2_selector);

        ininViewsAndVariables ();
    }

    private void ininViewsAndVariables() {
        m_booked_events_BTN = findViewById(R.id.booked_events_BTN);
        m_booked_restaurants_BTN = findViewById(R.id.booked_restaurants_BTN);
        m_date = getIntent().getParcelableExtra("selected date");
        m_booked_events_BTN.setOnClickListener(this);
        m_booked_restaurants_BTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.booked_restaurants_BTN :
                NavigateToAnotherActivity(Bookings3RestaurantsActivity.class, m_date);
                break;
            case R.id.booked_events_BTN :
                NavigateToAnotherActivity(Bookings3EventsActivity.class, m_date);
                break;
        }
    }

    private void NavigateToAnotherActivity(Class classname, CalendarDay date) {
        Intent intent = new Intent(Bookings2SelectorActivity.this, classname);
        intent.putExtra("selected date", date);
        startActivity(intent);
    }
}
