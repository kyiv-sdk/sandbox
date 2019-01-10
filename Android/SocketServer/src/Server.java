import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<UserHandler> userHandlers = new ArrayList<>();
    static int nextUserID = 0;

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
                int userID = nextUserID++;
                System.out.println("user " + userID + " connected");

                UserHandler newUserHandler = new UserHandler(clientSocket, String.valueOf(userID), out, in);
                userHandlers.add(newUserHandler);
                Thread thread = new Thread(newUserHandler);
                thread.start();
            }

        } catch (Exception e) {
            System.out.println("Server started failed");
            e.printStackTrace();
        }
    }
}
