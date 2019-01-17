package com.example.iyuro.ssl_chat.messenger;

import com.example.iyuro.ssl_chat.common.ChatMessage;

import java.util.ArrayList;

public class ChatManager implements NetworkInterface, ChatInterface{
    private static final ChatManager ourInstance = new ChatManager();
    private static ArrayList<ChatUser> chatUserArrayList = new ArrayList<>();
//    private MessageProtocol messageProtocol;
    private UI_Interface UIInterface;
    private String currentUserID;

    public static ChatManager getInstance() {
        return ourInstance;
    }

    public ChatManager() {
        NetworkManager.getInstance().setNetworkInterface(this);
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
        int i = 0;
    }

    public ArrayList<ChatUser> getChatUserArrayList() {
        return chatUserArrayList;
    }

    @Override
    public void onMessageReceive(String data) {
        ChatMessage chatMessage = MessageProtocol.getInstance().processReceivedMessage(data);
        if (chatMessage.getKeyAction().equals("msg")){
            onNewMessage(chatMessage.getSrcID(), chatMessage.getMessage());
        } else if (chatMessage.getKeyAction().equals("loggedUsersList")){
            onUsersListRefresh(chatMessage.getAllLoggedUsersList());
        }
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

    public void sendMessage(String dstUserID, String message){
        String processedMessage = MessageProtocol.getInstance().processSendMessage(currentUserID, dstUserID, message);
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

    public String createLoggedUsersListRequest(){
        return MessageProtocol.getInstance().createLoggedUsersListRequest(currentUserID);
    }
}
