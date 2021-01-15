package org.mustream.server.net;

import org.mustream.common.PackageHeader;
import org.mustream.common.audio.FormatData;
import org.mustream.common.io.MSInputStream;
import org.mustream.common.net.MustreamPackage;
import org.mustream.common.net.NeoInputStream;
import org.mustream.common.sys.TrackAlert;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

public class TClient extends Thread {
    private final Socket cliSock;
    private final MSInputStream inputStream;

    private SoundPlayer soundPlayer;
    private boolean on;

    public TClient(Socket cliSock) throws IOException {
        this.cliSock = cliSock;
        inputStream = new MSInputStream(cliSock.getInputStream());
        on = false;

        setName("TClient "+cliSock.getInetAddress().toString());
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void killSoundPlayer() {
        if (soundPlayer != null && soundPlayer.isAlive()) {
            soundPlayer.shutdown();
        }
    }

    public synchronized void shutdown() {
        on = false;
    }

    @Override
    public void run() {
        try {
            on = true;

            int read;
            while (on) {
                read = inputStream.readInt();
                if (read == PackageHeader.AUDIO_DATA) {
                    if (soundPlayer != null)
                        soundPlayer.shutdown();
                    soundPlayer = new SoundPlayer();
                    soundPlayer.start();
                    soundPlayer.addBytes(inputStream.readAudio());
                }
                sleep(10);
            }

        } catch (IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
