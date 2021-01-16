package org.mustream.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Objects;

public class ByteInputStream extends InputStream {
    protected final byte[] buf;
    protected int pos;
    protected int mark = 0;
    protected int count;
    protected int dataSize;

    public ByteInputStream(int size) {
        this.buf = new byte[size];
        this.pos = 0;
        this.count = buf.length;
        dataSize = 0;
    }

    public void addBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (dataSize+i >= buf.length)
                break;
            buf[dataSize++] = bytes[i];
        }
    }

    public synchronized int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public synchronized int read(byte[] b, int off, int len) {
        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    public synchronized byte[] readAllBytes() {
        byte[] result = Arrays.copyOfRange(buf, pos, count);
        pos = count;
        return result;
    }

    public int readNBytes(byte[] b, int off, int len) {
        int n = read(b, off, len);
        return n == -1 ? 0 : n;
    }

    public synchronized long transferTo(OutputStream out) throws IOException {
        int len = count - pos;
        out.write(buf, pos, len);
        pos = count;
        return len;
    }

    public synchronized long skip(long n) {
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }

    public synchronized int available() {
        return count - pos;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark = pos;
    }

    public synchronized void reset() {
        pos = mark;
    }

    public void close() throws IOException {
    }
}
