package main;

public class RawMessage {
    int headerLen, fileLen;
    byte[] rawData;

    public RawMessage(int headerLen, int fileLen, byte[] rawData) {
        this.headerLen = headerLen;
        this.fileLen = fileLen;
        this.rawData = rawData;
    }

    public RawMessage(byte[] rawData) {
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

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }
}
