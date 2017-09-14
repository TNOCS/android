package org.policetracks.android.support.interfaces;

import android.content.Intent;

import org.greenrobot.eventbus.Subscribe;
import org.policetracks.android.services.ServiceProxy;
import org.policetracks.android.support.Events;

public interface ProxyableService {
	void onCreate(ServiceProxy c);

	void onDestroy();

	void onStartCommand(Intent intent);

	@Subscribe
	void onEvent(Events.Dummy event);
}
