package main;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class ServerMessageProtocol {
    private MessageProtocolStates messageProtocolState;

    public ServerMessageProtocol() {
        this.messageProtocolState = MessageProtocolStates.WAIT_FOR_UNIQUEID;
    }

    public UserMessage processNewMessage(RawMessage inRawMessage){

        UserMessage resultUserMessage = new UserMessage();

        byte[] inMessage = inRawMessage.getRawData();

        int hLen = inRawMessage.getHeaderLen();
        int fLen = inRawMessage.getFileLen();

        String jsonContent = new String(Arrays.copyOfRange(inMessage, 0, hLen));

        byte[] fileContent;
        if (fLen > 0) {
            fileContent = Arrays.copyOfRange(inMessage, hLen, hLen + fLen);
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
                    break;

                case "msg":
                    srcID = receivedMessageJsonObject.getString("srcID");
                    resultUserMessage.setSrcID(srcID);
                    dstID = receivedMessageJsonObject.getString("dstID");
                    resultUserMessage.setDstID(dstID);
                    String message = receivedMessageJsonObject.getString("message");
                    resultUserMessage.setMessage(message);
                    break;

                case "file":
                    srcID = receivedMessageJsonObject.getString("srcID");
                    resultUserMessage.setSrcID(srcID);
                    dstID = receivedMessageJsonObject.getString("dstID");
                    resultUserMessage.setDstID(dstID);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultUserMessage;
    }


    public MessageProtocolStates getMessageProtocolState() {
        return messageProtocolState;
    }

    public void setMessageProtocolState(MessageProtocolStates messageProtocolState) {
        this.messageProtocolState = messageProtocolState;
    }
}
