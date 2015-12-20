package im.tox.toktok.app.models;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class ApplicationPreferences extends RealmObject {

    @PrimaryKey
    private String field;
    private int fieldValue;

    @Ignore
    private int sessionId;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(int fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int dontPersist) {
        this.sessionId = sessionId;
    }
}
