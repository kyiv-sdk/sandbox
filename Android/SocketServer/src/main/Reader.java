package main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Reader implements Runnable{
    private final List<RawMessage> userMessages;
    private boolean loopFlag;
    private final InputStream in;

    public Reader(InputStream in, List<RawMessage> userMessages) {
        this.userMessages = userMessages;
        this.loopFlag = true;
        this.in = in;
    }

    @Override
    public void run() {
        System.out.println("Reader started");

        while (loopFlag){
            RawMessage rawMessage = read();
            if (rawMessage != null) {
                synchronized (userMessages) {
                    userMessages.add(rawMessage);
                    userMessages.notifyAll();
                }
            }
        }

        System.out.println("Reader closed");
    }

    public void setLoopFlag(boolean loopFlag) {
        this.loopFlag = loopFlag;
    }

    private RawMessage read(){
        try {
            int headerLen = readNum();
            int fileLen = readNum();

            int allLen = headerLen + fileLen;

            int MAX_BUF_SIZE = 1024;
            byte[] buf = new byte[MAX_BUF_SIZE];

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(headerLen + fileLen);

            int lenToRead = 0;
            int bytesRead = 0;
            while (allLen > 0) {
                if (allLen > MAX_BUF_SIZE) {
                    lenToRead = MAX_BUF_SIZE;
                } else {
                    lenToRead = allLen;
                }

                if ((bytesRead = in.read(buf, 0, lenToRead)) > 0) {
                    System.out.println("Reader: header len:" + headerLen);
                    System.out.println("Reader: file len:" + fileLen);
                    outputStream.write(buf, 0, bytesRead);
                } else {
                    throw new Exception("Reader: error while reading");
                }

                allLen -= bytesRead;
            }

            String newStr = outputStream.toString();
            System.out.println("Reader red: " + newStr);

            return new RawMessage(headerLen, fileLen, outputStream.toByteArray());
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Reader: caught error, closing connection");
            synchronized(userMessages) {
                userMessages.add(new RawMessage("".getBytes()));
                userMessages.notifyAll();
            }
        }

        return null;
    }

    private int readNum() throws Exception {
        int bufLen = 1;
        byte[] buf = new byte[bufLen];
        int result = 0;
        StringBuilder strLen = new StringBuilder();
        while (result == 0) {
            if (in.read(buf, 0, bufLen) > 0) {
                if (buf[0] == 2) {
                    result = Integer.parseInt(strLen.toString());
                    break;
                }
                if (buf[0] == 1) {
                    continue;
                }
                strLen.append(new String(buf));
            } else {
                throw new Exception("Reader: error while reading num");
            }
        }
        return result;
    }
}
