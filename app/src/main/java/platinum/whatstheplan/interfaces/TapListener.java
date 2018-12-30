package platinum.whatstheplan.interfaces;

import platinum.whatstheplan.models.Restaurant;

public interface TapListener {

    public void onTap(Restaurant restaurant, int viewId, int tappedItemPosition);

}
