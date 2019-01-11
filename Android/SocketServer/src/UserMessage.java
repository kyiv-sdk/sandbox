import org.json.JSONObject;

public class UserMessage {
    String keyAction;
    String dstUserName, message;

    public UserMessage(String inMessage) {
        JSONObject jsonObject = new JSONObject(inMessage);
        keyAction = jsonObject.getString("keyAction");
        message = jsonObject.getString("message");
        dstUserName = jsonObject.getString("dstID");
    }

    public String getDstUserName() {
        return dstUserName;
    }

    public void setDstUserName(String dstUserName) {
        this.dstUserName = dstUserName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
