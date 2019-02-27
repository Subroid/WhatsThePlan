package platinum.whatstheplan.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.constraint.ConstraintLayout;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.shashank.sony.fancytoastlib.FancyToast;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.authentications.PhoneAuthActivity;
import platinum.whatstheplan.activities.authentications.SignInActivity;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivityTag";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private Context mContext;
    private android.support.v7.widget.Toolbar toolbarTB;
    private ConstraintLayout restaurantsCL;
    private ConstraintLayout sportsCL;
    private ConstraintLayout openeventsCL;
    private ConstraintLayout partiesCL;
    private ConstraintLayout featuredCL;
    private ConstraintLayout exclusiveCL;
    private ImageView restaurantsBgIV;
    private ImageView openeventsBgIV;
    private ImageView partiesBgIV;
    private ImageView sportsBgIV;
    private ImageView featuredBgIV;
    private ImageView exclusiveBgIV;
    private com.google.firebase.auth.FirebaseUser mCurrentUser;
    private boolean mIsGpsEnabled;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: mIsGpsEnabled = " + mIsGpsEnabled);

        getCurrentUser();
        initViewsAndVariables();
        performActions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentUser();
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
                Intent signInIntent = new Intent(HomeActivity.this, PhoneAuthActivity.class);
                startActivity(signInIntent);

        }

        return super.onOptionsItemSelected(item);
    }

        private void getCurrentUser() {
        Log.d(TAG, "getCurrentUser: mIsGpsEnabled = " + mIsGpsEnabled);
            mCurrentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (mCurrentUser == null) {
                android.content.Intent signInIntent = new android.content.Intent(HomeActivity.this,
                        PhoneAuthActivity.class);
                startActivity(signInIntent);
            }
        }

        private void initViewsAndVariables() {
                    mContext = HomeActivity.this;
                    toolbarTB  = findViewById(R.id.toolbar);
                    bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
                    restaurantsCL = findViewById(R.id.restaurants_CL);
                    openeventsCL = findViewById(R.id.openevents_CL);
                    partiesCL = findViewById(R.id.parties_CL);
                    sportsCL = findViewById(R.id.sports_CL);
                    featuredCL = findViewById(R.id.featured_CL);
                    exclusiveCL = findViewById(R.id.exclusive_CL);
                    restaurantsBgIV = findViewById(R.id.restaurants_bg_IV);
                    openeventsBgIV = findViewById(R.id.openevents_bg_IV);
                    partiesBgIV = findViewById(R.id.parties_bg_IV);
                    sportsBgIV = findViewById(R.id.sports_bg_IV);
                    featuredBgIV = findViewById(R.id.featured_bg_IV);
                    exclusiveBgIV = findViewById(R.id.exclusive_bg_IV);

                }

        private void performActions() {
            setSupportActionBar(toolbarTB);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            performBottomNavigationViewExActions ();
            performClicks ();
            performImageViewsActions ();

        }

            private void performImageViewsActions() {
        Glide.with(HomeActivity.this).load(R.drawable.circle_indianred)
                .apply(RequestOptions.centerCropTransform()).into(restaurantsBgIV);
        Glide.with(HomeActivity.this).load(R.drawable.circle_butterscotch)
                .apply(RequestOptions.centerCropTransform()).into(openeventsBgIV);
        Glide.with(HomeActivity.this).load(R.drawable.circle_persianpink)
                .apply(RequestOptions.centerCropTransform()).into(partiesBgIV);
        Glide.with(HomeActivity.this).load(R.drawable.circle_tealgreen)
                .apply(RequestOptions.centerCropTransform()).into(sportsBgIV);
        Glide.with(HomeActivity.this).load(R.drawable.circle_internationalorange)
                .apply(RequestOptions.centerCropTransform()).into(featuredBgIV);
        Glide.with(HomeActivity.this).load(R.drawable.circle_unmellowyellow)
                .apply(RequestOptions.centerCropTransform()).into(exclusiveBgIV);
    }

            private void performBottomNavigationViewExActions() {
                bottomNavigationViewEx.enableAnimation(false);
                bottomNavigationViewEx.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
                bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);
                // ^disable shifting mode of item when clicked

                BottomNavigationViewHelper.settingBottomNavigationViewListener
                                            (bottomNavigationViewEx, mContext, getResources());
                bottomNavigationViewEx.getIconAt(0).setImageDrawable(getDrawable(R.drawable.ic_home_blue));
               /* bottomNavigationViewEx.getBottomNavigationItemView(0)
                        .setTextColor(ColorStateList.valueOf(R.drawable.bottomnavigationitem_colorstates));*/

            }

            private void performClicks() {
                restaurantsCL.setOnClickListener(getOnClickListenerInstanceForRestaurantsCL ());
                sportsCL.setOnClickListener(getOnClickListenerInstanceForSportsCL ());
                openeventsCL.setOnClickListener(getOnClickListenerInstanceForOpenEventsCL ());
                partiesCL.setOnClickListener(getOnClickListenerInstanceForPartiesCL ());
                featuredCL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FancyToast.makeText(mContext, "Coming Soon", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    }
                });
                exclusiveCL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FancyToast.makeText(mContext, "Coming Soon", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                    }
                });
            }

                private View.OnClickListener getOnClickListenerInstanceForPartiesCL() {
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent partiesIntent = new Intent(HomeActivity.this, PartyVenuesActivity.class);
                            partiesIntent.putExtra("event_type", 1);
                            startActivity(partiesIntent);
                        }
                    };
                    return onClickListener;
                }

                private View.OnClickListener getOnClickListenerInstanceForRestaurantsCL() {
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent restaurantsIntent = new Intent(HomeActivity.this, FoodsDrinksVenuesActivity.class);
                            restaurantsIntent.putExtra("event_type", 2);
                            startActivity(restaurantsIntent);
                        }
                    };
                    return onClickListener;
                }

                private View.OnClickListener getOnClickListenerInstanceForSportsCL() {
                                        View.OnClickListener onClickListener = new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent sportsIntent = new Intent(HomeActivity.this, SportsActivity.class);
                                                startActivity(sportsIntent);
                                            }
                                        };
                                        return onClickListener;
                                    }

                private View.OnClickListener getOnClickListenerInstanceForOpenEventsCL() {
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent openeventsIntent = new Intent(HomeActivity.this, OpenEventsActivity.class);
                            startActivity(openeventsIntent);
                        }
                    };
                    return onClickListener;
                }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
