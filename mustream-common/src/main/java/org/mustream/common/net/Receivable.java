package org.mustream.common.net;

import java.io.IOException;

public interface Receivable {
    public byte[] readBytes() throws IOException;
    public String readString() throws IOException;
    public byte readByte() throws IOException;
    public short readShort() throws IOException;
    public int readInt() throws IOException;
    public long readLong() throws IOException;
    public float readFloat() throws IOException;
    public double readDouble() throws IOException;
}
