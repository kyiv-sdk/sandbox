import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserHandler implements UserHandlerInterface {
    private String userName;
    private String uniqueUserId;
    private final PrintWriter out;
    private final BufferedReader in;
    private Socket socket;
    private boolean isLoggedIn;

    private Reader reader;
    private MessageManager messageManager;

    private ServerInterface serverInterface;

    private final List<UserMessage> messages;

    public UserHandler(ServerInterface serverInterface, Socket socket, String uniqueID, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.userName = "default";
        this.out = out;
        this.in = in;
        this.isLoggedIn = true;
        this.uniqueUserId = uniqueID;

        this.serverInterface = serverInterface;

        ServerMessageProtocol serverMessageProtocol = new ServerMessageProtocol(serverInterface, this);

        System.out.println("Created user handler for: " + uniqueUserId);

        messages = Collections.synchronizedList(new ArrayList<>());
        reader = new Reader(in, messages);
        Thread threadReader = new Thread(reader);
        threadReader.start();

        messageManager = new MessageManager(messages, serverMessageProtocol);
        Thread threadWriter = new Thread(messageManager);
        threadWriter.start();
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

    public synchronized void writeMessage(String msg){
        this.out.println(msg);
        System.out.println("Sent: " + msg + " to " + this.userName);
    }

    @Override
    public void onLoginSuccess(String response, String userName) {
        System.out.println("Username " + this.userName + " changed to " + userName);
        this.userName = userName;
        writeMessage(response);
        serverInterface.notifyAllUsersChanges();
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
    public void onLoginFailed(String response, String userName) {
        System.out.println("Username " + this.userName + " (old) " + userName + " (new) " + "loginFailed");
        writeMessage(response);
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
            messages.add(new UserMessage(msg));
            messages.notifyAll();
        }
    }

}
