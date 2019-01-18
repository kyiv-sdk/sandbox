package main.ssl_server;

import main.UserHandler;
import main.basic_server.BasicServer;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class SSL_Server extends BasicServer {

    public SSL_Server(int portNumber) {
        super(portNumber);
    }

    private static ServerSocketFactory getServerSocketFactory() {
            SSLServerSocketFactory ssf = null;
            try {
                SSLContext ctx;
                KeyManagerFactory kmf;
                KeyStore ks;
                char[] passphrase = "changeme".toCharArray();

                ctx = SSLContext.getInstance("TLS");
                kmf = KeyManagerFactory.getInstance("SunX509");
                ks = KeyStore.getInstance("JKS");

                ks.load(new FileInputStream("src/main/ssl_server/selfsigned.jks"), passphrase);
                kmf.init(ks, passphrase);
                ctx.init(kmf.getKeyManagers(), null, null);

                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    @Override
    public void run() {
        try {
            ServerSocketFactory ssf = getServerSocketFactory();
            ServerSocket serverSocket = ssf.createServerSocket(portNumber);

            System.out.println("SSL_Server started successfully");

            while (true){
                Socket clientSocket = serverSocket.accept();

                System.out.println("SSL_Server accepted new connection");

                PrintWriter out =
                        new PrintWriter(clientSocket.getOutputStream(), true);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));

                UserHandler newUserHandler = new UserHandler(this, clientSocket, out, in);

                userHandlers.add(newUserHandler);
            }

        } catch (IOException e) {
            System.out.println("Unable to start SSL server: " +
                    e.getMessage());
            e.printStackTrace();
        }
    }
}
