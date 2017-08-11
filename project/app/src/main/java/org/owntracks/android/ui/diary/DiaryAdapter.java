package org.owntracks.android.ui.diary;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.owntracks.android.BR;
import org.owntracks.android.R;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.ui.base.BaseAdapter;
import org.owntracks.android.ui.base.BaseAdapterItemView;


class DiaryAdapter extends BaseAdapter<Intervention> {
    DiaryAdapter(ObservableList items, ClickListener clickListener) {
        super(BaseAdapterItemView.of(BR.intervention, R.layout.ui_row_intervention));
        setItems(items);
        setClickListener(clickListener);
    }

    interface ClickListener extends BaseAdapter.ClickListener<Intervention> {
        void onClick(@NonNull Intervention object , @NonNull View view, boolean longClick);
    }

}
