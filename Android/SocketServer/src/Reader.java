import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Reader implements Runnable{
    final List<UserMessage> userMessages;
    boolean loopFlag;
    private final BufferedReader in;

    public Reader(BufferedReader in, List<UserMessage> userMessages) {
        this.userMessages = userMessages;
        this.loopFlag = true;
        this.in = in;
    }

    @Override
    public void run() {
        String inputLine;
        while (loopFlag){
            try {
                inputLine = in.readLine();
                synchronized(userMessages) {
                    userMessages.add(new UserMessage(inputLine));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            this.in.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }
}
