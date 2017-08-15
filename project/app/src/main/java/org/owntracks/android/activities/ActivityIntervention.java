package org.owntracks.android.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.*;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.owntracks.android.App;
import org.owntracks.android.R;
import org.owntracks.android.databinding.ActivityInterventionBinding;
import org.owntracks.android.db.Dao;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.db.InterventionDao;
import org.owntracks.android.support.SimpleTextChangeListener;
import org.owntracks.android.services.ServiceProxy;
import org.owntracks.android.support.Events;
import org.owntracks.android.support.Preferences;
import org.owntracks.android.support.widgets.HourMinute;
import org.owntracks.android.support.widgets.Toasts;

import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;


public class ActivityIntervention extends ActivityBase implements View.OnClickListener {
    private static final String TAG = "ActivityIntervention";
    public static final String BUNDLE_KEY_INTERVENTION_ID = "BUNDLE_KEY_INTERVENTION_ID";

    private InterventionDao dao;
    private Intervention iv;
    private boolean update = true;

    private MenuItem saveButton;
    private ActivityInterventionBinding binding;

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

        binding.startTime.setOnClickListener(this);
        binding.endTime.setOnClickListener(this);

        binding.setItem(this.iv);
        setupListenerAndRequiredFields();
    }

    private void setupListenerAndRequiredFields() {
        TextWatcher requiredForSave = new SimpleTextChangeListener() {
            @Override
            public void onChanged(String s) {
                conditionallyEnableSaveButton();
            }
        };

        binding.description.addTextChangedListener(requiredForSave);
//        binding.from.addTextChangedListener(requiredForSave);
//        binding.to.addTextChangedListener(requiredForSave);

    }

    private void conditionallyEnableSaveButton() {

        boolean enabled;
        try {
//            enabled = (binding.description.getText().toString().length() > 0)
//                    && (binding.from.getText().toString().length() > 0)
//                    && (binding.to.getText().toString().length() > 0);
enabled = true;
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
//        App.getEventBus().post(new Events.WaypointAdded(w)); // For ServiceLocator update
        //App.getEventBus().postSticky(new Events.WaypointAddedByUser(w)); // For UI update
    }

    private void update(Intervention i) {
        this.dao.update(i);
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
                hm = HourMinute.fromMillis(iv.getFrom());
                pickTime(hm.getHour(), hm.getMinute(), true);
                break;
            case R.id.endTime:
                hm = HourMinute.fromMillis(iv.getFrom());
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
                        }
                    }, hour, minute, true);
            timePickerDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void save() {
        Intervention iv = this.iv;

        if (!update) {
//            w.setModeId(Preferences.getModeId());
        }

        iv.setDescription(binding.description.getText().toString());

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
}
