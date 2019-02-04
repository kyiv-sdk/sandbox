package com.example.iyuro.ssl_chat.login;

import org.json.JSONException;
import org.json.JSONObject;

public class UserCredentials {
    private final String IP_KEY = "IP_KEY";
    private final String PORT_KEY = "SOCKET_KEY";
    private final String USERNAME_KEY = "USERNAME_KEY";

    private String ip, port, username;

    public UserCredentials(String ip, String socket, String username) {
        this.ip = ip;
        this.port = socket;
        this.username = username;
    }

    public UserCredentials(JSONObject jsonObject){
        try {
            this.ip = jsonObject.getString(IP_KEY);
            this.port = jsonObject.getString(PORT_KEY);
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean valid(){
        return getIp() != null && getPort() != null && getUsername() != null;
    }

    @Override
    public String toString() {
        return "UserCredentials{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public JSONObject toJSON(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(IP_KEY, ip);
            jsonObject.put(PORT_KEY, port);
            jsonObject.put(USERNAME_KEY, username);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
