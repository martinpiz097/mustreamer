package org.mustream.server.net;

import org.mustream.common.PackageHeader;
import org.mustream.common.io.MSInputStream;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.Socket;

public class TClient extends Thread {
    private final Socket cliSock;
    private final MSInputStream inputStream;

    private volatile SoundPlayer soundPlayer;
    private volatile boolean on;

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
            int fileSize;
            while (on) {
                read = inputStream.readInt();
                switch (read) {
                    case PackageHeader.AUDIO_DATA:
                        soundPlayer.addBytes(inputStream.readBytes());
                        break;
                    case PackageHeader.SOUND:
                        if (soundPlayer != null)
                            soundPlayer.shutdown();
                        fileSize = inputStream.readInt();
                        soundPlayer = new SoundPlayer(fileSize);
                        soundPlayer.start();
                        break;
                }
                sleep(10);
            }

        } catch (IOException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
