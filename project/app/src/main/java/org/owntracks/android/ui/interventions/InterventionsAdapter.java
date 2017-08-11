package org.owntracks.android.ui.interventions;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.owntracks.android.BR;
import org.owntracks.android.R;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.ui.base.BaseAdapter;
import org.owntracks.android.ui.base.BaseAdapterItemView;


class InterventionsAdapter extends BaseAdapter<Intervention> {
    InterventionsAdapter(ObservableList items, ClickListener clickListener) {
        super(BaseAdapterItemView.of(BR.intervention, R.layout.ui_row_intervention));
        setItems(items);
        setClickListener(clickListener);
    }

    interface ClickListener extends BaseAdapter.ClickListener<Intervention> {
        void onClick(@NonNull Intervention object, @NonNull View view, boolean longClick);
    }

}
