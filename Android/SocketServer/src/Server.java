import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements ServerInterface{
    static ArrayList<UserHandler> userHandlers = new ArrayList<>();

    public void startServer(int portNumber){

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            System.out.println("Server started successfully");

            while (true){
                Socket clientSocket = serverSocket.accept();
                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                try {
                    String userUniqueId;
                    String inputLine = in.readLine();
                    System.out.println("Received: " + inputLine);

                    if (inputLine != null) {

                        userUniqueId = inputLine;

                        int userHandlerPosition = getLoggedUserHandlerPosition(userUniqueId);
                        if (userHandlerPosition < 0) {
                            UserHandler newUserHandler = new UserHandler(this, clientSocket, userUniqueId, out, in);

                            userHandlers.add(newUserHandler);

                        } else {
                            // TODO: bind with already created handler
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("Server started failed");
            e.printStackTrace();
        }
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
//        for (int i = 0; i < userHandlers.size(); i++){
//            UserHandler userHandler = userHandlers.get(i);
//            if ((userHandler.isLoggedIn()) && (userUniqueId.equals(userHandler.getUserUniqueID()))) {
//                return i;
//            }
//        }
        return -1;
    }
}