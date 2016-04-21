package example.tanprasit.com.companion_app.models;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import example.tanprasit.com.companion_app.Constants;
import example.tanprasit.com.companion_app.tools.TimeHelper;

/**
 * Created by luketanprasit on 09/04/2016.
 */
public class Key {
    private int id;
    private String takenAt;
    private String returnedAt;
    private Property property;
    private Contractor contractor;
    private int pin;

    public Key(int id, int pin, String takenAt, String returnedAt, Property property, Contractor contractor) {
        this.id = id;
        this.pin = pin;
        this.takenAt = takenAt;
        this.returnedAt = returnedAt;
        this.property = property;
        this.contractor = contractor;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DateTime getTakenAt() {
        String pattern = "YYYY-MM-dd HH:mm:ss";

        return (this.takenAt.equals(Constants.NULL_JODATIME))
                ? null
                : DateTime.parse(this.takenAt,  DateTimeFormat.forPattern(pattern));
    }

    public void setTakenAt(String takenAt) {
        this.takenAt = takenAt;
    }

    public DateTime getReturnedAt() {
        String pattern = "YYYY-MM-dd HH:mm:ss";
        return (this.returnedAt.equals(Constants.NULL_JODATIME))
                ? null
                : DateTime.parse(this.returnedAt,  DateTimeFormat.forPattern(pattern));
    }

    public void setReturnedAt(String returnedAt) {
        this.returnedAt = returnedAt;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;
    }

    public boolean isTaken() {
        return (this.takenAt != null && !this.takenAt.equals(Constants.NULL_JODATIME));
    }

    public boolean isReturned() {
        return (this.returnedAt != null && !this.returnedAt.equals(Constants.NULL_JODATIME));
    }
}
