package org.owntracks.android.ui.interventions;

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
import org.owntracks.android.databinding.UiActivityInterventionsBinding;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class InterventionsActivity extends BaseActivity<UiActivityInterventionsBinding, InterventionsMvvm.ViewModel> implements InterventionsMvvm.View, org.owntracks.android.ui.interventions.InterventionsAdapter.ClickListener {
    public static final String BUNDLE_KEY_INTERVENTIONS_ID = "BUNDLE_KEY_INTERVENTIONS_ID";

    private Menu mMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.v("onCreate");
        super.onCreate(savedInstanceState);
        ActivityWelcome.runChecks(this);

        activityComponent().inject(this);
        setAndBindContentView(R.layout.ui_activity_interventions, savedInstanceState);

        setHasEventBus(false);
        setSupportToolbar(binding.toolbar);
        setDrawer(binding.toolbar);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(new InterventionsAdapter(viewModel.getInterventions(), this));
    }

    @Override
    public void onClick(@NonNull Intervention object, @NonNull View view, boolean longClick) {
        viewModel.onInterventionClick(object);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_interventions, menu);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_addintervention) {
            viewModel.addIntervention();
            return true;
        }
        return false;
    }
}
