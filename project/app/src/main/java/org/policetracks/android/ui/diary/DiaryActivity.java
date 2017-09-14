package org.policetracks.android.ui.diary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import org.policetracks.android.R;
import org.policetracks.android.activities.ActivityWelcome;
import org.policetracks.android.databinding.UiActivityDiaryBinding;
import org.policetracks.android.db.Day;
import org.policetracks.android.support.Preferences;
import org.policetracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class DiaryActivity extends BaseActivity<UiActivityDiaryBinding, DiaryMvvm.ViewModel> implements DiaryMvvm.View, org.policetracks.android.ui.diary.DiaryAdapter.ClickListener {
    private Menu mMenu;
    private Switch mTrackingSwitch;

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

        viewModel.setDiaryAdapter(new DiaryAdapter(viewModel.getDays(), this));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(viewModel.getDiaryAdapter());

        mTrackingSwitch = (Switch)findViewById(R.id.trackingswitch);
        mTrackingSwitch.setChecked(Preferences.getPub());
        mTrackingSwitch.setOnCheckedChangeListener(viewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        mTrackingSwitch.setChecked(Preferences.getPub());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(@NonNull Day object, @NonNull View view, boolean longClick) {
        viewModel.onDayClick(object);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_refresh, menu);
        this.mMenu = menu;
        if (viewModel.isTodayAlreadyAdded()) {
            disableAddTodayMenu();
        } else {
            enableAddTodayMenu();
        }
        return true;
    }

    private void disableAddTodayMenu() {
        this.mMenu.findItem(R.id.menu_refresh).getIcon().setAlpha(130);
    }

    private void enableAddTodayMenu() {
        this.mMenu.findItem(R.id.menu_refresh).getIcon().setAlpha(255);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_refresh) {
            viewModel.syncWithServer();
            updateAdapter();
//            return true;
        }
        return false;
    }

    private void updateAdapter() {
        viewModel.updateAdapter();
//        viewModel.checkDays();
//        diaryAdapter.setItems(viewModel.getDays());
//        binding.recyclerView.postInvalidate();
    }
}
