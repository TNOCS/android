package org.policetracks.android.ui.load;
import org.policetracks.android.support.Parser;
import org.policetracks.android.ui.base.view.MvvmView;
import org.policetracks.android.ui.base.viewmodel.MvvmViewModel;

import java.io.IOException;

public interface LoadMvvm {

    interface View extends MvvmView {
        void displayErrorPreferencesLoadFailed();
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        void saveConfiguration();
        void setConfiguration(String configuration) throws IOException, Parser.EncryptionException;
        String getConfigurationPretty();
    }
}
