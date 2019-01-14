package platinum.whatstheplan.models;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.GeoPoint;

public class Party {

    private String event_name;
    private String venue_address;
    //todo GeoLocation event_geolocaion; //maybe not necessary
    private GeoPoint event_geopoint;
    private String event_image;
    private String event_id;

    public Party() {
    }

    public Party(String event_name, String venue_address, String event_image) {
        this.event_name = event_name;
        this.venue_address = venue_address;
        this.event_image = event_image;
    }

    public Party(String event_name, String venue_address, GeoPoint event_geopoint, String event_image, String event_id) {
        this.event_name = event_name;
        this.venue_address = venue_address;
        this.event_geopoint = event_geopoint;
        this.event_image = event_image;
        this.event_id = event_id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getVenue_address() {
        return venue_address;
    }

    public void setVenue_address(String venue_address) {
        this.venue_address = venue_address;
    }

    public GeoPoint getEvent_geopoint() {
        return event_geopoint;
    }

    public void setEvent_geopoint(GeoPoint event_geopoint) {
        this.event_geopoint = event_geopoint;
    }

    public String getEvent_image() {
        return event_image;
    }

    public void setEvent_image(String event_image) {
        this.event_image = event_image;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }
}
