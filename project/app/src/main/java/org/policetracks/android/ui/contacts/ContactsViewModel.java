package org.policetracks.android.ui.contacts;

import android.content.Context;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.policetracks.android.data.repos.ContactsRepo;
import org.policetracks.android.injection.qualifier.AppContext;
import org.policetracks.android.injection.scopes.PerActivity;
import org.policetracks.android.model.FusedContact;
import org.policetracks.android.ui.base.viewmodel.BaseViewModel;
import org.policetracks.android.ui.map.MapActivity;

import javax.inject.Inject;


@PerActivity
public class ContactsViewModel extends BaseViewModel<ContactsMvvm.View> implements ContactsMvvm.ViewModel<ContactsMvvm.View> {

    private final ContactsRepo contactsRepo;

    @Inject
    public ContactsViewModel(@AppContext Context context, ContactsRepo contactsRepo) {
        this.contactsRepo = contactsRepo;

    }

    public void attachView(@NonNull ContactsMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public ObservableList<FusedContact> getContacts() {
        return contactsRepo.getAll();
    }

    @Override
    public void onContactClick(FusedContact c) {
        if(!c.hasLocation())
            return;

        Bundle b = new Bundle();
        b.putString(MapActivity.BUNDLE_KEY_CONTACT_ID, c.getId());
        navigator.get().startActivity(MapActivity.class, b);
    }
}
