package org.owntracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.owntracks.android.db.Day;
import org.owntracks.android.db.Intervention;
import org.owntracks.android.db.Waypoint;
import org.owntracks.android.support.IncomingMessageProcessor;
import org.owntracks.android.support.OutgoingMessageProcessor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageIntervention extends MessageBase{
    static final String TYPE = "intervention";
    public static final String BASETOPIC_SUFFIX = "/event";

    public String getBaseTopicSuffix() {  return BASETOPIC_SUFFIX; }

    private String desc;
    private long tst;
    private String ivType;


    public String getIvType() {
        return ivType;
    }

    public void setIvType(String ivType) {
        this.ivType = ivType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getTst() {
        return tst;
    }

    public void setTst(long tst) {
        this.tst = tst;
    }

    @Override
    public void processIncomingMessage(IncomingMessageProcessor handler) {
        handler.processIncomingMessage(this);
    }

    @Override
    public void processOutgoingMessage(OutgoingMessageProcessor handler) {
        handler.processOutgoingMessage(this);
    }


    public Intervention toDaoObject() {
        Intervention iv = new Intervention();

        iv.setDescription(getDesc());
        iv.setDate(new Date(TimeUnit.SECONDS.toMillis(getTst())));
        iv.setIvtype(getIvType());

        return iv;
    }

    public static MessageIntervention fromDaoObject(Intervention iv) {
        MessageIntervention message = new MessageIntervention();
        message.setDesc(iv.getDescription());
        message.setIvType(iv.getIvtype());
        message.setTst(TimeUnit.MILLISECONDS.toSeconds(iv.getDate().getTime()));
        return message;
    }

    @Override
    public boolean isValidMessage() {
        return super.isValidMessage() && (desc != null);
    }

}
