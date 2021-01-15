package org.mustream.common.io;

import java.io.EOFException;

public class DataConverter {

    public byte[] shortToBytes(short sh) {
        final byte[] bytes = new byte[2];
        bytes[0] = (byte) ((sh >>> 8) & 0xFF);
        bytes[1] = (byte) ((sh >>> 0) & 0xFF);
        return bytes;
    }

    public byte[] intToBytes(int i) {
        final byte[] bytes = new byte[4];
        bytes[0] = (byte) ((i >>> 24) & 0xFF);
        bytes[1] = (byte) ((i >>> 16) & 0xFF);
        bytes[2] = (byte) ((i >>> 8) & 0xFF);
        bytes[3] = (byte) ((i >>> 0) & 0xFF);
        return bytes;
    }

    public byte[] longToBytes(long l) {
        final byte[] bytes = new byte[8];
        bytes[0] = (byte) ((l >>> 56) & 0xFF);
        bytes[1] = (byte) ((l >>> 48) & 0xFF);
        bytes[2] = (byte) ((l >>> 40) & 0xFF);
        bytes[3] = (byte) ((l >>> 32) & 0xFF);
        bytes[4] = (byte) ((l >>> 24) & 0xFF);
        bytes[5] = (byte) ((l >>> 16) & 0xFF);
        bytes[6] = (byte) ((l >>> 8) & 0xFF);
        bytes[7] = (byte) ((l >>> 0) & 0xFF);
        return bytes;
    }

    public long bytesToLong(byte[] bytes) {
        return (((long)bytes[0] << 56) +
                ((long)(bytes[1] & 255) << 48) +
                ((long)(bytes[2] & 255) << 40) +
                ((long)(bytes[3] & 255) << 32) +
                ((long)(bytes[4] & 255) << 24) +
                ((bytes[5] & 255) << 16) +
                ((bytes[6] & 255) <<  8) +
                ((bytes[7] & 255) <<  0));
    }

    public int bytesToInt(byte[] bytes) throws EOFException {
        int ch1 = bytes[0];
        int ch2 = bytes[1];
        int ch3 = bytes[2];
        int ch4 = bytes[3];
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }

    public short bytesToShort(byte[] bytes) throws EOFException {
        int ch1 = bytes[0];
        int ch2 = bytes[1];
        if ((ch1 | ch2) < 0)
            throw new EOFException();
        return (short)((ch1 << 8) + (ch2 << 0));
    }

}
