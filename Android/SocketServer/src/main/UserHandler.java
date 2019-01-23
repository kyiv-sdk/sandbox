package main;

import main.basic_server.BasicServer;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserHandler implements UserHandlerInterface {
    private String userName;
    private String uniqueUserId;
    private final OutputStream out;
    private final InputStream in;
    private Socket socket;
    private boolean isLoggedIn;

    private Reader reader;
    private MessageManager messageManager;

    private ServerInterface serverInterface;

    private final List<RawMessage> messages;

    public UserHandler(ServerInterface serverInterface, Socket socket, OutputStream out, InputStream in) {
        this.socket = socket;
        this.userName = "default";
        this.out = out;
        this.in = in;
        this.isLoggedIn = true;
        this.uniqueUserId = null;

        this.serverInterface = serverInterface;

        ServerMessageProtocol serverMessageProtocol = new ServerMessageProtocol(serverInterface, this);

        System.out.println("Created user handler");

        messages = Collections.synchronizedList(new ArrayList<>());

        messageManager = new MessageManager(messages, serverMessageProtocol);
        Thread threadWriter = new Thread(messageManager);
        threadWriter.start();

        reader = new Reader(in, messages);
        Thread threadReader = new Thread(reader);
        threadReader.start();

    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getUserUniqueID() {
        return uniqueUserId;
    }

    @Override
    public boolean isUsernameValid(String username) {
        for (UserHandler handler : BasicServer.userHandlers) {
            if ((handler.isLoggedIn()) && (username.equals(handler.getUserName()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setUniqueID(String uniqueID) {
        this.uniqueUserId = uniqueID;
    }

    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public synchronized void writeMessage(byte[] bytesData){
        try {
            this.out.write(bytesData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Sent: " + new String(bytesData) + " to " + this.userName);
    }

    @Override
    public void onLoginSuccess(byte[] bytesData, String userName) {
        System.out.println("Username " + this.userName + " changed to " + userName);
        this.userName = userName;
        writeMessage(bytesData);
        serverInterface.notifyAllUsersExceptOne(userName);
    }

    @Override
    public void onResponse(String dstId, byte[] bytesData) {
        for (UserHandler handler : BasicServer.userHandlers) {
            if ((handler.isLoggedIn()) && (dstId.equals(handler.getUserName()))) {
                handler.writeMessage(bytesData);
                break;
            }
        }
    }

    @Override
    public void onLoginFailed(byte[] bytesData, String userName) {
        System.out.println("Username " + this.userName + " (old) " + userName + " (new) " + "loginFailed");
        writeMessage(bytesData);
    }

    @Override
    public void onConnectionClose() {
        reader.setLoopFlag(false);
        messageManager.setLoopFlag(false);
        System.out.println("exited");
        this.isLoggedIn=false;
        try {
            this.in.close();
            this.out.close();
            this.socket.close();
            serverInterface.notifyAllUsersChanges();
            serverInterface.onUserHandlerClose(this.uniqueUserId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String msg){
        synchronized(messages) {
            messages.add(new RawMessage(msg.getBytes()));
            System.out.println("Message added");
            messages.notifyAll();
        }
    }

    @Override
    public String toString() {
        return "main.UserHandler{" +
                "userName='" + userName + '\'' +
                ", uniqueUserId='" + uniqueUserId + '\'' +
                ", out=" + out +
                ", in=" + in +
                '}';
    }
}
