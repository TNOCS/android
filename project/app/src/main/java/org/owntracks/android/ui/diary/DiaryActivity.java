package org.owntracks.android.ui.diary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import org.owntracks.android.R;
import org.owntracks.android.activities.ActivityWelcome;
import org.owntracks.android.databinding.UiActivityDiaryBinding;
import org.owntracks.android.model.FusedContact;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class DiaryActivity extends BaseActivity<UiActivityDiaryBinding, DiaryMvvm.ViewModel> implements DiaryMvvm.View, org.owntracks.android.ui.diary.DiaryAdapter.ClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.v("onCreate");
        super.onCreate(savedInstanceState);
        ActivityWelcome.runChecks(this);

        activityComponent().inject(this);
        setAndBindContentView(R.layout.ui_activity_diary, savedInstanceState);

        setHasEventBus(false);
        setSupportToolbar(binding.toolbar);
        setDrawer(binding.toolbar);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new DiaryAdapter(viewModel.getInterventions(), this));
    }



    @Override
    public void onClick(@NonNull Intervention object, @NonNull View view, boolean longClick) {
        viewModel.onInterventionClick(object);
    }
}
