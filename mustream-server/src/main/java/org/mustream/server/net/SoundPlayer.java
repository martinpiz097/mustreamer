package org.mustream.server.net;

import org.aucom.SysInfo;
import org.muplayer.audio.Track;
import org.mustream.common.io.ByteInputStream;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import java.io.*;

public class SoundPlayer extends Thread {
    private volatile Track track;
    private volatile boolean on;

    private final ByteInputStream inputStream;
    private volatile int receivedData;
    private final int fileSize;

    public SoundPlayer(int fileSize) throws LineUnavailableException {
        this.fileSize = fileSize;
        inputStream = new ByteInputStream(fileSize);
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
        inputStream.addBytes(bytes);
        receivedData+=bytes.length;
    }

    public void shutdown() {
        on = false;
        if (track != null)
            track.kill();
    }

    @Override
    public void run() {
        on = true;
        waitForData();
        track = Track.getTrack(inputStream);
        track.start();
        waitForTrackPlaying();
        System.gc();
    }
}
