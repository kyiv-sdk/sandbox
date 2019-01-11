import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class UserHandler implements Runnable, UserHandlerInterface {
    private String userName;
    private String uniqueUserId;
    private final PrintWriter out;
    private final BufferedReader in;
    Socket socket;
    boolean isLoggedIn;
    boolean loopFlag;
    ServerMessageProtocol serverMessageProtocol;

    public UserHandler(ServerInterface serverInterface, Socket socket, String uniqueID, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.userName = "default";
        this.out = out;
        this.in = in;
        this.isLoggedIn = true;
        this.loopFlag = true;
        this.uniqueUserId = uniqueID;

        serverMessageProtocol = new ServerMessageProtocol(serverInterface, this);

        System.out.println("Created user handler for: " + uniqueUserId);
    }

    @Override
    public void run() {
        String inputLine;
        while (loopFlag){
            try {
                inputLine = in.readLine();
                System.out.println("Received: " + inputLine + " from " + this.userName);

                serverMessageProtocol.processNewMessage(inputLine);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            this.in.close();
            this.out.close();
        } catch(IOException e){
            e.printStackTrace();
        }
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
//        for (UserHandler handler : Server.userHandlers) {
//            if ((handler.isLoggedIn()) && (username.equals(handler.getUserId()))) {
//                return false;
//            }
//        }
        return true;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void writeMessage(String msg){
        this.out.println(msg);
        System.out.println("Send:" + msg);
    }

    @Override
    public void onLoginSuccess(String response, String userName) {
        System.out.println("Username " + this.userName + " changed to " + userName);
        this.userName = userName;
        writeMessage(response);
    }

    @Override
    public void onLoginFailed(String response, String userName) {
        System.out.println("Username " + this.userName + " (old) " + userName + " (new) " + "loginFailed");
        writeMessage(response);
    }

    @Override
    public void onResponse(String dstId, String msg) {
        for (UserHandler handler : Server.userHandlers) {
            if ((handler.isLoggedIn()) && (dstId.equals(handler.getUserName()))) {
                handler.writeMessage(msg);
                break;
            }
        }
    }

    @Override
    public void onConnectionClose() {
        System.out.println("exited");
        this.isLoggedIn=false;
        loopFlag = false;
        try {
            this.socket.close();
            Server.userHandlers.remove(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
