package main;

import java.io.BufferedReader;
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
        System.out.println("main.Reader started");
        while (loopFlag){
            try {
                inputLine = in.readLine();
                System.out.println("Received: " + inputLine);
                synchronized(userMessages) {
                    userMessages.add(new UserMessage(inputLine));
                    userMessages.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Caught error, closing connection");
                synchronized(userMessages) {
                    userMessages.add(new UserMessage("exit"));
                    userMessages.notifyAll();
                }
            }
        }
        System.out.println("main.Reader closed");

    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }
}