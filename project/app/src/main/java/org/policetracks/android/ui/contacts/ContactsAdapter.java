package org.policetracks.android.ui.contacts;

import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.view.View;

import org.policetracks.android.BR;
import org.policetracks.android.R;
import org.policetracks.android.model.FusedContact;
import org.policetracks.android.ui.base.BaseAdapter;
import org.policetracks.android.ui.base.BaseAdapterItemView;


class ContactsAdapter extends BaseAdapter<FusedContact> {
    ContactsAdapter(ObservableList items, ClickListener clickListener) {
        super(BaseAdapterItemView.of(BR.contact, R.layout.ui_row_contact));
        setItems(items);
        setClickListener(clickListener);
    }

    interface ClickListener extends BaseAdapter.ClickListener<FusedContact> {
        void onClick(@NonNull FusedContact object , @NonNull View view, boolean longClick);
    }

}
