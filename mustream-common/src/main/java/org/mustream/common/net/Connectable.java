package org.mustream.common.net;

import java.io.IOException;

public interface Connectable extends Transmissible, Receivable {
    public default void closeAll() throws IOException {
        closeStreams();
        closeConnection();
    }
    public void closeConnection() throws IOException;
    public void closeStreams() throws IOException;

}
