package platinum.whatstheplan.activities;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import platinum.whatstheplan.R;
import platinum.whatstheplan.activities.authentications.SignInActivity;
import platinum.whatstheplan.utils.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private BottomNavigationViewEx bottomNavigationViewEx;
    private Context mContext;
    private ConstraintLayout restaurantsCL;
    private ConstraintLayout sportsCL;
    private ConstraintLayout openeventsCL;
    private ImageView restaurantsBgIV;
    private ImageView openeventsBgIV;
    private ImageView partiesBgIV;
    private ImageView sportsBgIV;
    private ImageView featuredBgIV;
    private ImageView exclusiveBgIV;
    private com.google.firebase.auth.FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getCurrentUser ();
        initViewsAndVariables ();
        performActions ();
    }

        private void getCurrentUser() {
            mCurrentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (mCurrentUser == null) {
                android.content.Intent signInIntent = new android.content.Intent(HomeActivity.this,
                        SignInActivity.class);
                startActivity(signInIntent);
            }
        }

        private void initViewsAndVariables() {
                    mContext = HomeActivity.this;
                    bottomNavigationViewEx = findViewById(R.id.ha_BottomNavigationView);
                    restaurantsCL = findViewById(R.id.restaurants_CL);
                    openeventsCL = findViewById(R.id.openevents_CL);
                    sportsCL = findViewById(R.id.sports_CL);
                    restaurantsBgIV = findViewById(R.id.restaurants_bg_IV);
                    openeventsBgIV = findViewById(R.id.openevents_bg_IV);
                    partiesBgIV = findViewById(R.id.parties_bg_IV);
                    sportsBgIV = findViewById(R.id.sports_bg_IV);
                    featuredBgIV = findViewById(R.id.featured_bg_IV);
                    exclusiveBgIV = findViewById(R.id.exclusive_bg_IV);

                }

        private void performActions() {
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
            }

                private View.OnClickListener getOnClickListenerInstanceForRestaurantsCL() {
                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent restaurantsIntent = new Intent(HomeActivity.this, RestaurantsActivity.class);
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


}
