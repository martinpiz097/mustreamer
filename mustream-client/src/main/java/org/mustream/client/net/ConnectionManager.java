package org.mustream.client.net;

import java.io.IOException;

public class ConnectionManager {
    public static Client connectTo(String host) {
        boolean connected = false;
        Client client = null;

        while (!connected) {
            System.out.println("Trying connect to "+host);
            client = new Client(host);
            connected = client.isConnected();
        }
        System.out.println("Connected!");
        return client;
    }
}
