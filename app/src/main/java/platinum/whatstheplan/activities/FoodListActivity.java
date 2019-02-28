package platinum.whatstheplan.activities;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import platinum.whatstheplan.R;
import platinum.whatstheplan.adapters.FoodsAdapter;
import platinum.whatstheplan.models.FoodItem;

public class FoodListActivity extends AppCompatActivity {

    private static final String TAG = "FoodListActivityTag";

    private Toolbar mToolbar;
    private String mRestaurantName;
    private String mRestaurantId;
    private TextView mToolBarTitleTV;

    private RecyclerView mFoodsRV;
    private FirebaseFirestore mDbFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        initViewsAndVariables ();
        performActions ();
    }

    private void performActions() {
        mFoodsRVActions ();
        displayFoodItems();
    }

    private void mFoodsRVActions() {
        mFoodsRV.setHasFixedSize(true);
        DividerItemDecoration itemDecorator = new DividerItemDecoration
                (FoodListActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(FoodListActivity.this, R.drawable.divider));
        mFoodsRV.addItemDecoration(itemDecorator);

    }

    private void displayFoodItems() {

        CollectionReference mDbFoodsRef = mDbFirestore.collection("Restaurants")
                .document(mRestaurantId).collection("Food Items");

         FirestoreRecyclerOptions<FoodItem> frOptions =
                new FirestoreRecyclerOptions.Builder<FoodItem>()
                        .setQuery(mDbFoodsRef, FoodItem.class)
                        .build();

        FoodsAdapter adapter = new FoodsAdapter(frOptions, FoodListActivity.this);
        mFoodsRV.setAdapter(adapter);
        adapter.startListening();
        mFoodsRV.setLayoutManager(new LinearLayoutManager(FoodListActivity.this));
;
    }

    private void initViewsAndVariables() {
        mRestaurantName = getIntent().getStringExtra("restaurant_name");
        mRestaurantId = getIntent().getStringExtra("restaurant_id");
        Log.d(TAG, "initViewsAndVariables: " + mRestaurantId);
        mToolBarTitleTV = findViewById(R.id.toolbar_title_TV);
        mToolBarTitleTV.setText(mRestaurantName);
        mDbFirestore = FirebaseFirestore.getInstance();

        mFoodsRV = findViewById(R.id.foodlistRV);

    }
}
