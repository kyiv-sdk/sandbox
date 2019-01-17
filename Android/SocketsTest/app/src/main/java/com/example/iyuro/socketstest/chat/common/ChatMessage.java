package com.example.iyuro.socketstest.chat.common;

import com.example.iyuro.socketstest.chat.messenger.ChatUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatMessage {
    String keyAction;
    String dstID;
    String srcID;

    String message;
    ArrayList<ChatUser> allLoggedUsersList;

//    public ChatMessage(){
//        this.keyAction = null;
//        this.dstID = null;
//        this.message = null;
//        this.allLoggedUsersList = null;
//    }


    public ChatMessage(String keyAction) {
        this.keyAction = keyAction;
        this.dstID = null;
        this.srcID = null;
        this.message = null;
        this.allLoggedUsersList = null;
    }

    public String getKeyAction() {
        return keyAction;
    }

    public void setKeyAction(String keyAction) {
        this.keyAction = keyAction;
    }

    public String getDstID() {
        return dstID;
    }

    public void setDstID(String dstID) {
        this.dstID = dstID;
    }

    public String getSrcID() {
        return srcID;
    }

    public void setSrcID(String srcID) {
        this.srcID = srcID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ChatUser> getAllLoggedUsersList() {
        return allLoggedUsersList;
    }

    public void setAllLoggedUsersList(ArrayList<ChatUser> allLoggedUsersList) {
        this.allLoggedUsersList = allLoggedUsersList;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("keyAction", keyAction);
            if (this.message != null && !this.message.equals("")) {
                jsonObject.put("message", this.message);
            }
            if (this.srcID != null && !this.srcID.equals("")) {
                jsonObject.put("srcID", this.srcID);
            }
            if (this.dstID != null && !this.dstID.equals("")) {
                jsonObject.put("dstID", this.dstID);
            }
            if (this.allLoggedUsersList != null && !this.allLoggedUsersList.isEmpty()) {
                jsonObject.put("loggedUsers", this.allLoggedUsersList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return this.toJSON().toString();
    }
}


