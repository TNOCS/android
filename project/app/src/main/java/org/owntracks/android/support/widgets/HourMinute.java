package org.owntracks.android.support.widgets;

import java.util.Calendar;

/**
 * Created by rbruining on 15-8-2017.
 */

public class HourMinute {
    private int hour;
    private int minute;

    public HourMinute() {
        hour = -1;
        minute = -1;
    }

    public HourMinute(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    // Construct HourMinute object from milliseconds, revert to current time if null
    public Long toMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTimeInMillis();
    }

    // Construct HourMinute object from milliseconds, revert to current time if null
    public static HourMinute fromMillis(Long millis) {
        HourMinute result = new HourMinute();
        Calendar c = Calendar.getInstance();
        if (millis != null) c.setTimeInMillis(millis);
        result.setHour(c.get(Calendar.HOUR_OF_DAY));
        result.setMinute(c.get(Calendar.MINUTE));
        return result;
    }
}
