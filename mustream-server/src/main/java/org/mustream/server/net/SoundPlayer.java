package org.mustream.server.net;

import org.aucom.sound.Speaker;
import org.muplayer.audio.Track;
import org.mustream.common.audio.AudioFormatUtils;
import org.mustream.common.net.NeoInputStream;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;

public class SoundPlayer extends Thread {
    private volatile Track track;
    private volatile boolean on;

    private final ByteArrayOutputStream outputStream;
    private volatile int receivedData;

    public SoundPlayer() throws LineUnavailableException {
        outputStream = new ByteArrayOutputStream();
        receivedData = 0;
        on = false;
        setName("SoundPlayer "+getId());
    }

    private void waitForTrackPlaying() {
        while (track.isAlive()) {}
    }

    private void waitForData() {
        while (receivedData == 0) {}
    }

    public synchronized void addBytes(byte[] bytes) throws IOException {
        outputStream.write(bytes);
        receivedData+=bytes.length;
    }

    public void shutdown() {
        on = false;
    }

    @Override
    public void run() {
        on = true;
        waitForData();
        track = Track.getTrack(new ByteArrayInputStream(outputStream.toByteArray()));
        track.start();
        waitForTrackPlaying();
        System.gc();
    }
}
