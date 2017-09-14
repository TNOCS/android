package org.policetracks.android.support.widgets;

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

    public String toString() {
        if (hour >= 0 && minute >= 0)
            return String.format("%d:%2d", hour, minute).replace(" ", "0");
        else
            return "--:--";
    }

    // Set hour and minute with the limitation
    public Long toMillisSmart(long dayStart) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dayStart);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        if (c.getTimeInMillis() < dayStart) {
            c.add(Calendar.DAY_OF_YEAR, 1);
        }
        return c.getTimeInMillis();
    }

    public Long toMillis() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        return c.getTimeInMillis();
    }

    // Construct HourMinute object from milliseconds, revert to current time if null
    public static HourMinute fromMillis(Long millis, boolean fallbackToCurrent) {
        HourMinute result = new HourMinute();
        Calendar c = Calendar.getInstance();
        if (millis != null) {
            c.setTimeInMillis(millis);
        }
        if (millis != null || fallbackToCurrent) {
            result.setHour(c.get(Calendar.HOUR_OF_DAY));
            result.setMinute(c.get(Calendar.MINUTE));
        }
        return result;
    }

    public static Calendar getFlooredCalender() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static Calendar getCeiledCalender() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c;
    }
}
