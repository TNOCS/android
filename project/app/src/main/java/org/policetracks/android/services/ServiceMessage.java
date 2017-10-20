package org.policetracks.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LongSparseArray;

import org.greenrobot.eventbus.Subscribe;
import org.policetracks.android.App;
import org.policetracks.android.db.Dao;
import org.policetracks.android.db.Intervention;
import org.policetracks.android.db.InterventionDao;
import org.policetracks.android.messages.MessageBase;
import org.policetracks.android.messages.MessageCard;
import org.policetracks.android.messages.MessageClear;
import org.policetracks.android.messages.MessageCmd;
import org.policetracks.android.messages.MessageIntervention;
import org.policetracks.android.messages.MessageLocation;
import org.policetracks.android.messages.MessageTransition;
import org.policetracks.android.messages.MessageUnknown;
import org.policetracks.android.messages.MessageWaypoints;
import org.policetracks.android.support.Events;
import org.policetracks.android.support.IncomingMessageProcessor;
import org.policetracks.android.support.Preferences;
import org.policetracks.android.support.widgets.Toasts;
import org.policetracks.android.support.interfaces.ProxyableService;
import org.policetracks.android.support.interfaces.ServiceMessageEndpoint;
import org.policetracks.android.support.interfaces.StatefulServiceMessageEndpoint;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;


public class ServiceMessage implements ProxyableService, IncomingMessageProcessor {
    private static final String TAG = "ServiceMessage";
    public static final String RECEIVER_ACTION_CLEAR_CONTACT_EXTRA_TOPIC = "RECEIVER_ACTION_CLEAR_CONTACT_EXTRA_TOPIC" ;
    public static final String RECEIVER_ACTION_CLEAR_CONTACT = "RECEIVER_ACTION_CLEAR_CONTACT";

    private static ServiceMessageEndpoint endpoint;
    private ThreadPoolExecutor incomingMessageProcessorExecutor;

    private ServiceProxy context;

    public void reconnect() {
        if(endpoint instanceof StatefulServiceMessageEndpoint)
            StatefulServiceMessageEndpoint.class.cast(endpoint).reconnect();
    }

    public void disconnect() {
        if(endpoint instanceof StatefulServiceMessageEndpoint)
            StatefulServiceMessageEndpoint.class.cast(endpoint).disconnect();
    }

    public ServiceProxy getContext() {
        return context;
    }

    public enum EndpointState {
        INITIAL,
        IDLE,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        DISCONNECTED,
        DISCONNECTED_USERDISCONNECT,
        ERROR,
        ERROR_DATADISABLED,
        ERROR_CONFIGURATION;

        public String getLabel(Context context) {
            Resources res = context.getResources();
            int resId = res.getIdentifier(this.name(), "string", context.getPackageName());
            if (0 != resId) {
                return (res.getString(resId));
            }
            return (name());
        }

        public boolean isErrorState() {
            return this == ERROR || this == ERROR_DATADISABLED || this == ERROR_CONFIGURATION;
        }
    }



