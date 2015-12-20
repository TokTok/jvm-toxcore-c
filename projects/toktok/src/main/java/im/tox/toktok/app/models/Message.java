package im.tox.toktok.app.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Message extends RealmObject {

    static final int MESSAGE_STATUS_SENDING = 0;
    static final int MESSAGE_STATUS_SEND = 1;
    static final int MESSAGE_STATUS_ERROR_NOT_SEND = 2;

    private Conversation conversation;
    private Friend creator;
    private Date postedDate;

    private Date sendDate;
    private int messageStatus;
    private String messageContent;

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public Friend getCreator() {
        return creator;
    }

    public void setCreator(Friend creator) {
        this.creator = creator;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
}
