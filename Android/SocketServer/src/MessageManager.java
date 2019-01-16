import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MessageManager implements Runnable{
    final List<UserMessage> userMessages;
    boolean loopFlag;
    ServerMessageProtocol serverMessageProtocol;

    public MessageManager(List<UserMessage> userMessages, ServerMessageProtocol serverMessageProtocol) {
        this.userMessages = userMessages;
        this.loopFlag = true;
        this.serverMessageProtocol = serverMessageProtocol;
    }

    @Override
    public void run() {
        System.out.println("MessageManager started");
        while (loopFlag){
            try {
                synchronized (userMessages) {
                    try {
                        userMessages.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Reader notified");
                    while (userMessages.size() > 0) {
                        String msg = userMessages.get(0).rawMessage;
                        userMessages.remove(0);
                        serverMessageProtocol.processNewMessage(msg);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            System.out.println("MessageManager closed");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }
}
