package main;

import java.util.ArrayList;
import java.util.List;

public class MessageManager implements Runnable{
    private final List<RawMessage> inMessages;
    private boolean loopFlag;
    private ServerMessageProtocol serverMessageProtocol;
    private UserHandlerInterface userHandlerInterface;
    private ServerInterface serverInterface;

    public MessageManager(List<RawMessage> inMessages, ServerMessageProtocol serverMessageProtocol, UserHandlerInterface userHandlerInterface, ServerInterface serverInterface) {
        this.inMessages = inMessages;
        this.loopFlag = true;
        this.serverMessageProtocol = serverMessageProtocol;
        this.userHandlerInterface = userHandlerInterface;
        this.serverInterface = serverInterface;
    }

    @Override
    public void run() {
        System.out.println("MessageManager started");
        while (loopFlag){
            try {
                synchronized (inMessages) {
                    try {
                        inMessages.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("manager notified");
                    while (inMessages.size() > 0) {
                        RawMessage msg = inMessages.get(0);
                        inMessages.remove(0);

                        if (msg.getRawData() == null || msg.getRawData().length == 0){
                            userHandlerInterface.onConnectionClose();
                        } else {

                            UserMessage userMessage = serverMessageProtocol.processNewMessage(msg);

                            switch (serverMessageProtocol.getMessageProtocolState()) {

                                case WAIT_FOR_UNIQUEID:
                                    manageUniqueID(userMessage);
                                    System.out.println("uniqueId handled");
                                    serverMessageProtocol.setMessageProtocolState(MessageProtocolStates.WAIT_FOR_LOGIN);
                                    break;

                                case WAIT_FOR_LOGIN:
                                    manageLogin(userMessage);
                                    System.out.println("login handled");
                                    break;

                                case WAIT_FOR_MESSAGE:
                                    System.out.println("message handled");
                                    manageMessage(userMessage);
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            System.out.println("MessageManager closed");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }

    public void manageUniqueID(UserMessage userMessage){
        userHandlerInterface.setUniqueID(userMessage.getMessage());
    }

    public void manageLogin(UserMessage inUserMessage){
        String keyAction = inUserMessage.getKeyAction();

        UserMessage responseUserMessage = new UserMessage();
        responseUserMessage.setKeyAction(keyAction);

        if (keyAction.equals("login")){
            String username = inUserMessage.getMessage();
            if (userHandlerInterface.isUsernameValid(username)) {
                responseUserMessage.setMessage("ok");
                userHandlerInterface.onLoginSuccess(responseUserMessage.getBytes(), username);
                serverMessageProtocol.setMessageProtocolState(MessageProtocolStates.WAIT_FOR_MESSAGE);
            } else {
                responseUserMessage.setMessage("nok");
                userHandlerInterface.onLoginFailed(responseUserMessage.getBytes(), username);
            }
        }
    }

    public void manageMessage(UserMessage inUserMessage){
        UserMessage responseUserMessage = new UserMessage();
        responseUserMessage.setKeyAction(inUserMessage.getKeyAction());
        String destinationID;

        switch (inUserMessage.getKeyAction()){

            case "loggedUsersList":
                ArrayList<String> loggedUsers = serverInterface.getLoggedUsers();
                responseUserMessage.setAllLoggedUsersList(loggedUsers);
                destinationID = userHandlerInterface.getUserName();
                userHandlerInterface.onResponse(destinationID, responseUserMessage.getBytes());
                break;

            case "msg":
                destinationID = inUserMessage.getDstID();
                userHandlerInterface.onResponse(destinationID, inUserMessage.getBytes());
                break;

            case "login":
                String username = inUserMessage.getMessage();
                if (userHandlerInterface.getUserName().equals(username)) {
                    responseUserMessage.setMessage("ok");
                    userHandlerInterface.onLoginSuccess(responseUserMessage.getBytes(), username);
                } else {
                    responseUserMessage.setMessage("nok");
                    userHandlerInterface.onLoginFailed(responseUserMessage.getBytes(), username);
                }
                break;

            case "file":
                destinationID = inUserMessage.getDstID();
                userHandlerInterface.onResponse(destinationID, inUserMessage.getBytes());
                break;
        }
    }
}
