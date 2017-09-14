package org.policetracks.android.ui.diary;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.CompoundButton;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.databinding.library.baseAdapters.BR;

import org.policetracks.android.App;
import org.policetracks.android.db.Dao;
import org.policetracks.android.db.Day;
import org.policetracks.android.db.DayDao;
import org.policetracks.android.db.Intervention;
import org.policetracks.android.db.InterventionDao;
import org.policetracks.android.injection.qualifier.AppContext;
import org.policetracks.android.injection.scopes.PerActivity;
import org.policetracks.android.messages.MessageIntervention;
import org.policetracks.android.services.ServiceProxy;
import org.policetracks.android.support.CustomBindingAdapters;
import org.policetracks.android.support.DaySort;
import org.policetracks.android.support.Events;
import org.policetracks.android.support.Preferences;
import org.policetracks.android.support.widgets.HourMinute;
import org.policetracks.android.support.widgets.Toasts;
import org.policetracks.android.ui.base.viewmodel.BaseViewModel;
import org.policetracks.android.ui.interventions.InterventionsActivity;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


@PerActivity
public class DiaryViewModel extends BaseViewModel<DiaryMvvm.View> implements DiaryMvvm.ViewModel<DiaryMvvm.View>, CompoundButton.OnCheckedChangeListener {

    private DayDao dao;
    private boolean noDaysLogged = true;
    private DaySort daySort = new DaySort();
    private DiaryAdapter diaryAdapter;

    public DiaryAdapter getDiaryAdapter() {
        return diaryAdapter;
    }

    public void setDiaryAdapter(DiaryAdapter diaryAdapter) {
        this.diaryAdapter = diaryAdapter;
    }

    @Inject
    public DiaryViewModel(@AppContext Context context) {
        this.dao = Dao.getDayDao();
    }

    public void attachView(@NonNull DiaryMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public ObservableList<Day> getDays() {
        ObservableList<Day> dayList = new ObservableArrayList<Day>();
        dayList.addAll(this.dao.loadAll());
        Collections.sort(dayList, daySort);
        return dayList;
    }

    public void checkDays() {
        this.noDaysLogged = (getDays().size() == 0);
        notifyPropertyChanged(BR.noDaysLogged);
    }

    @Bindable
    public boolean getNoDaysLogged() {
        this.checkDays();
        return this.noDaysLogged;
    }

    @Override
    public boolean isTodayAlreadyAdded() {
        boolean alreadyAdded = false;
        Calendar today = Calendar.getInstance();
        Calendar loggedDate = Calendar.getInstance();
        for (Day day: getDays()) {
            if (day.getTo() == null) break;
            loggedDate.setTimeInMillis(day.getTo());
            // If the last day/shift ended within 4 hours, continue the shift
            if ( today.getTimeInMillis() - loggedDate.getTimeInMillis() < TimeUnit.HOURS.toMillis(4)) {
//            if ( today.getTimeInMillis() - loggedDate.getTimeInMillis() < TimeUnit.MINUTES.toMillis(2)) {
                alreadyAdded = true;
                break;
            }
        }
        return alreadyAdded;
    }

    @Override
    public void updateAdapter() {
        diaryAdapter.setItems(getDays());
        checkDays();
    }

    @Override
    public void addToday() {
        if (isTodayAlreadyAdded()) return;
        Day day = new Day();
        day.setFrom(HourMinute.getFlooredCalender().getTimeInMillis());
        day.setTo(null);
        String description = CustomBindingAdapters.longToDateTimeStr(day.getFrom()) + " - ...";
        day.setDescription(description);
        this.dao.insert(day);
        this.checkDays();
        App.getEventBus().post(new Events.DayAdded(day));
        Toasts.showStartDay();
    }

    private void resumeToday() {
        if (getDays().isEmpty()) return;
        Day day = getDays().get(0);
        day.setTo(null);
        String description = CustomBindingAdapters.longToDateTimeStr(day.getFrom()) + " - ...";
        day.setDescription(description);
        this.dao.update(day);
        this.checkDays();
        App.getEventBus().post(new Events.DayUpdated(day));
        Toasts.showResumeDay();
    }

    private void endToday() {
        if (getDays().isEmpty()) return;
        Day day = getDays().get(0);
        day.setTo(HourMinute.getCeiledCalender().getTimeInMillis());
        String description = CustomBindingAdapters.longToDateTimeStr(day.getFrom());
        description += " - " + CustomBindingAdapters.longToDateTimeStr(day.getTo());
        day.setDescription(description);
        this.dao.update(day);
        this.checkDays();
        App.getEventBus().post(new Events.DayUpdated(day));
    }

    @Override
    public void onDayClick(Day day) {
        Bundle b = new Bundle();
        b.putLong(InterventionsActivity.BUNDLE_KEY_INTERVENTIONS_ID, day.getId());
        navigator.get().startActivity(InterventionsActivity.class, b);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        Preferences.setPub(isChecked);
        if (isChecked) {
            if (isTodayAlreadyAdded()) {
                this.resumeToday();
            } else {
                this.addToday();
            }
        } else {
            this.endToday();
        }
        updateAdapter();
        ServiceProxy.getServiceNotification().updateNotificationOngoing();
    }

    @Override
    public void syncWithServer() {
        sendLocalInterventions();
//        getRemoteInterventions();
    }

    private void sendLocalInterventions() {
        InterventionDao dao = Dao.getInterventionDao();
        List<Intervention> list = dao.loadAll();
        for (Intervention iv : list) {
            App.getEventBus().post(new Events.InterventionAdded(iv)); // For ServiceLocator update
        }
        Toasts.showInterventionsSent();
    }

    private void getRemoteInterventions() {
        MessageIntervention message = new MessageIntervention();
        message._custom_endpoint = true; // Send it to the json webservice
        message._custom_CRUD = "G"; // Get it
        ServiceProxy.getServiceMessage().sendMessage(message);
    }
}
