package org.owntracks.android.ui.interventions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.activities.ActivityIntervention;
import org.owntracks.android.activities.ActivityRegion;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Day;
import org.owntracks.android.db.DayDao;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.db.InterventionDao;
import org.owntracks.android.injection.qualifier.AppContext;
import org.owntracks.android.injection.scopes.PerActivity;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.InterventionSort;
import org.owntracks.android.ui.base.viewmodel.BaseViewModel;


import java.sql.Time;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.inject.Inject;

import timber.log.Timber;


@PerActivity
public class InterventionsViewModel extends BaseViewModel<InterventionsMvvm.View> implements InterventionsMvvm.ViewModel<InterventionsMvvm.View> {

    private InterventionDao ivDao;
    private DayDao dayDao;
    private boolean noInterventionsLogged = true;
    private Long dayId;
    private InterventionsAdapter interventionsAdapter;
    private InterventionSort interventionSort = new InterventionSort();

    public InterventionsAdapter getInterventionsAdapter() {
        return interventionsAdapter;
    }

    public void setInterventionsAdapter(InterventionsAdapter interventionsAdapter) {
        this.interventionsAdapter = interventionsAdapter;
    }


    public Long getDayId() {
        return dayId;
    }

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    @Inject
    public InterventionsViewModel(@AppContext Context context) {
        this.ivDao = Dao.getInterventionDao();
        this.dayDao = Dao.getDayDao();
    }

    public void attachView(@NonNull InterventionsMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public ObservableList<Intervention> getInterventions() {
        Day day = this.dayDao.loadByRowId(getDayId());
        Calendar dayCal = getDayCalendar(day);

        ObservableList<Intervention> ivList = new ObservableArrayList<Intervention>();
        for (Intervention iv: this.ivDao.loadAll()) {
            Calendar ivCal = getInterventionCalendar(iv);
            if (ivCal.get(Calendar.DAY_OF_YEAR) == dayCal.get(Calendar.DAY_OF_YEAR)
                    && ivCal.get(Calendar.YEAR) == dayCal.get(Calendar.YEAR)) {
                ivList.add(iv);
            }
        }
        Collections.sort(ivList, interventionSort);
        return ivList;
    }

    private Calendar getDayCalendar(Day day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day.getDate());
        return cal;
    }

    private Calendar getInterventionCalendar(Intervention iv) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(iv.getFrom());
        return cal;
    }

    public void checkInterventions() {
        if (this.noInterventionsLogged == (getInterventions().size() == 0))
            return;
        this.noInterventionsLogged = (getInterventions().size() == 0);
        notifyPropertyChanged(BR.noInterventionsLogged);
    }

    public void updateAdapter() {
        interventionsAdapter.setItems(getInterventions());
        checkInterventions();
    }

    public void deleteIntervention(Long id) {
        InterventionDao dao = Dao.getInterventionDao();
        Intervention iv = dao.loadByRowId(id);
        if (iv == null) {
            Timber.v("Cannot delete intervention null");
            return;
        }
        dao.delete(iv);
        updateAdapter();
        App.getEventBus().post(new Events.InterventionRemoved(iv)); // For ServiceLocator update
    }

    @Bindable
    public boolean getNoInterventionsLogged() {
        this.checkInterventions();
        return this.noInterventionsLogged;
    }

    @Override
    public void onInterventionClick(Intervention iv, View view, boolean longClick) {
        if (longClick == false) {
            Intent detailIntent = new Intent(view.getContext(), ActivityIntervention.class);
            detailIntent.putExtra("keyId", iv.getId());
            view.getContext().startActivity(detailIntent);
        } else {
            final Long ivId = iv.getId();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle(view.getResources().getString(R.string.delete_intervention))
                    .setMessage(view.getResources().getString(R.string.delete_intervention_quest))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteIntervention(ivId);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .show();
        }
    }
}
