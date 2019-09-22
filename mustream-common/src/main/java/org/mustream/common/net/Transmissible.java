package org.mustream.common.net;

import java.io.IOException;

public interface Transmissible {
    public void send(byte[] data) throws IOException;
    public void send(String data) throws IOException;
    public void send(byte data) throws IOException;
    public void send(short data) throws IOException;
    public void send(int data) throws IOException;
    public void send(long data) throws IOException;
    public void send(float data) throws IOException;
    public void send(double data) throws IOException;
}
