package org.owntracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.owntracks.android.db.Intervention;
import org.owntracks.android.support.IncomingMessageProcessor;
import org.owntracks.android.support.OutgoingMessageProcessor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import timber.log.Timber;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageIntervention extends MessageBase{
    static final String TYPE = "intervention";
    public static final String BASETOPIC_SUFFIX = "/event";

    public String getBaseTopicSuffix() {  return BASETOPIC_SUFFIX; }

    private String comment;
    private long tst;
    private String type;
    private String subtype;
    private Long from;
    private Long to;
    private String id;

    public String getId() { return id;}

    public void setId(String id) { this.id = id;}

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

        iv.setComment(getComment());
        iv.setFrom(getFrom());
        iv.setTo(getTo());
        iv.setType(getType());
        iv.setSubtype(getSubtype());
        try {
            iv.setId(Long.parseLong(getId()));
        } catch (Exception e) {
            Timber.w("Failed to parse " + getId());
        }

        return iv;
    }

    public static MessageIntervention fromDaoObject(Intervention iv) {
        MessageIntervention message = new MessageIntervention();
        message.setComment(iv.getComment());
        message.setType(iv.getType());
        message.setSubtype(iv.getSubtype());
        message.setFrom(iv.getFrom());
        message.setTo(iv.getTo());
        message.setTst(TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()));
        return message;
    }

    @Override
    public boolean isValidMessage() {
        return super.isValidMessage() && (to != null) && (from != null);
    }

}
