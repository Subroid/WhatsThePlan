package platinum.whatstheplan.models;

public class Guest {

    public String guest_name;
    public String guest_email;

    public Guest() {
    }

    public Guest(String guest_name, String guest_email) {
        this.guest_name = guest_name;
        this.guest_email = guest_email;
    }

    public String getGuest_name() {
        return guest_name;
    }

    public void setGuest_name(String guest_name) {
        this.guest_name = guest_name;
    }

    public String getGuest_email() {
        return guest_email;
    }

    public void setGuest_email(String guest_email) {
        this.guest_email = guest_email;
    }
}
