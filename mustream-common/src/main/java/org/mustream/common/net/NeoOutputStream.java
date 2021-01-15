package org.mustream.common.net;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class NeoOutputStream extends OutputStream {

    private final OutputStream out;

    public NeoOutputStream(OutputStream out) {
        this.out = out;
    }

    public final void writeBoolean(boolean v) throws IOException {
        out.write(v ? 1 : 0);
    }

    public final void writeByte(byte v) throws IOException {
        out.write(v);
    }

    public final void writeShort(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public final void writeChar(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write((v >>> 0) & 0xFF);
    }

    public final void writeInt(int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>>  8) & 0xFF);
        out.write((v >>>  0) & 0xFF);
    }

    public final void writeLong(long v) throws IOException {
        byte writeBuffer[] = new byte[8];
        writeBuffer[0] = (byte)(v >>> 56);
        writeBuffer[1] = (byte)(v >>> 48);
        writeBuffer[2] = (byte)(v >>> 40);
        writeBuffer[3] = (byte)(v >>> 32);
        writeBuffer[4] = (byte)(v >>> 24);
        writeBuffer[5] = (byte)(v >>> 16);
        writeBuffer[6] = (byte)(v >>>  8);
        writeBuffer[7] = (byte)(v >>>  0);
        out.write(writeBuffer, 0, 8);
    }

    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public final void writeString(String s) throws IOException {
        write(s.getBytes());
    }

    public final void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 0) & 0xFF);
        }
    }

    public void writeObject(Object obj) throws IOException {
        final ByteArrayOutputStream byteOs = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(byteOs);
        oos.writeObject(obj);
        write(byteOs.toByteArray());
        oos.close();
        byteOs.close();
    }

    public void writePackage(MustreamPackage pkg) throws IOException {
        writeObject(pkg);
    }

    public void writePackage(Object obj) throws IOException {
        writePackage(new MustreamPackage(obj));
    }

    public void writeFile(File file) throws IOException {
        final byte[] bytes = Files.readAllBytes(file.toPath());
        final int fileLenght = bytes.length;

        final int partSize = 10240;
        int parts = fileLenght / partSize;
        int rest = fileLenght % partSize;

        writeInt(parts);
        writeInt(rest);
        writeInt(partSize);

        for (int i = 0; i < fileLenght; i+=partSize) {
            write(Arrays.copyOfRange(bytes, i, i+partSize));
        }

        if (rest > 0) {
            write(Arrays.copyOfRange(bytes, fileLenght-rest, fileLenght));
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeInt(len-off);
        System.out.println("WriteBytesLenght: "+(len-off));
        for (int i = 0; i < len; i++)
            write(b[i+off]);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

}
