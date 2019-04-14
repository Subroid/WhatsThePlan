package platinum.whatstheplan.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.ResultReceiver;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.RestaurantVenuesAdapter;
import platinum.whatstheplan.adapters.VenuesAdapter;
import platinum.whatstheplan.interfaces.PageLoadingListener;
import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.models.Venue;

public class SearchActivity extends AppCompatActivity implements PageLoadingListener {

    private static final String TAG = "SearchActivityTag";
    private MaterialSearchBar mSearchBarMSB;
    private String mPreviousActivityStr;
    private List<RestaurantVenue> mRestaurantVenueList;
//    private List<RestaurantVenue> mRestaurantVenueSuggestionList;
    private List<String> mRestaurantNameList;
    private List<Venue> mPartyVenuesList;
    private List<String> mSuggestionList;
    private List<RestaurantVenue> mRestaurantVenueClickedSuggestionList;
    private RecyclerView mSearchResultRV;
    private Location mUserCurrentLocation;
    private List<String> mPartyVenueNamesList;
    private List<Venue> mPartyVenueClickedSuggestionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViewsAndVariables ();
    }

    private void initViewsAndVariables() {
        mSearchBarMSB = findViewById(R.id.search_bar_MSB);
        mSearchResultRV = findViewById(R.id.search_result_RV);
        mRestaurantVenueList = new ArrayList<>();
        mPartyVenuesList = new ArrayList<>();
        mRestaurantNameList = new ArrayList<>();
        mPartyVenueNamesList = new ArrayList<>();
        mSuggestionList = new ArrayList<>();
        mRestaurantVenueClickedSuggestionList = new ArrayList<>();
        mPartyVenueClickedSuggestionList = new ArrayList<>();
        mUserCurrentLocation = getIntent().getParcelableExtra("user_location");
        mPreviousActivityStr = getIntent().getStringExtra("previous_activity");
        switch (mPreviousActivityStr) {
            case "Restaurants" :
                mRestaurantVenueList = getIntent().getParcelableArrayListExtra("restaurants_list");
                for (RestaurantVenue restaurantVenue:
                    mRestaurantVenueList) {
                    String restaurantName = restaurantVenue.getVenue_name();
                    mRestaurantNameList.add(restaurantName);
                    mSearchBarMSBActions (mRestaurantNameList);
                }
                break;
            case "Parties" :
                mPartyVenuesList = getIntent().getParcelableArrayListExtra("party_venues_list");
                for (Venue venue:
                    mPartyVenuesList) {
                    String partyVenueName = venue.getVenue_name();
                    mPartyVenueNamesList.add(partyVenueName);
                    mSearchBarMSBActions (mPartyVenueNamesList);
                }
                break;
        }
    }

    private void mSearchBarMSBActions(final List<String> listNames) {

        mSearchBarMSB.setLastSuggestions(listNames);

        mSearchBarMSB.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchText = charSequence.toString();
                if (searchText.length() > 0) {
                    searchSuggesions (searchText);
                    mSearchBarMSB.setLastSuggestions(mSuggestionList);
                    mSearchBarMSB.showSuggestionsList();
                } else {
                    mSearchBarMSB.hideSuggestionsList();
                }

            }

            private void searchSuggesions(String searchText) {
                mSuggestionList.clear();
                for (String venueName:
                     listNames) {
                    if (venueName.toLowerCase().contains(searchText.toLowerCase())) {
                        mSuggestionList.add(venueName);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 0) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mSearchBarMSB.getWindowToken(), 0);
                }
            }
        });


        mSearchBarMSB.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    mSearchBarMSB.setLastSuggestions(listNames);
                    mSearchBarMSB.showSuggestionsList();
                } else {
                    mSearchBarMSB.hideSuggestionsList();
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mSearchBarMSB.getWindowToken(), 0);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        mSearchBarMSB.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                String venueName = listNames.get(position);
                switch (mPreviousActivityStr) {
                    case "Restaurants" :
                        mRestaurantVenueClickedSuggestionList.clear();
                        for (RestaurantVenue restaurantVenue:
                                mRestaurantVenueList) {
                            if (restaurantVenue.getVenue_name().toLowerCase().contains(venueName.toLowerCase())) {
                                mRestaurantVenueClickedSuggestionList.add(restaurantVenue);
                            }
                        }
                        RestaurantVenuesAdapter restaurantVenuesAdapter = new RestaurantVenuesAdapter(SearchActivity.this,
                                mRestaurantVenueClickedSuggestionList, mUserCurrentLocation, null, null);
                        mSearchBarMSB.clearSuggestions();
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mSearchBarMSB.getWindowToken(), 0);
                        displayClickedSearchResult (restaurantVenuesAdapter);
                        break;
                    case "Parties" :
                        mPartyVenueClickedSuggestionList.clear();
                        for (Venue partyVenue:
                                mPartyVenuesList) {
                            if (partyVenue.getVenue_name().toLowerCase().contains(venueName.toLowerCase())) {
                                mPartyVenueClickedSuggestionList.add(partyVenue);
                            }
                        }
                            VenuesAdapter venuesAdapter = new VenuesAdapter(SearchActivity.this,
                            mPartyVenueClickedSuggestionList, mUserCurrentLocation, null, null, SearchActivity.this);
                        mSearchBarMSB.clearSuggestions();
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mSearchBarMSB.getWindowToken(), 0);
                        displayClickedSearchResult (venuesAdapter);
                }

            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

    }

    private void displayClickedSearchResult(RecyclerView.Adapter recyclerVewAdapter) {
        mSearchResultRV.setHasFixedSize(true);
        mSearchResultRV.setAdapter(recyclerVewAdapter);
        mSearchResultRV.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
    }

    @Override
    public void onPageLoad(boolean pageLoaded) {
        // does nothing. just had to apply
    }

    @Override
    public void onBackPressed() {
        switch (mPreviousActivityStr) {
            case "Restaurants" :
                startActivity(new Intent(SearchActivity.this, FoodsDrinksVenuesActivity.class));
                finish();
                break;
            case "Parties" :
                startActivity(new Intent(SearchActivity.this, PartyVenuesActivity.class));
                finish();
                break;
        }
    }
}
