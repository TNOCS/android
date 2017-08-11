package org.owntracks.android.ui.diary;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Day;
import org.owntracks.android.db.DayDao;
import org.owntracks.android.injection.qualifier.AppContext;
import org.owntracks.android.injection.scopes.PerActivity;
import org.owntracks.android.messages.MessageDay;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.ui.base.viewmodel.BaseViewModel;

import java.util.LinkedList;

import javax.inject.Inject;


@PerActivity
public class DiaryViewModel extends BaseViewModel<DiaryMvvm.View> implements DiaryMvvm.ViewModel<DiaryMvvm.View> {

    private DayDao dao;

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

    @Override
    public void onDayClick(Day day) {
//        Bundle b = new Bundle();
//        b.putString(MapActivity.BUNDLE_KEY_CONTACT_ID, c.getId());
//        navigator.get().startActivity(MapActivity.class, b);
    }
}
