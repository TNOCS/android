package org.owntracks.android.support;

import android.databinding.BindingConversion;
import android.databinding.InverseBindingAdapter;
import android.widget.EditText;

import org.owntracks.android.support.widgets.HourMinute;

public class CustomBindingAdapters {

    @BindingConversion
    public static String longToTimeStr(Long value) {
        HourMinute hm = HourMinute.fromMillis(value);
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