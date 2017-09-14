package org.policetracks.android.ui.interventions;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.policetracks.android.BR;
import org.policetracks.android.R;
import org.policetracks.android.db.Intervention;
import org.policetracks.android.ui.base.BaseAdapter;
import org.policetracks.android.ui.base.BaseAdapterItemView;


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