    @Override
    public void onCreate(ServiceProxy c) {
        this.context = c;
        this.incomingMessageProcessorExecutor = new ThreadPoolExecutor(2,2,1,  TimeUnit.MINUTES,new LinkedBlockingQueue<Runnable>());
        onEndpointStateChanged(EndpointState.INITIAL);
        try {
            endpoint = instantiateEndpoint(Preferences.getModeId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private @NonNull ServiceMessageEndpoint instantiateEndpoint(int mode) throws Exception{
        Timber.v("mode:%s", mode);
        if(endpoint != null) {
            Timber.v("destroying endpoint");
            endpoint.onDestroy();
        }

        Timber.v("instantiating new endpoint");
        ServiceMessageEndpoint p = null;
        switch (mode) {
            case App.MODE_ID_HTTP_PRIVATE:
                p = new ServiceMessageHttp();
                break;
            case App.MODE_ID_MQTT_PRIVATE:
            case App.MODE_ID_MQTT_PUBLIC:
                p = new ServiceMessageMqtt();
                break;
        }

        if(p == null) {
            Timber.e("unable to instantiate endpoint for mode:%s", mode);
            throw new Exception("endpoint instantiation faield");
        }

        p.onCreate(context);
        p.onSetService(this);
        App.getEventBus().register(p);
        return p;
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        if(endpoint != null)
            endpoint.onDestroy();
    }

    @Override
    public void onStartCommand(Intent intent) {
        if (endpoint != null)
            endpoint.onStartCommand(intent);

    }

    @Subscribe
    public void onEvent(Events.Dummy event) {

    }

    @Subscribe
    public void onEvent(Events.ModeChanged event) {
        try {
            endpoint = instantiateEndpoint(Preferences.getModeId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private LongSparseArray<MessageBase> outgoingQueue = new LongSparseArray<>();

    public void sendMessage(MessageBase message) {
        Timber.v("endpoint:%s, message:%s",endpoint, message);

        message.setOutgoing();
        try {

            if(endpoint == null) {
                Timber.e("no endpoint, creating on demand");
                endpoint = instantiateEndpoint(Preferences.getModeId());
            }

            if(!endpoint.isReady()) {
                Timber.e("endpoint is not ready: %s", endpoint);
                endpoint.probe();
                return;
            }

            if(endpoint.sendMessage(message)) {
                this.onMessageQueued(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



     void onMessageDelivered(Long messageId) {
        MessageBase m = outgoingQueue.get(messageId);
        outgoingQueue.remove(messageId);

        if(m == null) {
            Log.e(TAG, "onMessageDelivered()- messageId:"+messageId + ", error: called for unqueued message");
        } else {
            Log.v(TAG, "onMessageDelivered()-  messageId:" + m.getMessageId()+", queueLength:"+outgoingQueue.size());
            if(m instanceof MessageLocation) {
                App.getEventBus().post(m);
            }
            //Toasts.showMessageSentAndRemaining(outgoingQueue.size());
        }
    }

    private void onMessageQueued(MessageBase m) {
        outgoingQueue.put(m.getMessageId(), m);

        Log.v(TAG, "onMessageQueued()- messageId:" + m.getMessageId()+", queueLength:"+outgoingQueue.size());
        if(m instanceof MessageLocation && MessageLocation.REPORT_TYPE_USER.equals(MessageLocation.class.cast(m).getT()))
            Toasts.showMessageQueued(outgoingQueue.size());
    }

    public void onMessageDeliveryFailed(Long messageId) {

        MessageBase m = outgoingQueue.get(messageId);
        outgoingQueue.remove(messageId);

        if(m == null) {
            Timber.e("type:base, messageId:%s, error: called for unqueued message", messageId);
        } else {
            Timber.e("type:base, messageId:%s, queueLength:%s", messageId, outgoingQueue.size());
            if(m.getOutgoingTTL() > 0)  {
                Timber.e("type:base, messageId:%s, action: requeued",m.getMessageId() );
                sendMessage(m);
            } else {
                Timber.e("type:base, messageId:%s, action: discarded due to expired ttl",m.getMessageId() );
            }
        }
    }

    public void onMessageReceived(MessageBase message) {
        message.setIncomingProcessor(this);
        message.setIncoming();
        incomingMessageProcessorExecutor.execute(message);
    }

    public void onEndpointStateChanged(EndpointState newState) {
        App.getEventBus().postSticky(new Events.EndpointStateChanged(newState));
    }

    public void onEndpointStateChanged(EndpointState newState,  String message) {
        App.getEventBus().postSticky(new Events.EndpointStateChanged(newState, message));
    }

    public void onEndpointStateChanged(EndpointState newState, Exception e) {
        Timber.v("new state:%s",newState);
        App.getEventBus().postSticky(new Events.EndpointStateChanged(newState, e));
    }


    @Override
    public void processIncomingMessage(MessageBase message) {
        Timber.v("type:base, key:%s", message.getContactKey());
    }

    public void processIncomingMessage(MessageUnknown message) {
        Timber.v("type:unknown, key:%s", message.getContactKey());
    }

    @Override
    public void processIncomingMessage(MessageClear message) {
        App.getContactsRepo().remove(message.getContactKey());
    }


    @Override
    public void processIncomingMessage(MessageLocation message) {
        App.getContactsRepo().update(message.getContactKey(),message);

    }

    @Override
    public void processIncomingMessage(MessageCard message) {
        App.getContactsRepo().update(message.getContactKey(),message);
    }

    @Override
    public void processIncomingMessage(MessageCmd message) {
        if(!Preferences.getRemoteCommand()) {
            Timber.e("remote commands are disabled");
            return;
        }


        if(!Preferences.getPubTopicCommands().equals(message.getTopic())) {
            Timber.e("cmd message received on wrong topic");
            return;
        }

        switch (message.getAction()) {
            case MessageCmd.ACTION_REPORT_LOCATION:
                ServiceProxy.getServiceLocator().reportLocationResponse();
                break;
            case MessageCmd.ACTION_WAYPOINTS:
                ServiceProxy.getServiceLocator().publishWaypointsMessage();
                break;
            case MessageCmd.ACTION_SET_WAYPOINTS:
                MessageWaypoints w = message.getWaypoints();
                if(w != null)
                    Preferences.importWaypointsFromJson(w.getWaypoints());
                break;
            case MessageCmd.ACTION_SET_CONFIGURATION:
                Preferences.importFromMessage(message.getConfiguration());
                break;
            case MessageCmd.ACTION_RECONNECT:
                ServiceProxy.getServiceMessage().reconnect();
            case MessageCmd.ACTION_RESTART:
                Intent mStartActivity = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                int mPendingIntentId = 223344;
                PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
        }
    }

    @Override
    public void processIncomingMessage(MessageTransition message) {
        ServiceProxy.getServiceNotification().processMessage(message);
    }

    @Override
    public void processIncomingMessage(MessageIntervention message) {
        if (message == null) return;

        InterventionDao dao = Dao.getInterventionDao();

        Intervention msgIntervention = message.toDaoObject();
        if (msgIntervention.getId() == null) return;

        if (dao.loadByRowId(msgIntervention.getId()) == null) {
            long id = dao.insert(msgIntervention);
            Log.v(TAG, "added intervention with id: " + id);
            App.getEventBus().post(new Events.InterventionAdded(msgIntervention)); // For ServiceLocator update
        }
    }
}