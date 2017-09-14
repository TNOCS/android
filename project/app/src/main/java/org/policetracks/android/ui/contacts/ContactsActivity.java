package org.policetracks.android.ui.contacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import org.policetracks.android.R;
import org.policetracks.android.databinding.UiActivityContactsBinding;
import org.policetracks.android.model.FusedContact;
import org.policetracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class ContactsActivity extends BaseActivity<UiActivityContactsBinding, ContactsMvvm.ViewModel> implements ContactsMvvm.View, org.policetracks.android.ui.contacts.ContactsAdapter.ClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.v("onCreate");
        super.onCreate(savedInstanceState);
        activityComponent().inject(this);
        setAndBindContentView(R.layout.ui_activity_contacts, savedInstanceState);

        setHasEventBus(false);
        setSupportToolbar(binding.toolbar);
        setDrawer(binding.toolbar);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new ContactsAdapter(viewModel.getContacts(), this));
    }



    @Override
    public void onClick(@NonNull FusedContact object, @NonNull View view, boolean longClick) {
        viewModel.onContactClick(object);
    }
}
