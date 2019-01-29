package com.example.chatlibrary.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.example.chatlibrary.chat.network.ChatMessage;
import com.example.chatlibrary.chat.network.MessageProtocol;
import com.example.chatlibrary.chat.network.NetworkInterface;
import com.example.chatlibrary.chat.network.NetworkManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class ChatManager implements NetworkInterface, ChatInterface{
    private static final ChatManager ourInstance = new ChatManager();
    private static ArrayList<ChatUser> chatUserArrayList = new ArrayList<>();
    private UI_Interface UIInterface;
    private String currentUserID;

    private static int nextFileID = 0;
    private String currentAbsolutePath;
    private Handler handler;

    public static ChatManager getInstance() {
        return ourInstance;
    }

    public ChatManager() {
        Log.i("--------MY_LOG--------", "chat manager created");
        refreshNetworkInterface();
        UIInterface = null;
        currentUserID = null;
        currentAbsolutePath = null;
        this.handler = new Handler();
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }

    public void setUIInterface(UI_Interface UIInterface) {
        this.UIInterface = UIInterface;
    }

    public ArrayList<ChatUser> getChatUserArrayList() {
        return chatUserArrayList;
    }

    public void refreshNetworkInterface(){
        NetworkManager.getInstance().setNetworkInterface(this);
    }

    public static int getNextFileID() {
        return nextFileID++;
    }

    public String getCurrentAbsolutePath() {
        return currentAbsolutePath;
    }

    public void setCurrentAbsolutePath(String currentAbsolutePath) {
        this.currentAbsolutePath = currentAbsolutePath;
    }

    @Override
    public void onMessageReceive(final ChatMessage chatMessage) {
        Log.i("--------MY_LOG--------", "chat manager onMessageReceive");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (chatMessage.getKeyAction().equals("msg")){
                    onNewMessage(chatMessage.getSrcID(), chatMessage.getMessage());
                } else if (chatMessage.getKeyAction().equals("loggedUsersList")){
                    onUsersListRefresh(chatMessage.getAllLoggedUsersList());
                } else if (chatMessage.getKeyAction().equals("file")){
                    onNewFile(chatMessage);
                }
            }
        });
    }

    public void onNewFile(ChatMessage chatMessage){
        switch (chatMessage.getContentType()){
            case "photo":
                Bitmap bitmap = BitmapFactory.decodeByteArray(chatMessage.getFile() , 0, chatMessage.getFile().length);
                for (int i = 0; i < chatUserArrayList.size(); i++){
                    ChatUser chatUser = chatUserArrayList.get(i);
                    if (chatUser.getUserID().equals(chatMessage.getSrcID())){
                        UserMessage userMessage = new UserMessage(bitmap, true);
                        chatUser.addMessage(userMessage);
                        break;
                    }
                }
                UIInterface.onNewPhotoMessage(chatMessage.getSrcID(), bitmap);
                break;
            case "audio":
                String currentFilePath = currentAbsolutePath + "/audiorecordtest" + String.valueOf(ChatManager.getNextFileID()) + ".3gp";
                File file = new File(currentFilePath);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(chatMessage.getFile());
                    fos.close();

                    for (int i = 0; i < chatUserArrayList.size(); i++){
                        ChatUser chatUser = chatUserArrayList.get(i);
                        if (chatUser.getUserID().equals(chatMessage.getSrcID())){
                            UserMessage userMessage = new UserMessage(true, currentFilePath);
                            chatUser.addMessage(userMessage);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                UIInterface.onNewAudioMessage(chatMessage.getSrcID(), currentFilePath);
                break;
        }

    }

    @Override
    public void onNewMessage(String srcID, String message) {
        for (int i = 0; i < chatUserArrayList.size(); i++){
            ChatUser chatUser = chatUserArrayList.get(i);
            if (chatUser.getUserID().equals(srcID)){
                UserMessage userMessage = new UserMessage(message, true);
                chatUser.addMessage(userMessage);
                break;
            }
        }

        UIInterface.onNewMessage(srcID, message);
    }

    @Override
    public void onUsersListRefresh(ArrayList<ChatUser> newUsers) {
        ArrayList<ChatUser> oldUsers = new ArrayList<>(chatUserArrayList);
        for(ChatUser oldUser : oldUsers){
            if (!newUsers.contains(oldUser)){
                chatUserArrayList.remove(oldUser);
            }
        }

        for(ChatUser newUser : newUsers){
            if (!chatUserArrayList.contains(newUser)){
                chatUserArrayList.add(newUser);
            }
        }

        UIInterface.onUsersListRefresh();
    }

    public void openConnection(boolean isSSLEnabled, String uniqueID){
        NetworkManager.getInstance().openConnection(isSSLEnabled, uniqueID);
        checkConnection();
    }

    public void sendMessage(byte[] message){
        if (checkConnection()) {
            NetworkManager.getInstance().send(message);
        }
    }

    public boolean checkConnection(){
        if (NetworkManager.getInstance().isConnectionOpen()) {
            return true;
        } else {
            UIInterface.onConnectionClosed();
        }
        return false;
    }

    public void sendMessage(String dstUserID, String message){
        byte[] processedMessage = MessageProtocol.getInstance().processSendMessage(currentUserID, dstUserID, message).getBytes();
        if (processedMessage.length > 0) {
            if (checkConnection()) {
                NetworkManager.getInstance().send(processedMessage);
            }
        }
    }

    public void sendAudio(String dstUserID, String filePath){
        byte[] processedMessage = MessageProtocol.getInstance().processSendAudio(currentUserID, dstUserID, filePath).getBytes();
        if (processedMessage.length > 0) {
            if (checkConnection()) {
                NetworkManager.getInstance().send(processedMessage);
            }
        }
    }

    public void sendPhoto(String dstUserID, Bitmap photo){
        ChatMessage photoMessage = MessageProtocol.getInstance().processSendPhoto(currentUserID, dstUserID, photo);
        if (checkConnection()) {
            NetworkManager.getInstance().send(photoMessage.getBytes());
        }
    }

    public ArrayList<UserMessage> getUserMessagesByID(String userID){
        for (int i = 0; i < chatUserArrayList.size(); i++){
            ChatUser chatUser = chatUserArrayList.get(i);
            if (chatUser.getUserID().equals(userID)){
                chatUser.resetUnreadMessagesCounter();
                return chatUser.getMessageArrayList();
            }
        }
        return null;
    }

    public void resetUnreadMessagesByUserID(String userID){
        for (int i = 0; i < chatUserArrayList.size(); i++){
            ChatUser chatUser = chatUserArrayList.get(i);
            if (chatUser.getUserID().equals(userID)){
                chatUser.resetUnreadMessagesCounter();
                return;
            }
        }
    }

    public byte[] createLoggedUsersListRequest(){
        return MessageProtocol.getInstance().createLoggedUsersListRequest(currentUserID).getBytes();
    }
}
