package org.policetracks.android.injection.modules;

import org.policetracks.android.ui.configuration.ConfigurationMvvm;
import org.policetracks.android.ui.configuration.ConfigurationViewModel;
import org.policetracks.android.ui.contacts.ContactsMvvm;
import org.policetracks.android.ui.contacts.ContactsViewModel;
import org.policetracks.android.ui.diary.DiaryMvvm;
import org.policetracks.android.ui.diary.DiaryViewModel;
import org.policetracks.android.ui.interventions.InterventionsMvvm;
import org.policetracks.android.ui.interventions.InterventionsViewModel;
import org.policetracks.android.ui.load.LoadMvvm;
import org.policetracks.android.ui.load.LoadViewModel;
import org.policetracks.android.ui.map.MapMvvm;
import org.policetracks.android.ui.map.MapViewModel;
import org.policetracks.android.ui.status.StatusMvvm;
import org.policetracks.android.ui.status.StatusViewModel;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelModule {

    // Activities
    @Binds
    abstract MapMvvm.ViewModel bindMapViewModel(MapViewModel mapViewModel);

    @Binds
    abstract ContactsMvvm.ViewModel bindContactsViewModel(ContactsViewModel contactsViewModel);

    @Binds
    abstract DiaryMvvm.ViewModel bindDiaryViewModel(DiaryViewModel diaryViewModel);

    @Binds
    abstract InterventionsMvvm.ViewModel bindInterventionsViewModel(InterventionsViewModel interventionsViewModel);

    @Binds
    abstract StatusMvvm.ViewModel bindStatusViewModel(StatusViewModel statusViewModel);

    @Binds
    abstract ConfigurationMvvm.ViewModel bindConfigurationViewModel(ConfigurationViewModel statusViewModel);

    @Binds
    abstract LoadMvvm.ViewModel bindLoadViewModel(LoadViewModel statusViewModel);

}
