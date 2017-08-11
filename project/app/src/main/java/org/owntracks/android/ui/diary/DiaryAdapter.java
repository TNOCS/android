package org.owntracks.android.ui.diary;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.owntracks.android.BR;
import org.owntracks.android.R;
import org.owntracks.android.db.Day;
import org.owntracks.android.db.DayDao;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.ui.base.BaseAdapter;
import org.owntracks.android.ui.base.BaseAdapterItemView;


class DiaryAdapter extends BaseAdapter<Day> {
    DiaryAdapter(ObservableList items, ClickListener clickListener) {
        super(BaseAdapterItemView.of(BR.day, R.layout.ui_row_day));
        setItems(items);
        setClickListener(clickListener);
    }

    interface ClickListener extends BaseAdapter.ClickListener<Day> {
        void onClick(@NonNull Day object, @NonNull View view, boolean longClick);
    }

}
