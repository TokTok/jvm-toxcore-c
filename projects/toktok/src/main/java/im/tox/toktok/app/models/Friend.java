package im.tox.toktok.app.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Friend extends RealmObject {

    @PrimaryKey
    private String toxID;

    private String name;
    private boolean isFriend;
    private int color;
    private String statusMessage;
    private int status;
    private boolean fav;
    private boolean blocked;

    public String getToxID() {
        return toxID;
    }

    public void setToxID(String toxID) {
        this.toxID = toxID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setIsFriend(boolean isFriend) {
        this.isFriend = isFriend;
    }
}
