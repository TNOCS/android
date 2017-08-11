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

import org.owntracks.android.R;
import org.owntracks.android.activities.ActivityWelcome;
import org.owntracks.android.databinding.UiActivityDiaryBinding;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Day;
import org.owntracks.android.db.DayDao;
import org.owntracks.android.model.FusedContact;
import org.owntracks.android.model.Intervention;
import org.owntracks.android.services.ServiceProxy;
import org.owntracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class DiaryActivity extends BaseActivity<UiActivityDiaryBinding, DiaryMvvm.ViewModel> implements DiaryMvvm.View, org.owntracks.android.ui.diary.DiaryAdapter.ClickListener {
    private Menu mMenu;

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
        binding.recyclerView.setAdapter(new DiaryAdapter(viewModel.getDays(), this));
    }

    @Override
    public void onClick(@NonNull Day object, @NonNull View view, boolean longClick) {
        viewModel.onDayClick(object);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_addtoday, menu);
        this.mMenu = menu;
        if(!viewModel.isTodayAlreadyAdded())
            disableAddTodayMenu();
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
            this.addToday();
            return true;
        }
        return false;
    }

    private void addToday() {
        viewModel.addToday();
    }
}
