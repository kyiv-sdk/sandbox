import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Writer implements Runnable{
    final List<UserMessage> userMessages;
    boolean loopFlag;
    private final PrintWriter out;
    ServerMessageProtocol serverMessageProtocol;

    public Writer(PrintWriter out, List<UserMessage> userMessages, ServerMessageProtocol serverMessageProtocol) {
        this.userMessages = userMessages;
        this.loopFlag = true;
        this.out = out;
        this.serverMessageProtocol = serverMessageProtocol;
    }

    @Override
    public void run() {
        while (loopFlag){
            synchronized(userMessages) {
                if (userMessages.size() > 0) {
                    String msg = userMessages.get(0).rawMessage;
                    serverMessageProtocol.processNewMessage(msg);
                    userMessages.remove(0);
                }
            }
        }
        try {
            this.out.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }
}
