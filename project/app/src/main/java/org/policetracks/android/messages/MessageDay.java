package org.policetracks.android.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.policetracks.android.db.Day;
import org.policetracks.android.support.IncomingMessageProcessor;
import org.policetracks.android.support.OutgoingMessageProcessor;

import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageDay extends MessageBase{
    static final String TYPE = "day";
    public static final String BASETOPIC_SUFFIX = "/event";

    public String getBaseTopicSuffix() {  return BASETOPIC_SUFFIX; }

    private String desc;
    private long tst;


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


    public Day toDaoObject() {
        Day day = new Day();

        day.setDescription(getDesc());
        day.setFrom(TimeUnit.SECONDS.toMillis(getTst()));
        day.setTo(day.getFrom() + TimeUnit.HOURS.toMillis(8));

        return day;
    }

    public static MessageDay fromDaoObject(Day day) {
        MessageDay message = new MessageDay();
        message.setDesc(day.getDescription());
        message.setTst(TimeUnit.MILLISECONDS.toSeconds(day.getFrom()));
        return message;
    }

    @Override
    public boolean isValidMessage() {
        return super.isValidMessage() && (desc != null);
    }

}
