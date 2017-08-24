package org.owntracks.android.support;

import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.widget.EditText;

import org.owntracks.android.support.widgets.HourMinute;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomBindingAdapters {

    @BindingConversion
    public static String longToDateTimeStr(Long value) {
        if (value == null) return "--:--";
        SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM HH:mm");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(value);
        return formatter.format(c.getTime());
    }

    @BindingConversion
    public static String longToTimeStr(Long value) {
        HourMinute hm = HourMinute.fromMillis(value, false);
        return hm.toString();
    }

    @InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
    public static Long captureLongValue(EditText view) {
        long value = 0;
        try {
            value = Long.parseLong(view.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return value;
    }
}