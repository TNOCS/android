package org.owntracks.android.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import timber.log.Timber;

public class Intervention extends BaseObservable {
    private static final String TAG = "Intervention";

    private String title;
    private final String id;

    public Intervention(@Nullable String id) {
        this.id = id != null ? id : "NOID";
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    @Bindable
    public @NonNull String getId() {
        return id;
    }
}
