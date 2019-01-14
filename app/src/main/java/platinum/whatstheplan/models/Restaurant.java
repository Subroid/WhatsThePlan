package platinum.whatstheplan.models;

import com.google.firebase.firestore.GeoPoint;

public class Restaurant {

    private String name;
    private String address;
    private GeoPoint geoLocation;
    private String image;
    private String id;

    public Restaurant() {
    }

    public Restaurant(String name, String address, GeoPoint geoLocation, String image) {
        this.name = name;
        this.address = address;
        this.geoLocation = geoLocation;
        this.image = image;
    }

    public Restaurant(String name, String address, GeoPoint geoLocation, String image, String id) {
        this.name = name;
        this.address = address;
        this.geoLocation = geoLocation;
        this.image = image;
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
