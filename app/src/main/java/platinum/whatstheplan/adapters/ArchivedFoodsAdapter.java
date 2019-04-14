package platinum.whatstheplan.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import platinum.whatstheplan.R;
import platinum.whatstheplan.models.FoodItem;

public class ArchivedFoodsAdapter extends FirestoreRecyclerAdapter<FoodItem, ArchivedFoodsAdapter.FoodViewHolder> {

    private static final String TAG = "FoodsAdapterTag";

    private Context mContext;


    public ArchivedFoodsAdapter(@NonNull FirestoreRecyclerOptions<FoodItem> options, Context context) {
        super(options);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodViewHolder holder, int position, @NonNull FoodItem foodItem) {

        holder.foodNameTV.setText(foodItem.getName());
        holder.foodPriceTV.setText(foodItem.getPrice());
        Glide.with(mContext).load(Uri.parse(foodItem.getImage())).into(holder.foodImageIV);

    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.layout_fooditem, viewGroup, false);
        FoodViewHolder viewHolder = new FoodViewHolder(itemView);
        return viewHolder;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {

        private TextView foodNameTV;
        private ImageView foodImageIV;
        private TextView foodPriceTV;


        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);

            foodNameTV = itemView.findViewById(R.id.food_name_TV);
            foodImageIV = itemView.findViewById(R.id.food_image_IV);
            foodPriceTV = itemView.findViewById(R.id.food_price_TV);
        }
    }
}
