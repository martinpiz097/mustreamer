package org.mustream.server.net;

import org.aucom.sound.Speaker;
import org.muplayer.audio.formats.io.AudioDataInputStream;
import org.muplayer.audio.formats.io.AudioDataOutputStream;
import org.muplayer.system.TrackStates;
import org.mustream.common.audio.AudioFormatUtils;
import org.mustream.common.audio.FormatData;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class SoundPlayer extends Thread {
    private Speaker speaker;
    private volatile boolean on;

    private AudioDataInputStream audioDataInputStream;
    private AudioDataOutputStream audioDataOutputStream;

    private boolean ready;

    public SoundPlayer(FormatData formatData) throws LineUnavailableException {
        speaker = new Speaker(AudioFormatUtils.getAudioFormat(formatData));
        audioDataInputStream = new AudioDataInputStream();
        audioDataOutputStream = new AudioDataOutputStream(audioDataInputStream.getByteBuffer());
        on = false;
        ready = false;
    }

    public boolean hasAudioData() throws IOException {
        return audioDataInputStream.available() > 0;
    }

    private byte[] readBytes() throws IOException {
        int available;
        while ((available = audioDataInputStream.available()) == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        byte[] bytes = new byte[Math.min(available, 4096)];
        audioDataInputStream.read(bytes);
        return bytes;
    }

    private void waitForData() {
        try {
            while (!hasAudioData()) {
                sleep(100);
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addBytes(byte[] bytes) throws IOException {
        audioDataOutputStream.write(bytes);
    }

    public synchronized boolean isPrepared() {
        return ready;
    }

    public synchronized void prepareToFinish() {
        ready = true;
    }

    public void shutdown() {
        on = false;
    }

    @Override
    public void run() {
        on = true;
        try {
            speaker.open();
            waitForData();

            while ((on && !ready) || hasAudioData()) {
                speaker.playAudio(readBytes());
            }
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
        speaker.close();
    }
}
