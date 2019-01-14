package platinum.whatstheplan.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Event implements Parcelable{

    private String event_name;
    private String event_type;
    private String event_subtype;
    private String venue_name;
    private String venue_id;
    private String venue_address;
    private String venue_image;
    private String event_date;
    private String event_time;
    private String event_id;
    private String event_image;
    private String admin_id;
    private GeoPoint event_geopoint;
    private int event_tickets;

    public Event() {
    }

    public Event(String event_name, String event_type, String event_subtype, String venue_name, String venue_id, String venue_address, String venue_image, String event_date, String event_time, String event_id, String event_image, String admin_id, GeoPoint event_geopoint, int event_tickets) {
        this.event_name = event_name;
        this.event_type = event_type;
        this.event_subtype = event_subtype;
        this.venue_name = venue_name;
        this.venue_id = venue_id;
        this.venue_address = venue_address;
        this.venue_image = venue_image;
        this.event_date = event_date;
        this.event_time = event_time;
        this.event_id = event_id;
        this.event_image = event_image;
        this.admin_id = admin_id;
        this.event_geopoint = event_geopoint;
        this.event_tickets = event_tickets;
    }

    protected Event(Parcel in) {
        event_name = in.readString();
        event_type = in.readString();
        event_subtype = in.readString();
        venue_name = in.readString();
        venue_id = in.readString();
        venue_address = in.readString();
        venue_image = in.readString();
        event_date = in.readString();
        event_time = in.readString();
        event_id = in.readString();
        event_image = in.readString();
        admin_id = in.readString();
        Double lat = in.readDouble();
        Double lng = in.readDouble();
        event_geopoint = new GeoPoint(lat, lng);
        event_tickets = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getEvent_subtype() {
        return event_subtype;
    }

    public void setEvent_subtype(String event_subtype) {
        this.event_subtype = event_subtype;
    }

    public String getVenue_name() {
        return venue_name;
    }

    public void setVenue_name(String venue_name) {
        this.venue_name = venue_name;
    }

    public String getVenue_id() {
        return venue_id;
    }

    public void setVenue_id(String venue_id) {
        this.venue_id = venue_id;
    }

    public String getVenue_address() {
        return venue_address;
    }

    public void setVenue_address(String venue_address) {
        this.venue_address = venue_address;
    }

    public String getVenue_image() {
        return venue_image;
    }

    public void setVenue_image(String venue_image) {
        this.venue_image = venue_image;
    }

    public String getEvent_date() {
        return event_date;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public String getEvent_time() {
        return event_time;
    }

    public void setEvent_time(String event_time) {
        this.event_time = event_time;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_image() {
        return event_image;
    }

    public void setEvent_image(String event_image) {
        this.event_image = event_image;
    }

    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(String admin_id) {
        this.admin_id = admin_id;
    }

    public GeoPoint getEvent_geopoint() {
        return event_geopoint;
    }

    public void setEvent_geopoint(GeoPoint event_geopoint) {
        this.event_geopoint = event_geopoint;
    }

    public int getEvent_tickets() {
        return event_tickets;
    }

    public void setEvent_tickets(int event_tickets) {
        this.event_tickets = event_tickets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(event_name);
        parcel.writeString(event_type);
        parcel.writeString(event_subtype);
        parcel.writeString(venue_name);
        parcel.writeString(venue_id);
        parcel.writeString(venue_address);
        parcel.writeString(venue_image);
        parcel.writeString(event_date);
        parcel.writeString(event_time);
        parcel.writeString(event_id);
        parcel.writeString(event_image);
        parcel.writeString(admin_id);
        parcel.writeDouble(event_geopoint.getLatitude());
        parcel.writeDouble(event_geopoint.getLongitude());
        parcel.writeInt(event_tickets);

    }
}
