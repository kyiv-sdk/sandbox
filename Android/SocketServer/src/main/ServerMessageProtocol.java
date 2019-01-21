package main;

import org.json.JSONObject;

import java.util.ArrayList;

public class ServerMessageProtocol {
    private UserHandlerInterface userHandlerInterface;
    private ServerInterface serverInterface;
    private MessageProtocolStates messageProtocolState;

    public ServerMessageProtocol(ServerInterface serverInterface, UserHandlerInterface userHandlerInterface) {
        this.userHandlerInterface = userHandlerInterface;
        this.serverInterface = serverInterface;
        this.messageProtocolState = MessageProtocolStates.WAIT_FOR_UNIQUEID;
    }

    public void processNewMessage(String inMessage){
        if(inMessage == null || inMessage.equals("exit")){
            userHandlerInterface.onConnectionClose();
        } else {

            switch (messageProtocolState){
                case WAIT_FOR_UNIQUEID:
                    manageUniqueID(inMessage);
                    System.out.println("uniqueId handled");
                    this.messageProtocolState = MessageProtocolStates.WAIT_FOR_LOGIN;
                    break;
                case WAIT_FOR_LOGIN:
                    manageLogin(inMessage);
                    System.out.println("login handled");
                    break;
                case WAIT_FOR_MESSAGE:
                    System.out.println("message handled");
                    manageMessage(inMessage);
                    break;
            }
        }
    }

    private void manageUniqueID(String inMessage){
        userHandlerInterface.setUniqueID(inMessage);
    }

    private void manageLogin(String inMessage){
        JSONObject requestJsonObject = new JSONObject(inMessage);
        String keyAction = requestJsonObject.getString("keyAction");

        JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("keyAction", keyAction);

        if (keyAction.equals("login")){
            String username = requestJsonObject.getString("message");
            if (userHandlerInterface.isUsernameValid(username)) {
                responseJsonObject.put("message", "ok");
                userHandlerInterface.onLoginSuccess(responseJsonObject.toString(), username);
                this.messageProtocolState = MessageProtocolStates.WAIT_FOR_MESSAGE;
            } else {
                this.messageProtocolState = MessageProtocolStates.WAIT_FOR_LOGIN;
                responseJsonObject.put("message", "nok");
                userHandlerInterface.onLoginFailed(responseJsonObject.toString(), username);
            }
        }
    }

    private void manageMessage(String inMessage){
        JSONObject requestJsonObject = new JSONObject(inMessage);
        String keyAction = requestJsonObject.getString("keyAction");

        JSONObject responseJsonObject = new JSONObject();
        responseJsonObject.put("keyAction", keyAction);
        String destinationID;
        String srcID;

        switch (keyAction){
            case "loggedUsersList":
                ArrayList<String> loggedUsers = serverInterface.getLoggedUsers();
                responseJsonObject.put("loggedUsers", loggedUsers);
                destinationID = userHandlerInterface.getUserName();

                userHandlerInterface.onResponse(destinationID, responseJsonObject.toString());
                break;
            case "msg":
                destinationID = requestJsonObject.getString("dstID");
                userHandlerInterface.onResponse(destinationID, requestJsonObject.toString());
                break;
            case "login":
                String username = requestJsonObject.getString("message");
                if (userHandlerInterface.getUserName().equals(username)) {
                    responseJsonObject.put("message", "ok");
                    userHandlerInterface.onLoginSuccess(responseJsonObject.toString(), username);
                } else {
                    responseJsonObject.put("message", "nok");
                    userHandlerInterface.onLoginFailed(responseJsonObject.toString(), username);
                }
                break;
            case "photo":
                destinationID = requestJsonObject.getString("dstID");

                userHandlerInterface.onResponse(destinationID, requestJsonObject.toString());
                break;
        }
    }
}
