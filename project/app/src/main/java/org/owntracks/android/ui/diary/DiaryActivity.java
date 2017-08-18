package org.owntracks.android.ui.diary;

import android.app.PendingIntent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.activities.ActivityWelcome;
import org.owntracks.android.databinding.UiActivityDiaryBinding;
import org.owntracks.android.db.Day;
import org.owntracks.android.support.Preferences;
import org.owntracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class DiaryActivity extends BaseActivity<UiActivityDiaryBinding, DiaryMvvm.ViewModel> implements DiaryMvvm.View, org.owntracks.android.ui.diary.DiaryAdapter.ClickListener {
    private Menu mMenu;
    private DiaryAdapter diaryAdapter;
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

        diaryAdapter = new DiaryAdapter(viewModel.getDays(), this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(diaryAdapter);

        mTrackingSwitch = (Switch)findViewById(R.id.trackingswitch);
        mTrackingSwitch.setChecked(Preferences.getPub());
        mTrackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Preferences.setPub(isChecked);
            }
        });
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
        inflater.inflate(R.menu.activity_addtoday, menu);
        this.mMenu = menu;
        if (viewModel.isTodayAlreadyAdded()) {
            disableAddTodayMenu();
        } else {
            enableAddTodayMenu();
        }
        return true;
    }

    private void disableAddTodayMenu() {
        this.mMenu.findItem(R.id.menu_addtoday).getIcon().setAlpha(130);
    }

    private void enableAddTodayMenu() {
        this.mMenu.findItem(R.id.menu_addtoday).getIcon().setAlpha(255);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_addtoday) {
            viewModel.addToday();
            updateAdapter();
            return true;
        }
        return false;
    }

    private void updateAdapter() {
        viewModel.checkDays();
        diaryAdapter.setItems(viewModel.getDays());
        binding.recyclerView.postInvalidate();
    }
}
