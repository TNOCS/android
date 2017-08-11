package org.owntracks.android.ui.diary;

import android.content.Context;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.owntracks.android.injection.qualifier.AppContext;
import org.owntracks.android.injection.scopes.PerActivity;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.ui.base.viewmodel.BaseViewModel;

import javax.inject.Inject;


@PerActivity
public class DiaryViewModel extends BaseViewModel<DiaryMvvm.View> implements DiaryMvvm.ViewModel<DiaryMvvm.View> {


    @Inject
    public DiaryViewModel(@AppContext Context context) {

    }

    public void attachView(@NonNull DiaryMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Override
    public ObservableList<Intervention> getInterventions() {
        return null;
    }

    @Override
    public void onInterventionClick(Intervention c) {
//        Bundle b = new Bundle();
//        b.putString(MapActivity.BUNDLE_KEY_CONTACT_ID, c.getId());
//        navigator.get().startActivity(MapActivity.class, b);
    }
}
