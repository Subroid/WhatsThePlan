package platinum.whatstheplan.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserLocation {

    GeoPoint geoPoint;
    @ServerTimestamp
    Date timeStamp;

    public UserLocation() {
    }

    public UserLocation(GeoPoint geoPoint, Date timeStamp) {
        this.geoPoint = geoPoint;
        this.timeStamp = timeStamp;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
