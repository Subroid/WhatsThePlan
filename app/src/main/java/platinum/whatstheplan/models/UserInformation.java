package platinum.whatstheplan.models;

public class UserInformation {
// todo implement parcelable
    private UserProfile userProfile;
    private UserLocation userLocation;

    public UserInformation() {

    }

    public UserInformation(UserProfile userProfile, UserLocation userLocation) {
        this.userProfile = userProfile;
        this.userLocation = userLocation;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }
}
