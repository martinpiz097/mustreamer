package org.mustream.server.net;

import org.muplayer.audio.Track;
import org.mustream.server.data.SoundData;

import java.io.*;
import java.util.*;

public class TStreamPlaying extends Thread {
    private volatile Deque<SoundData> dequeSoundData;
    private volatile Track current;

    public TStreamPlaying() {
        this.dequeSoundData = new ArrayDeque<>();
    }

    private boolean isCurrentActive() {
        return current != null && !current.isFinished();
    }

    private void waitForData() {
        while (dequeSoundData.isEmpty()) {
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void glueSoundData(SoundData soundData) {
        dequeSoundData.add(soundData);
    }

    public synchronized Deque<SoundData> getDequeSoundData() {
        return dequeSoundData;
    }

    public synchronized void finishCurrent() {
        if (isCurrentActive()) {
            current.finish();
        }
    }

    @Override
    public void run() {
        while (true) {
            waitForData();
            if (!isCurrentActive()) {
                current = dequeSoundData.pollFirst().getSoundTrack();
                current.start();
            }
            sleep();
        }

    }
}
