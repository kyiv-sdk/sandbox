package com.example.iyuro.socketstest;

import java.net.URL;

public class RequestParser {
    String protocol, host;
    int port;

    public RequestParser() {
        this.protocol = "";
        this.host = "";
        this.port = 0;
    }

    public void parse(String request){
        try{
            if ((request.length() > 4) && (!request.substring(0, 4).toLowerCase().equals("http"))) {
                request = "http://" + request;
            }
            URL url = new URL(request);
            protocol = url.getProtocol().toUpperCase();
            host = url.getHost();
            port = url.getPort();
            if (port < 0) {
                if (protocol.equals("HTTP")){
                    port = 80;
                } else {
                    port = 443;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
