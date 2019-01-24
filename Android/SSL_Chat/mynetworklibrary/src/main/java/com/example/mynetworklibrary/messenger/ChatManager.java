package com.example.mynetworklibrary.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.mynetworklibrary.network.NetworkInterface;
import com.example.mynetworklibrary.network.NetworkManager;

import java.util.ArrayList;

public class ChatManager implements NetworkInterface, ChatInterface{
    private static final ChatManager ourInstance = new ChatManager();
    private static ArrayList<ChatUser> chatUserArrayList = new ArrayList<>();
    private UI_Interface UIInterface;
    private String currentUserID;

    public static ChatManager getInstance() {
        return ourInstance;
    }

    public ChatManager() {
        Log.i("--------MY_LOG--------", "chat manager created");
        refreshNetworkInterface();
        UIInterface = null;
        currentUserID = null;
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
    public void onMessageReceive(int headerLen, int fileLen, byte[] data) {
        Log.i("--------MY_LOG--------", "chat manager onMessageReceive");
        ChatMessage chatMessage = MessageProtocol.getInstance().processReceivedMessage(headerLen, fileLen, data);
        if (chatMessage.getKeyAction().equals("msg")){
            onNewMessage(chatMessage.getSrcID(), chatMessage.getMessage());
        } else if (chatMessage.getKeyAction().equals("loggedUsersList")){
            onUsersListRefresh(chatMessage.getAllLoggedUsersList());
        } else if (chatMessage.getKeyAction().equals("file")){
            onNewFile(chatMessage);
        }
    }

    public void onNewFile(ChatMessage chatMessage){
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

    public void sendMessage(byte[] message){
        NetworkManager.getInstance().send(message);
    }

    public void sendMessage(String dstUserID, String message){
        byte[] processedMessage = MessageProtocol.getInstance().processSendMessage(currentUserID, dstUserID, message).getBytes();
        if (processedMessage.length > 0) {
            NetworkManager.getInstance().send(processedMessage);
        }
    }

    public void sendPhoto(String dstUserID, Bitmap photo){
        ChatMessage photoMessage = MessageProtocol.getInstance().processSendPhoto(currentUserID, dstUserID, photo);
        NetworkManager.getInstance().send(photoMessage.getBytes());
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
