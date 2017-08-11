package org.owntracks.android.ui.diary;

import android.content.Context;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.widget.TextView;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.owntracks.android.App;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Day;
import org.owntracks.android.db.DayDao;
import org.owntracks.android.injection.qualifier.AppContext;
import org.owntracks.android.injection.scopes.PerActivity;
import org.owntracks.android.messages.MessageDay;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.support.Events;
import org.owntracks.android.ui.base.viewmodel.BaseViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;


@PerActivity
public class DiaryViewModel extends BaseViewModel<DiaryMvvm.View> implements DiaryMvvm.ViewModel<DiaryMvvm.View> {

    private DayDao dao;
    private boolean noDaysLogged = true;

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
        return dayList;
    }

    @Bindable
    public boolean getNoDaysLogged() {
        this.noDaysLogged = (getDays().size() == 0);
        return this.noDaysLogged;
    }

    @Override
    public boolean isTodayAlreadyAdded() {
        boolean alreadyAdded = false;
        for (Day day: getDays()) {
            Calendar today = Calendar.getInstance();
            Calendar loggedDate = Calendar.getInstance();
            loggedDate.setTime(day.getDate());
            if ( loggedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                    && loggedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                alreadyAdded = true;
                break;
            }
        }
        return alreadyAdded;
    }

    @Override
    public void addToday() {
        if (isTodayAlreadyAdded())
            return;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        Day day = new Day();
        day.setDate(date);
        day.setDescription(formatter.format(date));
        this.dao.insert(day);
        App.getEventBus().post(new Events.DayAdded(day));
    }

    @Override
    public void onDayClick(Day day) {
//        Bundle b = new Bundle();
//        b.putString(MapActivity.BUNDLE_KEY_CONTACT_ID, c.getId());
//        navigator.get().startActivity(MapActivity.class, b);
    }
}
