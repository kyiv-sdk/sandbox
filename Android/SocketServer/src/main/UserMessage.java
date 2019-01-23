package main;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UserMessage {
    private String keyAction;
    private String dstID;
    private String srcID;

    private String message;
    private byte[] file;
    private int fileSliceID;
    private int fileID;
    private boolean isLast;

    private int width, height;

    private int photoLength;

    private ArrayList<String> allLoggedUsersList;

    public UserMessage() {
        this.keyAction = null;
        this.dstID = null;
        this.srcID = null;
        this.message = null;
        this.file = null;
        this.fileSliceID = -1;
        this.fileID = -1;
        this.isLast = false;
        this.width = -1;
        this.height = -1;
        this.photoLength = -1;
        this.allLoggedUsersList = null;
    }

    public UserMessage(String keyAction) {
        this.keyAction = keyAction;
        this.dstID = null;
        this.srcID = null;
        this.message = null;
        this.file = null;
        this.fileSliceID = -1;
        this.fileID = -1;
        this.isLast = false;
        this.width = -1;
        this.height = -1;
        this.photoLength = -1;
        this.allLoggedUsersList = null;
    }

    public String getKeyAction() {
        return keyAction;
    }

    public void setKeyAction(String keyAction) {
        this.keyAction = keyAction;
    }

    public String getDstID() {
        return dstID;
    }

    public void setDstID(String dstID) {
        this.dstID = dstID;
    }

    public String getSrcID() {
        return srcID;
    }

    public void setSrcID(String srcID) {
        this.srcID = srcID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public int getFileSliceID() {
        return fileSliceID;
    }

    public void setFileSliceID(int fileSliceID) {
        this.fileSliceID = fileSliceID;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ArrayList<String> getAllLoggedUsersList() {
        return allLoggedUsersList;
    }

    public void setAllLoggedUsersList(ArrayList<String> allLoggedUsersList) {
        this.allLoggedUsersList = allLoggedUsersList;
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (this.keyAction != null && !this.keyAction.equals("")) {
                jsonObject.put("keyAction", keyAction);
            }
            if (this.message != null && !this.message.equals("")) {
                jsonObject.put("message", this.message);
            }
            if (this.srcID != null && !this.srcID.equals("")) {
                jsonObject.put("srcID", this.srcID);
            }
            if (this.dstID != null && !this.dstID.equals("")) {
                jsonObject.put("dstID", this.dstID);
            }
            if (this.allLoggedUsersList != null && !this.allLoggedUsersList.isEmpty()) {
                jsonObject.put("loggedUsers", this.allLoggedUsersList);
            }

            if (this.file != null) {
                if (this.fileSliceID != -1) {
                    jsonObject.put("fileSliceID", this.fileSliceID);
                }

                if (this.fileID != -1) {
                    jsonObject.put("fileID", this.fileID);
                }

                if (this.width != -1) {
                    jsonObject.put("width", this.width);
                }

                if (this.height != -1) {
                    jsonObject.put("height", this.height);
                }

                if (this.photoLength != -1) {
                    jsonObject.put("photoLength", this.photoLength);
                }

                jsonObject.put("isLast", this.isLast);

//                JSONArray jsonArray = new JSONArray();
//                for (byte b : this.file){
//                    jsonArray.put(b);
//                }
//                jsonObject.put("file", jsonArray);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        String result = this.toJSON().toString();

        return result;
    }

    public byte[] getBytes(){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            stream.write((byte)1);
            int len = this.toString().length();
            StringBuilder strLen = new StringBuilder(String.valueOf(len));
            while (strLen.length() < 8){
                strLen.insert(0, '0');
            }
            stream.write(strLen.toString().getBytes());
            stream.write((byte)2);

            int fLen = 0;
            if (file != null) {
                fLen = file.length;
            }
            StringBuilder strFLen = new StringBuilder(String.valueOf(fLen));
            while (strFLen.length() < 8){
                strFLen.insert(0, '0');
            }
            stream.write(strFLen.toString().getBytes());
            stream.write((byte)2);

            stream.write(this.toString().getBytes());
            if (this.file != null) {
                stream.write(this.file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stream.toByteArray();
    }
}
