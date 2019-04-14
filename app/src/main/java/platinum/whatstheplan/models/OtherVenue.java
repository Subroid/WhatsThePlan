package platinum.whatstheplan.models;

import android.graphics.Bitmap;

public class OtherVenue {

    private String venue_name;
    private String venue_address;
    private Bitmap venue_image;

    public OtherVenue(String venue_name, String venue_address, Bitmap venue_image) {
        this.venue_name = venue_name;
        this.venue_address = venue_address;
        this.venue_image = venue_image;
    }

    public OtherVenue() {
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name;
    }

    public String getVenue_address() {
        return venue_address;
    }

    public void setVenue_address(String venue_address) {
        this.venue_address = venue_address;
    }

    public Bitmap getVenue_image() {
        return venue_image;
    }

    public void setVenue_image(Bitmap venue_image) {
        this.venue_image = venue_image;
    }
}
