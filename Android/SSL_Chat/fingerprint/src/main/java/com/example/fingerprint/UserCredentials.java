package com.example.fingerprint;

import org.json.JSONException;
import org.json.JSONObject;

public class UserCredentials {
    private final String IP_KEY = "IP_KEY";
    private final String SOCKET_KEY = "SOCKET_KEY";
    private final String USERNAME_KEY = "USERNAME_KEY";

    private String ip, socket, username;

    public UserCredentials(String ip, String socket, String username) {
        this.ip = ip;
        this.socket = socket;
        this.username = username;
    }

    public UserCredentials(JSONObject jsonObject){
        try {
            this.ip = jsonObject.getString(IP_KEY);
            this.socket = jsonObject.getString(SOCKET_KEY);
            this.username = jsonObject.getString(USERNAME_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean valid(){
        return getIp() != null && getSocket() != null && getUsername() != null;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "ip='" + ip + '\'' +
                ", socket='" + socket + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(IP_KEY, ip);
            jsonObject.put(SOCKET_KEY, socket);
            jsonObject.put(USERNAME_KEY, username);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
