package main;

import main.basic_server.BasicServer;
import main.ssl_server.SSL_Server;

public class Main {
    private static final int BASIC_PORT = 4545;
    private static final int SSL_PORT = 5454;

    public static void main(String[] args) {
        BasicServer basicServer = new BasicServer(BASIC_PORT);
        Thread basicServerThread = new Thread(basicServer);
        basicServerThread.start();

        SSL_Server ssl_server = new SSL_Server(SSL_PORT);
        Thread SSLServerThread = new Thread(ssl_server);
        SSLServerThread.start();
    }
}
