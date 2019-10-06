package org.mustream.server.net;

import org.mustream.common.audio.FormatData;
import org.mustream.common.net.MustreamPackage;
import org.mustream.common.net.NeoInputStream;
import org.mustream.common.sys.TrackAlert;

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

    private boolean on;

    public TClient(Socket cliSock) throws IOException {
        this.cliSock = cliSock;
        inputStream = new NeoInputStream(cliSock.getInputStream());
        playerDeque = new ArrayDeque<>();
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

    public boolean hasSoundPlayers() {
        return !playerDeque.isEmpty();
    }

    public SoundPlayer getNext() {
        return playerDeque.peekFirst();
    }

    public SoundPlayer getAndRemoveNext() {
        return playerDeque.pollFirst();
    }

    public synchronized void shutdown() {
        on = false;
    }

    @Override
    public void run() {
        try {
            on = true;
            MustreamPackage pkg;
            Class objClass;

            while (on) {
                pkg = inputStream.readPackage();
                objClass = pkg.getObjectClass();

                if (objClass.equals(byte[].class)) {
                    if (soundPlayer.isAlive()) {
                        if (soundPlayer.isPrepared() && hasSoundPlayers()) {
                            getNext().addBytes(pkg.getObject());
                        }
                        else {
                            soundPlayer.addBytes(pkg.getObject());
                        }
                    }
                    else if (hasSoundPlayers()) {
                        soundPlayer = getAndRemoveNext();
                        soundPlayer.start();
                        soundPlayer.addBytes(pkg.getObject());
                    }
                }
                else if (objClass.equals(FormatData.class)){
                    SoundPlayer newSoundPlayer = new SoundPlayer(pkg.getObject());
                    if (soundPlayer == null) {
                        soundPlayer = newSoundPlayer;
                        soundPlayer.start();
                    }
                    else {
                        if (soundPlayer.isAlive()) {
                            playerDeque.add(newSoundPlayer);
                        }
                        else {
                            if (hasSoundPlayers()) {
                                playerDeque.add(newSoundPlayer);
                                soundPlayer = getAndRemoveNext();
                                soundPlayer.start();
                            }
                            else {
                                soundPlayer = newSoundPlayer;
                                soundPlayer.start();
                            }
                        }
                    }
                } else if (objClass.equals(TrackAlert.class)) {
                    if (pkg.getObject() == TrackAlert.TRACK_FINISHED) {
                        if (soundPlayer != null && soundPlayer.isAlive()) {
                            soundPlayer.prepareToFinish();
                        }
                    }
                }
                sleep(10);
            }

        } catch (IOException | ClassNotFoundException | LineUnavailableException | InterruptedException e) {
            e.printStackTrace();
        }

    }

}
