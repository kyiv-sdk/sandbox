import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class UserHandler implements Runnable{
    private String userId;
    private final PrintWriter out;
    private final BufferedReader in;
    Socket socket;
    boolean isloggedin;

    public UserHandler(Socket socket, String userId, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.userId = userId;
        this.out = out;
        this.in = in;
        this.isloggedin = true;
    }

    @Override
    public void run() {
        String inputLine;
        while (true){
            try {
                inputLine = in.readLine();
                System.out.println("Received: " + inputLine + " from " + this.userId);

                if(inputLine.equals("exit")){
                    System.out.println("exited");
                    this.isloggedin=false;
                    this.socket.close();
                    break;
                }

                JSONObject jsonObject = new JSONObject(inputLine);
                String msg = jsonObject.getString("message");

                if (msg.equals("login")){
                    String username = jsonObject.getString("username");
                    System.out.println("Username " + userId + " changed to " + username);
                    userId = username;
                    writeMessage("ok");
                } else {

                    for (UserHandler handler : Server.userHandlers) {
                        String destinationID = jsonObject.getString("dstID");
                        if ((handler.isloggedin()) && (destinationID.equals(handler.getUserId()))) {
                            handler.writeMessage(msg);
                            break;
                        }
                    }
                }

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

    public String getUserId() {
        return userId;
    }

    public PrintWriter getOut() {
        return out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public boolean isloggedin() {
        return isloggedin;
    }

    public void writeMessage(String msg){
        this.out.println(msg);
    }
}
