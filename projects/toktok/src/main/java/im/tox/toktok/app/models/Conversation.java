package im.tox.toktok.app.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andre on 30/08/15.
 */
public class Conversation extends RealmObject{

    @PrimaryKey
    private String conversationID;
    private Friend creator;
    private boolean group;
    private RealmList<Friend> groupFriend;
    private Date conversationCreated;
    private int notReadMessages;
    private boolean silenced;


    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public Friend getCreator() {
        return creator;
    }

    public void setCreator(Friend creator) {
        this.creator = creator;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public RealmList<Friend> getGroupFriend() {
        return groupFriend;
    }

    public void setGroupFriend(RealmList<Friend> groupFriend) {
        this.groupFriend = groupFriend;
    }

    public Date getConversationCreated() {
        return conversationCreated;
    }

    public void setConversationCreated(Date conversationCreated) {
        this.conversationCreated = conversationCreated;
    }

    public int getNotReadMessages() {
        return notReadMessages;
    }

    public void setNotReadMessages(int notReadMessages) {
        this.notReadMessages = notReadMessages;
    }

    public boolean isSilenced() {
        return silenced;
    }

    public void setSilenced(boolean silenced) {
        this.silenced = silenced;
    }

}
