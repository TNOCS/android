package org.policetracks.android.support;


import org.policetracks.android.messages.MessageBase;
import org.policetracks.android.messages.MessageClear;
import org.policetracks.android.messages.MessageCmd;
import org.policetracks.android.messages.MessageEvent;
import org.policetracks.android.messages.MessageLocation;
import org.policetracks.android.messages.MessageTransition;
import org.policetracks.android.messages.MessageWaypoint;
import org.policetracks.android.messages.MessageWaypoints;

public interface OutgoingMessageProcessor {
    void processOutgoingMessage(MessageBase message);
    void processOutgoingMessage(MessageCmd message);
    void processOutgoingMessage(MessageEvent message);
    void processOutgoingMessage(MessageLocation message);
    void processOutgoingMessage(MessageTransition message);
    void processOutgoingMessage(MessageWaypoint message);
    void processOutgoingMessage(MessageWaypoints message);
    void processOutgoingMessage(MessageClear message);
}
