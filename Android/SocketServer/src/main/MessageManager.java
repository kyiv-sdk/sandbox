package main;

import java.util.List;

public class MessageManager implements Runnable{
    final List<RawMessage> inMessages;
    boolean loopFlag;
    ServerMessageProtocol serverMessageProtocol;

    public MessageManager(List<RawMessage> inMessages, ServerMessageProtocol serverMessageProtocol) {
        this.inMessages = inMessages;
        this.loopFlag = true;
        this.serverMessageProtocol = serverMessageProtocol;
    }

    @Override
    public void run() {
        System.out.println("main.MessageManager started");
        while (loopFlag){
            try {
                synchronized (inMessages) {
                    try {
                        inMessages.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("manager notified");
                    while (inMessages.size() > 0) {
                        RawMessage msg = inMessages.get(0);
                        inMessages.remove(0);

                        UserMessage userMessage = serverMessageProtocol.processNewMessage(msg);

                        switch (serverMessageProtocol.getMessageProtocolState()){
                            case WAIT_FOR_UNIQUEID:
                                serverMessageProtocol.manageUniqueID(userMessage);
                                System.out.println("uniqueId handled");
                                serverMessageProtocol.setMessageProtocolState(MessageProtocolStates.WAIT_FOR_LOGIN);
                                break;
                            case WAIT_FOR_LOGIN:
                                serverMessageProtocol.manageLogin(userMessage);
                                System.out.println("login handled");
                                break;
                            case WAIT_FOR_MESSAGE:
                                System.out.println("message handled");
                                serverMessageProtocol.manageMessage(userMessage);
                                break;
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            System.out.println("main.MessageManager closed");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }
}
