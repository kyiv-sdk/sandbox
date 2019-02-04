package com.example.iyuro.ssl_chat.network;

import android.graphics.Bitmap;

import com.example.iyuro.ssl_chat.ChatUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class MessageProtocol {
    private static final MessageProtocol ourInstance = new MessageProtocol();

    public MessageProtocol() {
    }

    public static MessageProtocol getInstance() {
        return ourInstance;
    }

    public ChatMessage processReceivedMessage(int headerLen, int fileLen, byte[] inMessage){
        ChatMessage resultChatMessage = null;

        try {
            JSONObject receivedMessageJsonObject = new JSONObject(new String(Arrays.copyOfRange(inMessage, 0, headerLen)));
            String keyAction = receivedMessageJsonObject.getString("keyAction");

            resultChatMessage = new ChatMessage(keyAction);

            String srcID, dstID;

            switch (keyAction){
                case "login":
                    String response = receivedMessageJsonObject.getString("message");
                    resultChatMessage.setMessage(response);
                    break;

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
                    srcID = receivedMessageJsonObject.getString("srcID");
                    resultChatMessage.setSrcID(srcID);
                    dstID = receivedMessageJsonObject.getString("dstID");
                    resultChatMessage.setDstID(dstID);
                    String message = receivedMessageJsonObject.getString("message");
                    resultChatMessage.setMessage(message);
                    break;

                case "file":
                    String contentType = receivedMessageJsonObject.getString("contentType");
                    resultChatMessage.setContentType(contentType);
                    srcID = receivedMessageJsonObject.getString("srcID");
                    resultChatMessage.setSrcID(srcID);
                    dstID = receivedMessageJsonObject.getString("dstID");
                    resultChatMessage.setDstID(dstID);
                    byte[] file = Arrays.copyOfRange(inMessage, headerLen, headerLen + fileLen);
                    resultChatMessage.setFile(file);
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

    public ChatMessage processSendPhoto(String srcID, String dstID, Bitmap photo){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        ChatMessage resultChatMessage = new ChatMessage("file");
        resultChatMessage.setContentType("photo");
        resultChatMessage.setDstID(dstID);
        resultChatMessage.setSrcID(srcID);
        resultChatMessage.setFile(byteArray);

        return resultChatMessage;
    }

    public ChatMessage processSendAudio(String srcID, String dstID, String filePath){
        final File file = new File(filePath);
        FileInputStream fis = null;
        byte[] bytesArray = new byte[(int) file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ChatMessage resultChatMessage = new ChatMessage("file");
        resultChatMessage.setContentType("audio");
        resultChatMessage.setDstID(dstID);
        resultChatMessage.setSrcID(srcID);
        resultChatMessage.setFile(bytesArray);

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

    public ChatMessage createUniqueIDRequest(String uniqueID){
        ChatMessage resultChatMessage = new ChatMessage("uniqueID");
        resultChatMessage.setMessage(uniqueID);
        return resultChatMessage;
    }
}
