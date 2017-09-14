package org.policetracks.android.ui.status;

import org.policetracks.android.services.ServiceMessage;
import org.policetracks.android.ui.base.view.MvvmView;
import org.policetracks.android.ui.base.viewmodel.MvvmViewModel;

import java.util.Date;

public interface StatusMvvm {

    interface View extends MvvmView {
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        ServiceMessage.EndpointState getEndpointState();
        String getEndpointMessage();
        int getEndpointQueue();
        boolean getPermissionLocation();
        Date getLocationUpdated();
        Date getAppStarted();
        Date getServiceStarted();

    }
}
