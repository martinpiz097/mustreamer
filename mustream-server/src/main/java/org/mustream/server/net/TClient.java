package org.mustream.server.net;

import org.mustream.common.PackageHeader;
import org.mustream.common.audio.AudioFormatUtils;
import org.mustream.common.audio.FormatData;
import org.mustream.common.audio.SoundData;
import org.mustream.common.net.NeoInputStream;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

public class TClient extends Thread {
    private Socket cliSock;
    private NeoInputStream inputStream;

    private Deque<SoundPlayer> playerDeque;
    private SoundPlayer soundPlayer;

    public TClient(Socket cliSock) throws IOException {
        this.cliSock = cliSock;
        inputStream = new NeoInputStream(cliSock.getInputStream());
        playerDeque = new ArrayDeque<>();
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

    @Override
    public void run() {
        try {
            String header;
            while (true) {
                header = inputStream.readString();
                System.out.println("Header: "+header);

                switch (header) {
                    case PackageHeader.SOUND:
                        SoundData soundData = (SoundData) inputStream.readObject();
                        soundPlayer = new SoundPlayer(AudioFormatUtils
                                .getAudioFormat((FormatData) inputStream.readObject()));
                        soundPlayer.start();
                        break;

                    case PackageHeader.AUDIO_DATA:
                        soundPlayer.addBytes(inputStream.readAllBytes());
                        break;

                    case PackageHeader.NEXT:
                        killSoundPlayer();
                }
                sleep();
            }
        } catch (IOException | ClassNotFoundException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }

}
