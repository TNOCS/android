package org.policetracks.android.support;

import org.policetracks.android.messages.MessageBase;
import org.policetracks.android.messages.MessageCard;
import org.policetracks.android.messages.MessageClear;
import org.policetracks.android.messages.MessageCmd;
import org.policetracks.android.messages.MessageIntervention;
import org.policetracks.android.messages.MessageLocation;
import org.policetracks.android.messages.MessageTransition;
import org.policetracks.android.messages.MessageUnknown;

public interface IncomingMessageProcessor {
    void processIncomingMessage(MessageBase message);
    void processIncomingMessage(MessageLocation message);
    void processIncomingMessage(MessageCard message);
    void processIncomingMessage(MessageCmd message);
    void processIncomingMessage(MessageTransition message);
    void processIncomingMessage(MessageUnknown message);
    void processIncomingMessage(MessageClear message);
    void processIncomingMessage(MessageIntervention message);

}
