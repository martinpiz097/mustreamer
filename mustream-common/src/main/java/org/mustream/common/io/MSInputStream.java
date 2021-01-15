package org.mustream.common.io;

import org.mustream.common.PackageHeader;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MSInputStream extends InputStream {

    private final InputStream inputStream;
    private final DataConverter dataConverter;

    public MSInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        this.dataConverter = new DataConverter();
    }

    private void waitForData() throws IOException {
        while (available() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasData() throws IOException {
        return available() > 0;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    public byte[] readBytes() throws IOException {
        final int count = readInt();
        final byte[] buffer = new byte[count];

        for (int i = 0; i < buffer.length; i++)
            buffer[i] = (byte) read();
        return buffer;
    }

    public byte[] readAudio() throws IOException {
        return readBytes();
    }

    public boolean readBoolean() throws IOException {
        int ch = inputStream.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    public byte readByte() throws IOException {
        int ch = inputStream.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    public int readUnsignedByte() throws IOException {
        int ch = inputStream.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    public short readShort() throws IOException {
        int ch1 = inputStream.read();
        int ch2 = inputStream.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    public int readUnsignedShort() throws IOException {
        int ch1 = inputStream.read();
        int ch2 = inputStream.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    public char readChar() throws IOException {
        int ch1 = inputStream.read();
        int ch2 = inputStream.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    public int readInt() throws IOException {
        int ch1 = inputStream.read();
        int ch2 = inputStream.read();
        int ch3 = inputStream.read();
        int ch4 = inputStream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public long readLong() throws IOException {
        byte[] bytes = readBytes();
        return (((long)bytes[0] << 56) +
                ((long)(bytes[1] & 255) << 48) +
                ((long)(bytes[2] & 255) << 40) +
                ((long)(bytes[3] & 255) << 32) +
                ((long)(bytes[4] & 255) << 24) +
                ((bytes[5] & 255) << 16) +
                ((bytes[6] & 255) <<  8) +
                ((bytes[7] & 255) <<  0));
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public String readString() throws IOException {
        return new String(readBytes(), StandardCharsets.ISO_8859_1);
    }

    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }


    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }
    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

}
