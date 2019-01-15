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
        System.out.println("Writer started");
        while (loopFlag){
            try {
                synchronized (userMessages) {
                    try {
                        userMessages.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (userMessages.size() > 0) {
                        String msg = userMessages.get(0).rawMessage;
                        serverMessageProtocol.processNewMessage(msg);
                        userMessages.remove(0);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            System.out.println("Writer closed");
            this.out.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }
}
