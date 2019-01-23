package main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerMessageProtocol {
    private UserHandlerInterface userHandlerInterface;
    private ServerInterface serverInterface;
    private MessageProtocolStates messageProtocolState;

    public ServerMessageProtocol(ServerInterface serverInterface, UserHandlerInterface userHandlerInterface) {
        this.userHandlerInterface = userHandlerInterface;
        this.serverInterface = serverInterface;
        this.messageProtocolState = MessageProtocolStates.WAIT_FOR_UNIQUEID;
    }

    public UserMessage processNewMessage(RawMessage inRawMessage){

        UserMessage resultUserMessage = new UserMessage();

        byte[] inMessage = inRawMessage.getRawData();

        if(inMessage == null || inMessage.length == 0){
            userHandlerInterface.onConnectionClose();
        } else {

            String jsonContent = new String(inMessage);

            int hLen = inRawMessage.getHeaderLen();
            int fLen = inRawMessage.getFileLen();

            jsonContent = jsonContent.substring(0, hLen);

            byte[] fileContent;
            if (fLen > 0) {
                int jLen = jsonContent.length();
                int mLen = inMessage.length;
                fileContent = Arrays.copyOfRange(inMessage, hLen, hLen + fLen);
                int fileCLen = fileContent.length;
                resultUserMessage.setFile(fileContent);
            }

            try {
                JSONObject receivedMessageJsonObject = new JSONObject(jsonContent);
                String keyAction = receivedMessageJsonObject.getString("keyAction");

                resultUserMessage.setKeyAction(keyAction);

                String srcID, dstID;

                switch (keyAction){
                    case "uniqueID":

                        String uniqueID = receivedMessageJsonObject.getString("message");
                        resultUserMessage.setMessage(uniqueID);

                        break;
                    case "login":

                        String response = receivedMessageJsonObject.getString("message");
                        resultUserMessage.setMessage(response);

                        break;
                    case "loggedUsersList":

//                        ArrayList<String> allLoggedUsersList = new ArrayList<>();
//
//                        allLoggedUsersList.clear();
//                        JSONArray loggedUsersJSONArray = receivedMessageJsonObject.getJSONArray("loggedUsers");
//                        for (i = 0; i < loggedUsersJSONArray.length(); i++) {
//                            allLoggedUsersList.add(loggedUsersJSONArray.getString(i));
//                        }
                        ArrayList<String> allLoggedUsersList = serverInterface.getLoggedUsers();

                        resultUserMessage.setAllLoggedUsersList(allLoggedUsersList);

                        break;
                    case "msg":
                        srcID = receivedMessageJsonObject.getString("srcID");
                        resultUserMessage.setSrcID(srcID);
                        dstID = receivedMessageJsonObject.getString("dstID");
                        resultUserMessage.setDstID(dstID);
                        String message = receivedMessageJsonObject.getString("message");
                        resultUserMessage.setMessage(message);
                        break;
                    case "photo":
                        srcID = receivedMessageJsonObject.getString("srcID");
                        resultUserMessage.setSrcID(srcID);
                        dstID = receivedMessageJsonObject.getString("dstID");
                        resultUserMessage.setDstID(dstID);

                        int fileID = receivedMessageJsonObject.getInt("fileID");
                        resultUserMessage.setFileID(fileID);
                        int fileSliceID = receivedMessageJsonObject.getInt("fileSliceID");
                        resultUserMessage.setFileSliceID(fileSliceID);
                        boolean isLast = receivedMessageJsonObject.getBoolean("isLast");
                        resultUserMessage.setLast(isLast);


//                        JSONArray jsonArrayFile = receivedMessageJsonObject.getJSONArray("file");
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//                        for (i = 0; i < jsonArrayFile.length(); i++){
//                            byte b = (byte)jsonArrayFile.getInt(i);
//                            byteArrayOutputStream.write(b);
//                        }
//
//                        byte[] file = byteArrayOutputStream.toByteArray();
//
//                        resultUserMessage.setFile(file);
                        int width = receivedMessageJsonObject.getInt("width");
                        resultUserMessage.setWidth(width);
                        int height = receivedMessageJsonObject.getInt("height");
                        resultUserMessage.setHeight(height);
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return resultUserMessage;
    }

    public void manageUniqueID(UserMessage userMessage){
        userHandlerInterface.setUniqueID(userMessage.getMessage());
    }

    public void manageLogin(UserMessage inUserMessage){
//        JSONObject requestJsonObject = new JSONObject(inMessage);
        String keyAction = inUserMessage.getKeyAction();

        UserMessage responseUserMessage = new UserMessage();
        responseUserMessage.setKeyAction(keyAction);

        if (keyAction.equals("login")){
            String username = inUserMessage.getMessage();
            if (userHandlerInterface.isUsernameValid(username)) {
                responseUserMessage.setMessage("ok");

                userHandlerInterface.onLoginSuccess(responseUserMessage.getBytes(), username);
                this.messageProtocolState = MessageProtocolStates.WAIT_FOR_MESSAGE;
            } else {
                responseUserMessage.setMessage("nok");

                userHandlerInterface.onLoginFailed(responseUserMessage.getBytes(), username);
            }
        }
    }

    public void manageMessage(UserMessage inUserMessage){
//        JSONObject requestJsonObject = new JSONObject(inMessage);
//        String keyAction = requestJsonObject.getString("keyAction");
//
//        JSONObject responseJsonObject = new JSONObject();
        UserMessage responseUserMessage = new UserMessage();
        responseUserMessage.setKeyAction(inUserMessage.getKeyAction());
        String destinationID;
        String srcID;




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
            case "photo":
                destinationID = inUserMessage.getDstID();

                userHandlerInterface.onResponse(destinationID, inUserMessage.getBytes());
                break;
        }
    }

    public MessageProtocolStates getMessageProtocolState() {
        return messageProtocolState;
    }

    public void setMessageProtocolState(MessageProtocolStates messageProtocolState) {
        this.messageProtocolState = messageProtocolState;
    }
}
