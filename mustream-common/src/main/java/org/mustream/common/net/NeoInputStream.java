package org.mustream.common.net;

import org.mustream.common.sys.SysInfo;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class NeoInputStream extends InputStream {

    private final InputStream in;

    public NeoInputStream(InputStream in) {
        super();
        this.in = in;
    }

    public byte[] readAllBytes() throws IOException {
        return readAllBytes(readInt());
    }

    public byte[] readAllBytes(int size) throws IOException {
        byte[] buffer = new byte[size];

        for (int i = 0; i < size; i++) {
            buffer[i] = (byte) read();
        }
        return buffer;
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIs = new ByteArrayInputStream(readAllBytes());
        ObjectInputStream ois = new ObjectInputStream(byteIs);
        Object obj = ois.readObject();
        ois.close();
        byteIs.close();
        return obj;
    }

    public MustreamPackage readPackage() throws IOException, ClassNotFoundException {
        return (MustreamPackage) readObject();
    }

    public File readFile() throws IOException {
        File tmp = new File(SysInfo.SERVER_TEMP_FOLDER, String.valueOf(new Date().getTime()));
        tmp.createNewFile();
        final Path tmpPath = tmp.toPath();

        final int parts = readInt();
        final int rest = readInt();
        //final int partSize = readInt();

        for (int i = 0; i < parts; i++) {
            Files.write(tmpPath, readAllBytes(), StandardOpenOption.APPEND);
        }

        if (rest > 0) {
            Files.write(tmpPath, readAllBytes(), StandardOpenOption.APPEND);
        }

        return tmp;

    }

    public String readString() throws IOException {
        return readString(Charset.defaultCharset());
    }

    public String readString(Charset charset) throws IOException {
        return new String(readAllBytes(), charset);
    }

    public String readString(String charset) throws IOException {
        return readString(Charset.forName(charset));
    }

    public final boolean readBoolean() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (ch != 0);
    }

    public final byte readByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return (byte)(ch);
    }

    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if (ch < 0)
            throw new EOFException();
        return ch;
    }

    public final short readShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

    public final int readUnsignedShort() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (ch1 << 8) + (ch2 << 0);
    }

    public final char readChar() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (char)((ch1 << 8) + (ch2 << 0));
    }

    public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }


    public final long readLong() throws IOException {
        byte readBuffer[] = new byte[8];
        read(readBuffer);
        return (((long)readBuffer[0] << 56) +
                ((long)(readBuffer[1] & 255) << 48) +
                ((long)(readBuffer[2] & 255) << 40) +
                ((long)(readBuffer[3] & 255) << 32) +
                ((long)(readBuffer[4] & 255) << 24) +
                ((readBuffer[5] & 255) << 16) +
                ((readBuffer[6] & 255) <<  8) +
                ((readBuffer[7] & 255) <<  0));
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int limit = off+len;
        if (limit >= b.length)
            limit = b.length;

        for (int i = off; i < limit; i++)
            b[i] = (byte) read();
        return limit;
    }

    @Override
    public long skip(long n) throws IOException {
        return in.skip(n);
    }

    @Override
    public int available() throws IOException {
        return in.available();
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        in.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        in.reset();
    }

    @Override
    public boolean markSupported() {
        return in.markSupported();
    }

    public int read() throws IOException {
        return in.read();
    }

}
