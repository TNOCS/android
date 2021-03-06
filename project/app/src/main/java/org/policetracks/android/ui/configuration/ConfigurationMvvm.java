package org.policetracks.android.ui.configuration;
import org.policetracks.android.ui.base.view.MvvmView;
import org.policetracks.android.ui.base.viewmodel.MvvmViewModel;
public interface ConfigurationMvvm {

    interface View extends MvvmView {
        void displayLoadFailed();

        void displayExportToFileFailed();
        void displayExportToFileSuccessful();

        boolean exportConfigurationToFile(String exportStr);
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        void onExportConfigurationToFileClicked();
        void onPreferencesValueForKeySetSuccessful();
    }
}
