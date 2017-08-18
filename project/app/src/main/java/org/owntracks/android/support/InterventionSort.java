package org.owntracks.android.support;

import org.owntracks.android.db.Intervention;

import java.util.Comparator;

public class InterventionSort implements Comparator<Intervention> {
    @Override
    public int compare(Intervention iv1, Intervention iv2) {
        long diff = (iv1.getFrom() - iv2.getFrom());
        if (diff < 0)
            return -1;
        else if (diff > 0)
            return 1;
        else
            return 0;
    }
}

