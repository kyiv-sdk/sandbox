package com.example.iyuro.ssl_chat.messenger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MessageProtocol {
    private static final MessageProtocol ourInstance = new MessageProtocol();

    public MessageProtocol() {
    }

    public static MessageProtocol getInstance() {
        return ourInstance;
    }

    public ChatMessage processReceivedMessage(String inMessage){
        // TODO: rewrite using ChatMessage
        ChatMessage resultChatMessage = null;
        try {
            JSONObject receivedMessageJsonObject = new JSONObject(inMessage);
            String keyAction = receivedMessageJsonObject.getString("keyAction");

            resultChatMessage = new ChatMessage(keyAction);

            switch (keyAction){
                case "loggedUsersList":

                    ArrayList<ChatUser> allLoggedUsersList = new ArrayList<>();

                    allLoggedUsersList.clear();
                    JSONArray loggedUsersJSONArray = receivedMessageJsonObject.getJSONArray("loggedUsers");
                    for (int i = 0; i < loggedUsersJSONArray.length(); i++) {
                        allLoggedUsersList.add(new ChatUser(loggedUsersJSONArray.getString(i)));
                    }

                    resultChatMessage.setAllLoggedUsersList(allLoggedUsersList);

                    break;
                case "msg":
                    String srcID = receivedMessageJsonObject.getString("srcID");
                    resultChatMessage.setSrcID(srcID);
                    String dstID = receivedMessageJsonObject.getString("dstID");
                    resultChatMessage.setDstID(dstID);
                    String message = receivedMessageJsonObject.getString("message");
                    resultChatMessage.setMessage(message);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultChatMessage;
    }

    public ChatMessage processSendMessage(String srcID, String dstID, String message){
        ChatMessage resultChatMessage = new ChatMessage("msg");

        resultChatMessage.setDstID(dstID);
        resultChatMessage.setSrcID(srcID);
        resultChatMessage.setMessage(message);

        return resultChatMessage;
    }

    public ChatMessage createLoginRequest(String username){
        ChatMessage resultChatMessage = new ChatMessage("login");

        resultChatMessage.setMessage(username);

        return resultChatMessage;
    }

    public ChatMessage createLoggedUsersListRequest(String srcID){

        ChatMessage resultChatMessage = new ChatMessage("loggedUsersList");

        resultChatMessage.setSrcID(srcID);

        return resultChatMessage;
    }
}
