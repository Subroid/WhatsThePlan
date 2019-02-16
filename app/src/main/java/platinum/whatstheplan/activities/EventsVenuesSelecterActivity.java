package platinum.whatstheplan.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.authentications.SignInActivity;

public class EventsVenuesSelecterActivity extends AppCompatActivity implements View.OnClickListener {

    private com.google.firebase.auth.FirebaseUser mCurrentUser;
    private ImageView eventsIV;
    private ImageView venuesIV;
    private Toolbar toolbarTB;
    private int mEventType = 0;
    private Class mVenuesClassName;
    private Class mEventsClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_venue_selecter);

        getCurrentUser ();
        initViewsAndVariables ();
        performActions ();
    }

    private void getCurrentUser() {
        mCurrentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser == null) {
            navigateToNewActivity (SignInActivity.class);
        }
    }

    private void performActions() {

        setSupportActionBar(toolbarTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Glide.with(EventsVenuesSelecterActivity.this)
                .load(R.drawable.events)
                .apply(RequestOptions.circleCropTransform())
                .into(eventsIV);
        eventsIV.setOnClickListener(this);

        Glide.with(EventsVenuesSelecterActivity.this)
                .load(R.drawable.venues)
                .apply(RequestOptions.circleCropTransform())
                .into(venuesIV);
        venuesIV.setOnClickListener(this);
    }

    private void initViewsAndVariables() {
        if (getIntent() != null) {
            mEventType = getIntent().getIntExtra("event_type", 0);
            switch (mEventType) {
                case 1 :
                    mVenuesClassName = PartyVenuesActivity.class;
                    mEventsClassName = PartyEventsActivity.class;
                    break;
                case 2 :
                    mVenuesClassName = FoodsDrinksVenuesActivity.class;
                    mEventsClassName = FoodsDrinksActivity.class;
                    break;
            }
        }
        toolbarTB = findViewById(R.id.toolbar);
        eventsIV = findViewById(R.id.events_IV);
        venuesIV = findViewById(R.id.venues_IV);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.events_IV :
                navigateToNewActivity (mEventsClassName);
                break;
            case R.id.venues_IV :
                navigateToNewActivity(mVenuesClassName);
        }
    }

    private void navigateToNewActivity(Class classname) {
        Intent intent = new Intent(EventsVenuesSelecterActivity.this, classname);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out :
                FirebaseAuth.getInstance().signOut();
                navigateToNewActivity(SignInActivity.class);

        }

        return super.onOptionsItemSelected(item);
    }
}
