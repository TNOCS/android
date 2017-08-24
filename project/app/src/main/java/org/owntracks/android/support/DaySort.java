package org.owntracks.android.support;

import org.owntracks.android.db.Day;

import java.util.Comparator;

public class DaySort implements Comparator<Day> {
    @Override
    public int compare(Day d1, Day d2) {
        return d2.getFrom().compareTo(d1.getFrom()); // reversed
    }
}

