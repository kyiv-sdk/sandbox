package com.example.iyuro.ssl_chat.messenger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.iyuro.ssl_chat.network.NetworkInterface;
import com.example.iyuro.ssl_chat.network.NetworkManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ChatManager implements NetworkInterface, ChatInterface{
    private static final ChatManager ourInstance = new ChatManager();
    private static ArrayList<ChatUser> chatUserArrayList = new ArrayList<>();
//    private MessageProtocol messageProtocol;
    private UI_Interface UIInterface;
    private String currentUserID;

    private ArrayList<ChatMessage> cachedFiles;

    public static ChatManager getInstance() {
        return ourInstance;
    }

    public ChatManager() {
        Log.i("--------MY_LOG--------", "chat manager created");
        refreshNetworkInterface();
        UIInterface = null;
        currentUserID = null;
        this.cachedFiles = new ArrayList<>();
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

    @Override
    public void onMessageReceive(String data) {
        Log.i("--------MY_LOG--------", "chat manager onMessageReceive");
        ChatMessage chatMessage = MessageProtocol.getInstance().processReceivedMessage(data);
        if (chatMessage.getKeyAction().equals("msg")){
            onNewMessage(chatMessage.getSrcID(), chatMessage.getMessage());
        } else if (chatMessage.getKeyAction().equals("loggedUsersList")){
            onUsersListRefresh(chatMessage.getAllLoggedUsersList());
        } else if (chatMessage.getKeyAction().equals("photo")){
            onNewFileSlice(chatMessage);
        }
    }

    public void onNewFileSlice(ChatMessage chatMessage){
        cachedFiles.add(chatMessage);

        if (chatMessage.isLast()){

            int fileID = chatMessage.getFileID();

            ArrayList<ChatMessage> thisFileIDMessages = new ArrayList<>();

            for (ChatMessage cm : cachedFiles){
                if (fileID == cm.getFileID()){
                    thisFileIDMessages.add(cm);
                }
            }

            thisFileIDMessages.sort(new Comparator<ChatMessage>() {
                @Override
                public int compare(ChatMessage o1, ChatMessage o2) {
                    return o1.getFileSliceID() - o2.getFileSliceID();
                }
            });

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            try {
                for (ChatMessage cm : thisFileIDMessages){
                        byteArrayOutputStream.write(cm.getFile());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String str = byteArrayOutputStream.toString();
            byte[] bbitmap = byteArrayOutputStream.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bbitmap , 0, bbitmap.length);
            for (int i = 0; i < chatUserArrayList.size(); i++){
                ChatUser chatUser = chatUserArrayList.get(i);
                if (chatUser.getUserID().equals(chatMessage.getSrcID())){
                    UserMessage userMessage = new UserMessage(bitmap, true);
                    chatUser.addMessage(userMessage);
                    break;
                }
            }

            UIInterface.onNewPhotoMessage(chatMessage.getSrcID(), bitmap);

            cachedFiles.removeIf(photo -> photo.getFileID() == chatMessage.getFileID());
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

    public void sendMessage(String message){
        NetworkManager.getInstance().send(message.getBytes());
    }

    public void sendMessage(String dstUserID, String message){
        String processedMessage = MessageProtocol.getInstance().processSendMessage(currentUserID, dstUserID, message).toString();
        if (!processedMessage.equals("")) {
            NetworkManager.getInstance().send(processedMessage.getBytes());
        }
    }

    public void sendPhoto(String dstUserID, Bitmap photo){
        ArrayList<ChatMessage> photoSlices = MessageProtocol.getInstance().processSendPhoto(currentUserID, dstUserID, photo);

        for (ChatMessage photoChatMessage : photoSlices) {
            NetworkManager.getInstance().send(photoChatMessage.toString().getBytes());
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

    public String createLoggedUsersListRequest(){
        return MessageProtocol.getInstance().createLoggedUsersListRequest(currentUserID).toString();
    }
}
