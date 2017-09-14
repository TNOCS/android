package org.policetracks.android.ui.interventions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.databinding.library.baseAdapters.BR;

import org.policetracks.android.App;
import org.policetracks.android.R;
import org.policetracks.android.activities.ActivityIntervention;
import org.policetracks.android.db.Dao;
import org.policetracks.android.db.Day;
import org.policetracks.android.db.DayDao;
import org.policetracks.android.db.Intervention;
import org.policetracks.android.db.InterventionDao;
import org.policetracks.android.injection.qualifier.AppContext;
import org.policetracks.android.injection.scopes.PerActivity;
import org.policetracks.android.support.Events;
import org.policetracks.android.support.InterventionSort;
import org.policetracks.android.ui.base.viewmodel.BaseViewModel;


import java.util.Calendar;
import java.util.Collections;

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
        Calendar from = Calendar.getInstance();
        from.setTimeInMillis(day.getFrom());
        Calendar to = null;
        if (day.getTo() != null) {
            to = Calendar.getInstance();
            to.setTimeInMillis(day.getTo());
        }

        ObservableList<Intervention> ivList = new ObservableArrayList<Intervention>();
        for (Intervention iv: this.ivDao.loadAll()) {
            Calendar ivCal = getInterventionCalendar(iv);
            if (ivCal.getTimeInMillis() >= from.getTimeInMillis()) {
                if (to != null && ivCal.getTimeInMillis() <= to.getTimeInMillis()) {
                    ivList.add(iv);
                } else if (to == null) {
                    ivList.add(iv);
                }
            }
        }
        Collections.sort(ivList, interventionSort);
        return ivList;
    }

    private Calendar getDayCalendar(Day day) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(day.getFrom());
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

    public void addIntervention(Context context) {
        this.openInterventionActivity(context, null);
    }

    private void openInterventionActivity(Context context, Intervention iv) {
        Intent detailIntent = new Intent(context, ActivityIntervention.class);
        if (iv != null) {
            detailIntent.putExtra("ivId", iv.getId());
        }
        detailIntent.putExtra("dayStartId", this.dayDao.loadByRowId(this.dayId).getFrom());
        detailIntent.putExtra("dayEndId", this.dayDao.loadByRowId(this.dayId).getTo());
        context.startActivity(detailIntent);
    }

    @Bindable
    public boolean getNoInterventionsLogged() {
        this.checkInterventions();
        return this.noInterventionsLogged;
    }

    @Override
    public void onInterventionClick(Intervention iv, Context context, boolean longClick) {
        if (longClick == false) {
            this.openInterventionActivity(context, iv);
        } else {
            final Long ivId = iv.getId();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.delete_intervention))
                    .setMessage(context.getResources().getString(R.string.delete_intervention_quest))
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
