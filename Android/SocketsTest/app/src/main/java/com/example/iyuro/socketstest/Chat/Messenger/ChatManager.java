package com.example.iyuro.socketstest.Chat.Messenger;

import java.util.ArrayList;

public class ChatManager implements NetworkInterface, ChatInterface{
    private static final ChatManager ourInstance = new ChatManager();
    private static ArrayList<ChatUser> chatUserArrayList = new ArrayList<>();
    MessageProtocol messageProtocol;
    UI_Interface UIInterface;

    public static ChatManager getInstance() {
        return ourInstance;
    }

    public ChatManager() {
        NetworkManager.getInstance().openConnection();
        NetworkManager.getInstance().setNetworkInterface(this);
        messageProtocol = new MessageProtocol(this);
        UIInterface = null;
    }

    public void setUIInterface(UI_Interface UIInterface) {
        this.UIInterface = UIInterface;
    }

    public ArrayList<ChatUser> getChatUserArrayList() {
        return chatUserArrayList;
    }

    @Override
    public void onMessageReceive(String data) {
        messageProtocol.processReceivedMessage(data);
    }

    @Override
    public void onNewMessage(String userID, String message) {
        for (int i = 0; i < chatUserArrayList.size(); i++){
            ChatUser chatUser = chatUserArrayList.get(i);
            if (chatUser.getUserID().equals(userID)){
                UserMessage userMessage = new UserMessage(message, true);
                chatUser.addMessage(userMessage);
                break;
            }
        }

        UIInterface.onNewMessage(userID, message);
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
        NetworkManager.getInstance().send(message);
    }

    public void sendMessage(String userID, String message){
        String processedMessage = messageProtocol.processSendMessage(userID, message);
        if (processedMessage != null) {
            NetworkManager.getInstance().send(processedMessage);
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
}
