package main;

import org.json.JSONObject;

public class UserMessage {
    String keyAction;
    String dstUserName, message;
    String rawMessage;

    public UserMessage(String inMessage) {
        rawMessage = inMessage;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    @Override
    public String toString() {
        return "main.UserMessage{" +
                "rawMessage='" + rawMessage + '\'' +
                '}';
    }
}
