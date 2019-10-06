package org.mustream.server.net;

import org.aucom.sound.Speaker;
import org.muplayer.audio.Track;
import org.mustream.common.net.NeoInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayDeque;

public class SoundPlayer extends Thread {
    private Track track;
    private NeoInputStream dataIn;
    private final ArrayDeque<byte[]> dequeBytes;

    private volatile boolean on;

    public SoundPlayer(String format) throws LineUnavailableException {
        this.speaker = new Speaker(format);
        dequeBytes = new ArrayDeque<>();
        on = false;
    }

    private byte[] readBytes() {
        while (dequeBytes.isEmpty()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return dequeBytes.pollFirst();
    }

    public synchronized void addBytes(byte[] bytes) {
        dequeBytes.add(bytes);
    }

    public void shutdown() {
        on = false;
    }

    @Override
    public void run() {
        on = true;
        try {
            speaker.open();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        while (on || !dequeBytes.isEmpty()) {
            speaker.playAudio(readBytes());
        }
        speaker.close();
        dequeBytes.clear();
    }
}
