package main;

import main.basic_server.BasicServer;
import main.ssl_server.SSL_Server;

public class Main {
    public static void main(String[] args) {
//        BasicServer basicServer = new BasicServer();
//        basicServer.startServer(4444);
        SSL_Server ssl_server = new SSL_Server();
        ssl_server.startServer(4444);
    }
}
