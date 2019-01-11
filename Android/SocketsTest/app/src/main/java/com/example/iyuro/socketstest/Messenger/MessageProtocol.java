package com.example.iyuro.socketstest.Messenger;

import org.json.JSONException;
import org.json.JSONObject;


public class MessageProtocol {
    MessageProtocolInterface messageProtocolInterface;

    public MessageProtocol(MessageProtocolInterface messageProtocolInterface) {
        this.messageProtocolInterface = messageProtocolInterface;
    }

    public void setMessageProtocolInterface(MessageProtocolInterface messageProtocolInterface) {
        this.messageProtocolInterface = messageProtocolInterface;
    }

    public void processReceivedMessage(String inMessage){
        JSONObject requestJsonObject = null;
        try {
            requestJsonObject = new JSONObject(inMessage);

            String keyAction = requestJsonObject.getString("keyAction");
            String sourceID = requestJsonObject.getString("srcID");

            switch (keyAction){
                case "msg":
                    String message = requestJsonObject.getString("message");
                    if (messageProtocolInterface.getCurrentDestionationUsername().equals(sourceID)){
                        messageProtocolInterface.onCurrentChatNewMessage(message);
                    } else {
                        messageProtocolInterface.onAnotherChatNewMessage(sourceID, message);
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
