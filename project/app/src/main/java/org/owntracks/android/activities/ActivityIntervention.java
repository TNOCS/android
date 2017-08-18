package org.owntracks.android.activities;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.databinding.ActivityInterventionBinding;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.db.InterventionDao;
import org.owntracks.android.services.ServiceProxy;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.SimpleTextChangeListener;
import org.owntracks.android.support.widgets.HourMinute;
import org.owntracks.android.support.widgets.Toasts;

import java.lang.reflect.Array;

import timber.log.Timber;


public class ActivityIntervention extends ActivityBase implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "ActivityIntervention";
    public static final String BUNDLE_KEY_INTERVENTION_ID = "BUNDLE_KEY_INTERVENTION_ID";

    private InterventionDao dao;
    private Intervention iv;
    private boolean update = true;

    private MenuItem saveButton;
    private ActivityInterventionBinding binding;
    private String[] interventionTypes = new String[]{"No interventions defined"};
    private String[] interventionSubtypes = new String[]{};
    private ArrayAdapter<String> subTypesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, ServiceProxy.class));
        ServiceProxy.runOrBind(this, new Runnable() {
            @Override
            public void run() {
                Log.v("ActivityIntervention", "ServiceProxy bound");
            }
        });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_intervention);

        setSupportToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.dao = Dao.getInterventionDao();

        if (hasIntentExtras()) {
            this.iv = this.dao.loadByRowId(getIntent().getExtras().getLong("keyId"));
        }

        if(this.iv == null) {
            this.update = false;
            this.iv = new Intervention();
            this.iv.setDefaults();
        }

        this.initializeSpinner();
        this.initializeSubSpinner(this.interventionSubtypes, true);

        binding.startTime.setOnClickListener(this);
        binding.endTime.setOnClickListener(this);

        binding.setItem(this.iv);

        setupListenerAndRequiredFields();
    }

    private void initializeSpinner() {
        this.interventionTypes = getResources().getStringArray(R.array.intervention_types);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, this.interventionTypes); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIvtype.setAdapter(spinnerArrayAdapter);
        binding.spinnerIvtype.setOnItemSelectedListener(this);
        if (this.iv.getType() == null) return;
        int pos = spinnerArrayAdapter.getPosition(this.iv.getType());
        binding.spinnerIvtype.setSelection(pos);
    }

    private void initializeSubSpinner(String[] interventionSubtypes, boolean firstCall) {
        this.subTypesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, interventionSubtypes); //selected item will look like a spinner set from XML
        this.subTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIvSubtype.setAdapter(this.subTypesAdapter);
        if (firstCall) {
            binding.spinnerIvSubtype.setOnItemSelectedListener(this);
            if (this.iv.getSubtype() == null) return;
            int pos = this.subTypesAdapter.getPosition(this.iv.getSubtype());
            binding.spinnerIvSubtype.setSelection(pos);
        }
    }

    private void setupListenerAndRequiredFields() {
        TextWatcher requiredForSave = new SimpleTextChangeListener() {
            @Override
            public void onChanged(String s) {
                iv.setComment(binding.comment.getText().toString());
                conditionallyEnableSaveButton();
            }
        };
        binding.comment.addTextChangedListener(requiredForSave);
    }

    private void conditionallyEnableSaveButton() {
        boolean enabled = true;
        try {
            if (binding.spinnerIvtype.getSelectedItem() == null) enabled = false;
            if (iv.getFrom() == null) enabled = false;
            if (iv.getTo() == null) enabled = false;
            if (iv.getFrom() >= iv.getTo()) enabled = false;
        } catch (Exception e) {
            enabled = false; // invalid input or NumberFormatException result in no valid input
        }
        Log.v(TAG, "conditionallyEnableSaveButton: " +enabled);
        if(saveButton != null) {
            saveButton.setEnabled(enabled);
            saveButton.getIcon().setAlpha(enabled ? 255 : 130);
        }
    }


    @Override
    public void onDestroy() {
        // handler.removeCallbacksAndMessages(null); // disable handler
        ServiceProxy.runOrBind(this, new Runnable() {

            @Override
            public void run() {
                ServiceProxy.closeServiceConnection();

            }
        });
        super.onDestroy();
    }


    private void add(Intervention i) {
        long id = this.dao.insert(i);
        Log.v(TAG, "added intervention with id: " + id);
        App.getEventBus().post(new Events.InterventionAdded(iv)); // For ServiceLocator update
        //App.getEventBus().postSticky(new Events.WaypointAddedByUser(w)); // For UI update
    }

    private void update(Intervention i) {
        this.dao.update(i);
        App.getEventBus().post(new Events.InterventionUpdated(iv)); // For ServiceLocator update
//        App.getEventBus().post(new Events.WaypointUpdated(w)); // For ServiceLocator update
        //App.getEventBus().postSticky(new Events.WaypointUpdatedByUser(w)); // For UI update
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_intervention, menu);
        this.saveButton = menu.findItem(R.id.menu_saveintervention);
        conditionallyEnableSaveButton();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_saveintervention:
                save();
                finish();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        HourMinute hm;
        switch (v.getId()) {
            case R.id.startTime:
                hm = HourMinute.fromMillis(iv.getFrom(), true);
                pickTime(hm.getHour(), hm.getMinute(), true);
                break;
            case R.id.endTime:
                hm = HourMinute.fromMillis(iv.getTo(), true);
                pickTime(hm.getHour(), hm.getMinute(), false);
                break;
            default:
                Timber.v("Could not open timepicker for id " + v.getId());
                break;
        }
    }

    private void pickTime(int hour, int minute, final boolean isStart) {
        try {
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            if (isStart) {
                                iv.setFrom(new HourMinute(hourOfDay, minute).toMillis());
                                if (iv.getTo() == null) {
                                    iv.setTo(iv.getFrom() + 60 * 60 * 1000); //Auto fill end time with 1 hour
                                }
                            } else {
                                iv.setTo(new HourMinute(hourOfDay, minute).toMillis());
                                if (iv.getFrom() == null) {
                                    iv.setFrom(iv.getTo() - 60 * 60 * 1000); //Auto fill end time with -1 hour
                                }
                            }
                            if (iv.getFrom() >= iv.getTo()) Toasts.showEndTimeBeforeStart();
                            binding.setItem(iv);
                            conditionallyEnableSaveButton();
                        }
                    }, hour, minute, true);
            timePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void save() {
        Intervention iv = this.iv;

        iv.setComment(binding.comment.getText().toString());

        if (update)
            update(iv);
        else {
            add(iv);
        }
    }

    // If the user hits back, go back to ActivityMain, no matter where he came from
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void updateSubtypeSpinner() {
        switch(this.iv.getType()) {
            case "Dadergerichte surveillance":
            case "Gebiedsgerichte surveillance":
                this.interventionSubtypes = getResources().getStringArray(R.array.intervention_subtypes_visible);
                break;
            case "Buiten/binnenring controle":
            case "Buitenringcontrole":
            case "Wijk-op-slot":
                this.interventionSubtypes = getResources().getStringArray(R.array.intervention_subtypes_number);
                break;
            default:
                this.interventionSubtypes = new String[]{};
                break;
        }
        this.initializeSubSpinner(this.interventionSubtypes, false);
        if (this.interventionSubtypes == null || this.interventionSubtypes.length == 0) {
            binding.spinnerIvSubtype.setVisibility(View.INVISIBLE);
        } else {
            binding.spinnerIvSubtype.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Object item = adapterView.getItemAtPosition(i);
        if (item == null) return;
        if (adapterView.getId() == binding.spinnerIvtype.getId()) {
            this.iv.setType((String) item);
            this.iv.setSubtype(null);
            updateSubtypeSpinner();
        } else if (adapterView.getId() == binding.spinnerIvSubtype.getId()) {
            this.iv.setSubtype((String) item);
        }
        conditionallyEnableSaveButton();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if (adapterView.getId() == binding.spinnerIvtype.getId()) {
            this.iv.setType(null);
            this.iv.setSubtype(null);
        } else if (adapterView.getId() == binding.spinnerIvSubtype.getId()) {
            this.iv.setSubtype(null);
        }
        conditionallyEnableSaveButton();
    }
}
