package org.policetracks.android.ui.diary;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.policetracks.android.BR;
import org.policetracks.android.R;
import org.policetracks.android.db.Day;
import org.policetracks.android.ui.base.BaseAdapter;
import org.policetracks.android.ui.base.BaseAdapterItemView;


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
