package platinum.whatstheplan.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class RestaurantVenue implements Parcelable {

    private String venue_id;
    private String venue_name;
    private String venue_address;
    private String venue_type;
    private String venue_main_event;
    private String venue_sub_events;
    private String venue_image;
    private String admin_id;
    private int venue_tickets;  //require merge
    private String venue_date;

    private GeoPoint venue_geopoint;

    public RestaurantVenue() {
    }

    public RestaurantVenue(String venue_id, String venue_name, String venue_address, String venue_type, String venue_main_event, String venue_sub_events, String venue_image, String admin_id, GeoPoint venue_geopoint) {
        this.venue_id = venue_id;
        this.venue_name = venue_name;
        this.venue_address = venue_address;
        this.venue_type = venue_type;
        this.venue_main_event = venue_main_event;
        this.venue_sub_events = venue_sub_events;
        this.venue_image = venue_image;
        this.admin_id = admin_id;
        this.venue_geopoint = venue_geopoint;
    }

    public RestaurantVenue(String venue_id, String venue_name, String venue_address, String venue_type, String venue_main_event, String venue_sub_events, String venue_image, String admin_id, String venue_date, GeoPoint venue_geopoint) {
        this.venue_id = venue_id;
        this.venue_name = venue_name;
        this.venue_address = venue_address;
        this.venue_type = venue_type;
        this.venue_main_event = venue_main_event;
        this.venue_sub_events = venue_sub_events;
        this.venue_image = venue_image;
        this.admin_id = admin_id;
        this.venue_date = venue_date;
        this.venue_geopoint = venue_geopoint;
    }
    //    public RestaurantVenue(String venue_id, String venue_name, String venue_address, String venue_type, String venue_main_event, String venue_sub_events, String venue_image, String admin_id, int venue_tickets, GeoPoint venue_geopoint) {
//        this.venue_id = venue_id;
//        this.venue_name = venue_name;
//        this.venue_address = venue_address;
//        this.venue_type = venue_type;
//        this.venue_main_event = venue_main_event;
//        this.venue_sub_events = venue_sub_events;
//        this.venue_image = venue_image;
//        this.admin_id = admin_id;
//        this.venue_tickets = venue_tickets;
//        this.venue_geopoint = venue_geopoint;
//    }

    protected RestaurantVenue(Parcel in) {
        venue_id = in.readString();
        venue_name = in.readString();
        venue_address = in.readString();
        venue_type = in.readString();
        venue_main_event = in.readString();
        venue_sub_events = in.readString();
        venue_image = in.readString();
        admin_id = in.readString();
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        venue_geopoint = new GeoPoint(lat, lng);
        venue_date = in.readString();
    }

    public static final Creator<RestaurantVenue> CREATOR = new Creator<RestaurantVenue>() {
        @Override
        public RestaurantVenue createFromParcel(Parcel in) {
            return new RestaurantVenue(in);
        }

        @Override
        public RestaurantVenue[] newArray(int size) {
            return new RestaurantVenue[size];
        }
    };

    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_id) {
        this.venue_id = venue_id;
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

    public String getVenue_type() {
        return venue_type;
    }

    public void setVenue_type(String venue_type) {
        this.venue_type = venue_type;
    }

    public String getVenue_main_event() {
        return venue_main_event;
    }

    public void setVenue_main_event(String venue_main_event) {
        this.venue_main_event = venue_main_event;
    }

    public String getVenue_sub_events() {
        return venue_sub_events;
    }

    public void setVenue_sub_events(String venue_sub_events) {
        this.venue_sub_events = venue_sub_events;
    }

    public String getVenue_image() {
        return venue_image;
    }

    public void setVenue_image(String venue_image) {
        this.venue_image = venue_image;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public GeoPoint getVenue_geopoint() {
        return venue_geopoint;
    }

    public void setVenue_geopoint(GeoPoint venue_geopoint) {
        this.venue_geopoint = venue_geopoint;
    }


    public int getVenue_tickets() {
        return venue_tickets;
    }

    public void setVenue_tickets(int venue_tickets) {
        this.venue_tickets = venue_tickets;
    }

    public String getVenue_date() {
        return venue_date;
    }

    public void setVenue_date(String venue_date) {
        this.venue_date = venue_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(venue_id);
        parcel.writeString(venue_name);
        parcel.writeString(venue_address);
        parcel.writeString(venue_type);
        parcel.writeString(venue_main_event);
        parcel.writeString(venue_sub_events);
        parcel.writeString(venue_image);
        parcel.writeString(admin_id);
        parcel.writeDouble(venue_geopoint.getLatitude());
        parcel.writeDouble(venue_geopoint.getLongitude());
        parcel.writeString(venue_date);
    }
}
