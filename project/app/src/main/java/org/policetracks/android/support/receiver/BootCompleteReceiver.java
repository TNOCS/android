package org.policetracks.android.support.receiver;

import org.policetracks.android.services.ServiceProxy;
import org.policetracks.android.support.Preferences;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) && Preferences.getAutostartOnBoot()){
			context.startService(new Intent(context, ServiceProxy.class));
		}
	}
}
