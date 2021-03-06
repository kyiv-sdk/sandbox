package main.basic_server;

import main.common.ServerInterface;
import main.common.UserHandler;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BasicServer implements ServerInterface, Runnable {
    public static ArrayList<UserHandler> userHandlers = new ArrayList<>();

    protected int portNumber;

    public BasicServer(int portNumber) {
        this.portNumber = portNumber;
    }

    @Override
    public void notifyAllUsersChanges(){
        for (int i = 0; i < userHandlers.size(); i++){
            UserHandler userHandler = userHandlers.get(i);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("keyAction", "loggedUsersList");

            userHandler.addMessage(jsonObject.toString());
        }
    }

    @Override
    public void notifyAllUsersExceptOne(String exceptThis){
        for (int i = 0; i < userHandlers.size(); i++){
            UserHandler userHandler = userHandlers.get(i);
            if (userHandler.getUserName().equals(exceptThis)) continue;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("keyAction", "loggedUsersList");

            userHandler.addMessage(jsonObject.toString());
        }
    }

    @Override
    public void onUserHandlerClose(String uniqueID) {
        for (UserHandler userHandler : userHandlers){
            if (userHandler.getUserUniqueID().equals(uniqueID)){
                userHandlers.remove(userHandler);
                System.out.println("removed");
                System.out.println("All users list: " + userHandlers.toString());
                return;
            }
        }
    }

    @Override
    public ArrayList<String> getLoggedUsers() {
        ArrayList<String> result = new ArrayList<>();
        for (UserHandler userHandler : userHandlers){
            if (!userHandler.getUserName().equals("default")) {
                result.add(userHandler.getUserName());
            }
        }
        return result;
    }

    @Override
    public int getLoggedUserHandlerPosition(String userUniqueId) {
        for (int i = 0; i < userHandlers.size(); i++){
            UserHandler userHandler = userHandlers.get(i);
            if ((userHandler.isLoggedIn()) && (userUniqueId.equals(userHandler.getUserUniqueID()))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(portNumber);
            System.out.println("BasicServer started successfully");

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Basic accepted new connection");

                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();

                UserHandler newUserHandler = new UserHandler(this, clientSocket, out, in);

                userHandlers.add(newUserHandler);
            }

        } catch (Exception e) {
            System.out.println("main.basic_server.BasicServer started failed");
            e.printStackTrace();
        }
    }
}
