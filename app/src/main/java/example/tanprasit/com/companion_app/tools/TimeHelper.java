package example.tanprasit.com.companion_app.tools;

/**
 * Created by luketanprasit on 13/04/2016.
 */

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import example.tanprasit.com.companion_app.Constants;


public class TimeHelper {
    private DateTime dateTime;
    private DateTimeZone timeZone = DateTimeZone.forID(Constants.TIMEZONE_UK);

    public TimeHelper() {
        // Whenever we deal with time set the default time to now and set default timezone
        // to Europe London.
        this.dateTime = new DateTime();
        this.dateTime = dateTime.withZone(this.timeZone);
    }

    /**
     * Create a human readable string from a timestamp. Taking into consideration daylight saving time.
     * @return a string of time e.g. 30 December 1993 12:00
     */
    public String getReadableDateAndTime() {
        return this.dateTime.toString("dd MMMM yyyy HH:mm");
    }

    public String getReadableTime() {
        return this.dateTime.toString("HH:mm");
    }

    public String getReadableDate() {
        return this.dateTime.toString("dd MMMM yyyy");
    }

    public String getReadableTimeForCard() {
        return this.dateTime.toString("HH:mm");
    }

    public String getReadableDateForCard() {
        return this.dateTime.toString("EEE dd MMM");
    }

    // According to the docs, the only way you can set time from long is to construct a new date
    // time object. Also, we need to ensure that we set the correct timezone.
    // MUST be in milliseconds since midnight GMT on 1 Jan 1970.
    public void setCurrentTimeFromLong(long timeAsLong) {
        this.dateTime = new DateTime(timeAsLong);
        this.dateTime = this.dateTime.withZone(this.timeZone);
    }

    public void setCurrentTimeFromTimestamp(String timestamp) {
        LocalDateTime ldt = DateTimeFormat.forPattern(timestamp).parseLocalDateTime(timestamp);
        this.dateTime = ldt.toDateTime();
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
        this.dateTime = this.dateTime.withZone(this.timeZone);
    }

    public long getConvertedTimeToLong() {
        long offset = this.timeZone.getOffset(new Instant());
        return this.dateTime.plusMillis((int) offset).getMillis();
    }

}