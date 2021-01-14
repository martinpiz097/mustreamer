package org.mustream.server.net;

import org.mustream.common.sys.SysInfo;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Thread {
    private final ServerSocket serverSocket;
    private TClient client;

    private boolean connected;

    private static Server server;

    public static Server getInstance() throws IOException {
        if (server == null) {
            server = new Server();
        }
        return server;
    }


    private Server() throws IOException {
        serverSocket = new ServerSocket(SysInfo.MUSTREAMER_PORT);
        connected = false;
        setName("MustreamServer");
    }

    public void disconnect() {
        connected = false;
    }

    @Override
    public void run() {
        connected = true;

        while (connected) {
            if (client == null) {
                try {
                    client = new TClient(serverSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                client.start();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Server.getInstance().start();
    }

}
