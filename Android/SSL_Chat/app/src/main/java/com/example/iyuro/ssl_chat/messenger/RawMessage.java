package com.example.iyuro.ssl_chat.messenger;

public class RawMessage {
    int headerLen, fileLen;
    String rawData;

    public RawMessage(int headerLen, int fileLen, String rawData) {
        this.headerLen = headerLen;
        this.fileLen = fileLen;
        this.rawData = rawData;
    }

    public RawMessage(String rawData) {
        this.headerLen = -1;
        this.fileLen = -1;
        this.rawData = rawData;
    }

    public int getHeaderLen() {
        return headerLen;
    }

    public void setHeaderLen(int headerLen) {
        this.headerLen = headerLen;
    }

    public int getFileLen() {
        return fileLen;
    }

    public void setFileLen(int fileLen) {
        this.fileLen = fileLen;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}
