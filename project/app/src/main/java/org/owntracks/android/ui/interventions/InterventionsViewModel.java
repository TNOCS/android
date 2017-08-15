package org.owntracks.android.ui.interventions;

import android.content.Context;
import android.content.Intent;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import org.owntracks.android.App;
import org.owntracks.android.activities.ActivityIntervention;
import org.owntracks.android.activities.ActivityRegion;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.db.InterventionDao;
import org.owntracks.android.injection.qualifier.AppContext;
import org.owntracks.android.injection.scopes.PerActivity;
import org.owntracks.android.support.Events;
import org.owntracks.android.ui.base.viewmodel.BaseViewModel;


import java.util.Date;

import javax.inject.Inject;


@PerActivity
public class InterventionsViewModel extends BaseViewModel<InterventionsMvvm.View> implements InterventionsMvvm.ViewModel<InterventionsMvvm.View> {

    private InterventionDao dao;
    private boolean noInterventionsLogged = true;

    @Inject
    public InterventionsViewModel(@AppContext Context context) {
        this.dao = Dao.getInterventionDao();
    }

    public void attachView(@NonNull InterventionsMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public ObservableList<Intervention> getInterventions() {
        ObservableList<Intervention> ivList = new ObservableArrayList<Intervention>();
        ivList.addAll(this.dao.loadAll());
        return ivList;
    }

    public void checkInterventions() {
        this.noInterventionsLogged = (getInterventions().size() == 0);
        notifyPropertyChanged(BR.noInterventionsLogged);
    }

    @Bindable
    public boolean getNoInterventionsLogged() {
        this.checkInterventions();
        return this.noInterventionsLogged;
    }

    @Override
    public void onInterventionClick(Intervention iv, View view) {
        Intent detailIntent = new Intent(view.getContext(), ActivityIntervention.class);
        detailIntent.putExtra("keyId", iv.getId());
        view.getContext().startActivity(detailIntent);
    }
}
