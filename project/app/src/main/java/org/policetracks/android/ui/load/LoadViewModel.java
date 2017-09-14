package org.policetracks.android.ui.load;

import android.content.Context;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.policetracks.android.BR;
import org.policetracks.android.injection.qualifier.AppContext;
import org.policetracks.android.injection.scopes.PerActivity;
import org.policetracks.android.messages.MessageConfiguration;
import org.policetracks.android.support.Parser;
import org.policetracks.android.support.Preferences;
import org.policetracks.android.ui.base.viewmodel.BaseViewModel;

import java.io.IOException;

import javax.inject.Inject;

import timber.log.Timber;


@PerActivity
public class LoadViewModel extends BaseViewModel<LoadMvvm.View> implements LoadMvvm.ViewModel<LoadMvvm.View> {
    @Bindable
    private String configurationPretty;
    private MessageConfiguration configuration;

    @Inject
    public LoadViewModel(@AppContext Context context) {

    }

    public void attachView(@NonNull LoadMvvm.View view, @Nullable Bundle savedInstanceState) {
        super.attachView(view, savedInstanceState);
    }

    @Bindable
    public String getConfigurationPretty() {
        return configurationPretty;
    }

    public void setConfiguration(String json) throws IOException, Parser.EncryptionException {
        Timber.v("%s", json);
        this.configuration = MessageConfiguration.class.cast(Parser.fromJson(json.getBytes()));
        this.configurationPretty = Parser.toJsonPlainPretty(this.configuration);
        notifyPropertyChanged(BR.configurationPretty);
    }


    public void saveConfiguration() {
        Preferences.importFromMessage(configuration);
    }
}
