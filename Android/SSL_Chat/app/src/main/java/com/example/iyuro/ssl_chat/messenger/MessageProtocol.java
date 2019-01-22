package com.example.iyuro.ssl_chat.messenger;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;


public class MessageProtocol {
    private static final MessageProtocol ourInstance = new MessageProtocol();

    private final int FILE_SIZE = 1000;
    private static int nextFileID = 0;

    public MessageProtocol() {
    }

    public static MessageProtocol getInstance() {
        return ourInstance;
    }

    public ChatMessage processReceivedMessage(String inMessage){
        ChatMessage resultChatMessage = null;
        try {
            JSONObject receivedMessageJsonObject = new JSONObject(inMessage);
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
                case "photo":
                    srcID = receivedMessageJsonObject.getString("srcID");
                    resultChatMessage.setSrcID(srcID);
                    dstID = receivedMessageJsonObject.getString("dstID");
                    resultChatMessage.setDstID(dstID);

                    int fileID = receivedMessageJsonObject.getInt("fileID");
                    resultChatMessage.setFileID(fileID);
                    int fileSliceID = receivedMessageJsonObject.getInt("fileSliceID");
                    resultChatMessage.setFileSliceID(fileSliceID);
                    boolean isLast = receivedMessageJsonObject.getBoolean("isLast");
                    resultChatMessage.setLast(isLast);


                    JSONArray jsonArrayFile = receivedMessageJsonObject.getJSONArray("file");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    for (int i = 0; i < jsonArrayFile.length(); i++){
                        byte b = (byte)jsonArrayFile.getInt(i);
                        byteArrayOutputStream.write(b);
                    }

                    byte[] file = byteArrayOutputStream.toByteArray();

                    resultChatMessage.setFile(file);
                    int width = receivedMessageJsonObject.getInt("width");
                    resultChatMessage.setWidth(width);
                    int height = receivedMessageJsonObject.getInt("height");
                    resultChatMessage.setHeight(height);
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

    public ArrayList<ChatMessage> processSendPhoto(String srcID, String dstID, Bitmap photo){
        ArrayList<ChatMessage> result = new ArrayList<>();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        int arrLength = byteArray.length;
        int counter = 0;

        while (arrLength > 0){
            ChatMessage resultChatMessage = new ChatMessage("photo");
            resultChatMessage.setFileSliceID(counter);

            resultChatMessage.setDstID(dstID);
            resultChatMessage.setSrcID(srcID);

            resultChatMessage.setWidth(photo.getWidth());
            resultChatMessage.setHeight(photo.getHeight());

            resultChatMessage.setFileID(nextFileID);

            int sliceFrom = counter * FILE_SIZE;
            int sliceTo = arrLength > FILE_SIZE ? (counter + 1) * FILE_SIZE : counter * FILE_SIZE + arrLength;
            arrLength -= sliceTo - sliceFrom;
            resultChatMessage.setFile(Arrays.copyOfRange(byteArray, sliceFrom, sliceTo));
            result.add(resultChatMessage);


            counter++;
        }

        nextFileID++;

        result.get(result.size() - 1).setLast(true);

        return result;
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
