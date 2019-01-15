package com.example.iyuro.socketstest.Chat.Messenger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MessageProtocol {
    private ChatInterface chatInterface;

    public MessageProtocol(ChatInterface chatInterface) {
        this.chatInterface = chatInterface;
    }

    public void setResponseMessageProtocolInterface(ChatInterface chatInterface) {
        this.chatInterface = chatInterface;
    }

    public void processReceivedMessage(String inMessage){
        JSONObject receivedMessageJsonObject = null;
        try {
            receivedMessageJsonObject = new JSONObject(inMessage);

            String keyAction = receivedMessageJsonObject.getString("keyAction");

            switch (keyAction){
                case "loggedUsersList":

                    ArrayList<ChatUser> allLoggedUsersList = new ArrayList<>();

                    allLoggedUsersList.clear();
                    JSONArray loggedUsersJSONArray = receivedMessageJsonObject.getJSONArray("loggedUsers");
                    for (int i = 0; i < loggedUsersJSONArray.length(); i++){
                        allLoggedUsersList.add(new ChatUser(loggedUsersJSONArray.getString(i)));
                    }

                    chatInterface.onUsersListRefresh(allLoggedUsersList);

                    break;
                case "msg":

                    String sourceID = receivedMessageJsonObject.getString("srcID");
                    String message = receivedMessageJsonObject.getString("message");
                    chatInterface.onNewMessage(sourceID, message);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String processSendMessage(String dstID, String message){
        JSONObject jsonObject = new JSONObject();
        try {
            if (dstID != null) {
                jsonObject.put("keyAction", "msg");
                jsonObject.put("dstID", dstID);
                jsonObject.put("message", message);
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
