package org.mustream.common.io;

import org.mustream.common.PackageHeader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MSOutputStream extends OutputStream {
    private final OutputStream outputStream;
    private final DataConverter dataConverter;

    public MSOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.dataConverter = new DataConverter();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        if (bytes != null && bytes.length > 0) {
            writeInt(bytes.length);
            for (int i = 0; i < bytes.length; i++)
                write(bytes[i]);
        }
    }

    public void writeAudio(byte[] audioBytes) throws IOException {
        writeInt(PackageHeader.AUDIO_DATA);
        writeBytes(audioBytes);
    }

    public void writeBoolean(boolean v) throws IOException {
        outputStream.write(v ? 1 : 0);
    }

    public void writeByte(int v) throws IOException {
        outputStream.write(v);
    }

    public void writeShort(int v) throws IOException {
        outputStream.write((v >>> 8) & 0xFF);
        outputStream.write((v >>> 0) & 0xFF);
    }

    public void writeChar(int v) throws IOException {
        outputStream.write((v >>> 8) & 0xFF);
        outputStream.write((v >>> 0) & 0xFF);
    }

    public void writeInt(int v) throws IOException {
        outputStream.write((v >>> 24) & 0xFF);
        outputStream.write((v >>> 16) & 0xFF);
        outputStream.write((v >>>  8) & 0xFF);
        outputStream.write((v >>>  0) & 0xFF);
    }

    public void writeLong(long v) throws IOException {
        final byte[] writeBuffer = new byte[8];
        writeBuffer[0] = (byte)(v >>> 56);
        writeBuffer[1] = (byte)(v >>> 48);
        writeBuffer[2] = (byte)(v >>> 40);
        writeBuffer[3] = (byte)(v >>> 32);
        writeBuffer[4] = (byte)(v >>> 24);
        writeBuffer[5] = (byte)(v >>> 16);
        writeBuffer[6] = (byte)(v >>>  8);
        writeBuffer[7] = (byte)(v >>>  0);
        outputStream.write(writeBuffer, 0, 8);
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeChars(String s) throws IOException {
        int len = s.length();
        for (int i = 0 ; i < len ; i++) {
            int v = s.charAt(i);
            outputStream.write((v >>> 8) & 0xFF);
            outputStream.write((v >>> 0) & 0xFF);
        }
    }

    public void writeString(String str) throws IOException {
        writeBytes(str.getBytes(StandardCharsets.ISO_8859_1));
    }

    @Override
    public void write(byte[] b) throws IOException {
        writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeBytes(Arrays.copyOfRange(b, off, off+len));
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
