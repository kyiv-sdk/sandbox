package com.example.iyuro.ssl_chat.messenger;

import com.example.iyuro.ssl_chat.common.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MessageProtocol {
    private static final MessageProtocol ourInstance = new MessageProtocol();
//    private ChatInterface chatInterface;

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

    public String processSendMessage(String srcID, String dstID, String message){
        JSONObject jsonObject = new JSONObject();
        try {
            if (dstID != null) {
                jsonObject.put("keyAction", "msg");
                jsonObject.put("dstID", dstID);
                jsonObject.put("srcID", srcID);
                jsonObject.put("message", message);
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String createLoginRequest(String username){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyAction", "login");
            jsonObject.put("message", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public String createLoggedUsersListRequest(String srcID){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyAction", "loggedUsersList");
            jsonObject.put("srcID", srcID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
