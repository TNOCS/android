package org.policetracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.policetracks.android.support.IncomingMessageProcessor;
import org.policetracks.android.support.OutgoingMessageProcessor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageEncrypted extends MessageBase{
    static final String TYPE = "encrypted";
    public String getData() {
        return data;
    }

    public void setdata(String cyphertext) {
        this.data = cyphertext;
    }

    private String data;

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }

    @Override
    public void processOutgoingMessage(OutgoingMessageProcessor handler) {
        handler.processOutgoingMessage(this);
    }

    @Override
    public String getBaseTopicSuffix() {  return null; }

}
