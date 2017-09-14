package org.policetracks.android.ui.interventions;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.policetracks.android.R;
import org.policetracks.android.activities.ActivityWelcome;
import org.policetracks.android.databinding.UiActivityInterventionsBinding;
import org.policetracks.android.db.Intervention;
import org.policetracks.android.ui.base.BaseActivity;

import timber.log.Timber;


public class InterventionsActivity extends BaseActivity<UiActivityInterventionsBinding, InterventionsMvvm.ViewModel> implements InterventionsMvvm.View, org.policetracks.android.ui.interventions.InterventionsAdapter.ClickListener {
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

        handleIntentExtras(getIntent());
        viewModel.setInterventionsAdapter(new InterventionsAdapter(viewModel.getInterventions(), this));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(viewModel.getInterventionsAdapter());

    }

    private void handleIntentExtras(Intent intent) {
        Bundle b = getExtrasBundle(intent);
        if(b != null) {
            Timber.v("intent has extras from navigator");
            Long dayId = b.getLong(BUNDLE_KEY_INTERVENTIONS_ID);
            if(dayId != null) {
                viewModel.setDayId(dayId);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntentExtras(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
        handleIntentExtras(getIntent());
    }

    @Override
    public void onClick(@NonNull Intervention object, @NonNull View view, boolean longClick) {
        viewModel.onInterventionClick(object, view.getContext(), longClick);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_interventions, menu);
        this.mMenu = menu;
        this.mMenu.findItem(R.id.menu_addintervention).getIcon().setAlpha(255);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_addintervention) {
            viewModel.addIntervention(this);
            return true;
        }
        return false;
    }

    private void updateAdapter() {
        viewModel.updateAdapter();
//        binding.recyclerView.postInvalidate();
    }
}
