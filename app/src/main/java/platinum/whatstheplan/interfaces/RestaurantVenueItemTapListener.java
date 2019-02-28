package platinum.whatstheplan.interfaces;

import platinum.whatstheplan.models.RestaurantVenue;
import platinum.whatstheplan.models.Venue;

public interface RestaurantVenueItemTapListener {

    public void onTap(RestaurantVenue venue, int viewId, int tappedItemPosition);

}
