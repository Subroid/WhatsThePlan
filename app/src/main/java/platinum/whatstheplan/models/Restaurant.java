package platinum.whatstheplan.models;

import com.google.firebase.firestore.GeoPoint;

public class Restaurant {

    private String name;
    private String address;
    private GeoPoint geoLocation;

    public Restaurant() {
    }

    public Restaurant(String name, String address, GeoPoint geoLocation) {
        this.name = name;
        this.address = address;
        this.geoLocation = geoLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public GeoPoint getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoPoint geoLocation) {
        this.geoLocation = geoLocation;
    }
}
