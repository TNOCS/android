package org.policetracks.android.support.interfaces;

import org.policetracks.android.messages.MessageBase;
import org.policetracks.android.services.ServiceMessage;

public interface ServiceMessageEndpoint extends ProxyableService {
        void onSetService(ServiceMessage service);
        boolean sendMessage(MessageBase message);
        boolean isReady();

        void probe();
}
