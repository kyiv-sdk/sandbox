package main;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Reader implements Runnable{
    final List<RawMessage> userMessages;
    boolean loopFlag;
    private final InputStream in;

    public Reader(InputStream in, List<RawMessage> userMessages) {
        this.userMessages = userMessages;
        this.loopFlag = true;
        this.in = in;
    }

    @Override
    public void run() {
        byte[] inputLine;
        System.out.println("main.Reader started");
        while (loopFlag){
            try {
                int headerLen = 0;

                int bufLen = 10;
                byte[] buf = new byte[bufLen];
                if (in.read(buf, 0 ,bufLen) > 0){
                    byte[] hLen = Arrays.copyOfRange(buf, 1, 9);
                    try {
                        headerLen = Integer.parseInt(new String(hLen));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                bufLen--;
                buf = new byte[bufLen];

                int fileLen = 0;
                if (in.read(buf, 0 ,bufLen) > 0){
                    byte[] fLen = Arrays.copyOfRange(buf, 0, 8);
                    try {
                        fileLen = Integer.parseInt(new String(fLen));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                int allLen = headerLen + fileLen;

                int MAX_BUF_SIZE = 1024;
                buf = new byte[MAX_BUF_SIZE];

//                while(allLen > 0){
//                    if (allLen > MAX_BUF_SIZE){
//                        bufLen = MAX_BUF_SIZE;
//                    } else {
//                        bufLen = allLen;
//                    }
//                    allLen -= bufLen;
//
//                    if (in.read(buf, i, bufLen) > 0) {
////                        System.out.println("Received: " + new String(buf));
//                        System.out.println("Reader: header len:" + headerLen);
//                        System.out.println("Reader: file len:" + fileLen);
//                    }
//
//                    i += bufLen;
//                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(headerLen + fileLen);

                int lenToRead = 0;
                int bytesRead = 0;
                while(allLen > 0){
                    if (allLen > MAX_BUF_SIZE){
                        lenToRead = MAX_BUF_SIZE;
                    } else {
                        lenToRead = allLen;
                    }


                    if ((bytesRead = in.read(buf, 0, lenToRead)) > 0) {
                        System.out.println("Reader: header len:" + headerLen);
                        System.out.println("Reader: file len:" + fileLen);
                        outputStream.write(buf, 0, bytesRead);
                    }

                    allLen -= bytesRead;
                }

                String newStr = outputStream.toString();
                int osLen = outputStream.size();
                int nstrLen = newStr.length();
//                fileLen = nstrLen - headerLen;
                RawMessage rawMessage = new RawMessage(headerLen, fileLen, outputStream.toByteArray());
                System.out.println("Reader red: " + newStr);

                synchronized (userMessages) {
                    userMessages.add(rawMessage);
                    userMessages.notifyAll();
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Caught error, closing connection");
                synchronized(userMessages) {
                    userMessages.add(new RawMessage("".getBytes()));
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
