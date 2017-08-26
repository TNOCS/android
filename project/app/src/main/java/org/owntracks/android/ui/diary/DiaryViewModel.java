package org.owntracks.android.ui.diary;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.databinding.library.baseAdapters.BR;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Day;
import org.owntracks.android.db.DayDao;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.db.InterventionDao;
import org.owntracks.android.injection.qualifier.AppContext;
import org.owntracks.android.injection.scopes.PerActivity;
import org.owntracks.android.messages.MessageIntervention;
import org.owntracks.android.services.ServiceProxy;
import org.owntracks.android.support.CustomBindingAdapters;
import org.owntracks.android.support.DaySort;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.InterventionSort;
import org.owntracks.android.support.Preferences;
import org.owntracks.android.support.widgets.HourMinute;
import org.owntracks.android.support.widgets.Toasts;
import org.owntracks.android.ui.base.viewmodel.BaseViewModel;
import org.owntracks.android.ui.interventions.InterventionsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
