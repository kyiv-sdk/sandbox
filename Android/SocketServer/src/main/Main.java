package main;

import main.basic_server.BasicServer;
import main.ssl_server.SSL_Server;

public class Main {
    public static void main(String[] args) {
        BasicServer basicServer = new BasicServer(4545);
        Thread basicServerThread = new Thread(basicServer);
        basicServerThread.start();

        SSL_Server ssl_server = new SSL_Server(5454);
        Thread SSLServerThread = new Thread(ssl_server);
        SSLServerThread.start();
    }
}
