import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ServerMessageProtocol {
    UserHandlerInterface userHandlerInterface;
    ServerInterface serverInterface;

    public ServerMessageProtocol(ServerInterface serverInterface, UserHandlerInterface userHandlerInterface) {
        this.userHandlerInterface = userHandlerInterface;
        this.serverInterface = serverInterface;
    }

    public void processNewMessage(String inMessage){
        if(inMessage == null || inMessage.equals("exit")){
            userHandlerInterface.onConnectionClose();
        } else {

            JSONObject requestJsonObject = new JSONObject(inMessage);
            String keyAction = requestJsonObject.getString("keyAction");

            JSONObject responseJsonObject = new JSONObject();
            responseJsonObject.put("keyAction", keyAction);
            String destinationID;

            switch (keyAction){
                case "login":
                    String username = requestJsonObject.getString("username");
                    if (userHandlerInterface.isUsernameValid(username)){
                        responseJsonObject.put("message", "ok");
                        userHandlerInterface.onLoginSuccess(responseJsonObject.toString(), username);
                    } else {
                        responseJsonObject.put("message", "nok");
                        userHandlerInterface.onLoginFailed(responseJsonObject.toString(), username);
                    }
                    break;
                case "loggedUsersList":
                    ArrayList<String> loggedUsers = serverInterface.getLoggedUsers();
                    responseJsonObject.put("loggedUsers", loggedUsers);
                    destinationID = userHandlerInterface.getUserName();

                    userHandlerInterface.onResponse(destinationID, responseJsonObject.toString());
                    break;
                case "msg":
                    destinationID = requestJsonObject.getString("dstID");

                    responseJsonObject.put("srcID", userHandlerInterface.getUserName());
                    String msg = requestJsonObject.getString("message");
                    responseJsonObject.put("message", msg);

                    userHandlerInterface.onResponse(destinationID, responseJsonObject.toString());
                    break;
            }
        }
    }
}
