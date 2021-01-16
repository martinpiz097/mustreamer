package org.mustream.client.net;

import org.mustream.common.io.MSOutputStream;
import org.mustream.common.sys.SysInfo;
import org.mustream.common.sys.TrackAlert;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.http.HttpClient;
import java.util.Deque;
import java.util.LinkedList;

public class Client extends Thread{
    private Socket socket;
    private MSOutputStream outputStream;
    private final Deque<File> dequeFiles;
    private AudioSender audioSender;

    private volatile boolean connected;

    public Client() {
        this("localhost");
    }

    public Client(String host) {
        try {
            socket = new Socket(host, SysInfo.MUSTREAMER_PORT);
            outputStream = new MSOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dequeFiles = new LinkedList<>();
        connected = false;

        setName("Client "+socket.getRemoteSocketAddress().toString());
    }

    private void waitForSongs() {
        while (dequeFiles.isEmpty()) {
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

    public boolean isConnected() {
        return socket != null;
    }

    public boolean glueSongFile(File fSound) {
        if (fSound == null || !fSound.exists()) {
            return false;
        }
        else {
            dequeFiles.add(fSound);
            return true;
        }
    }

    public void sendNext() {
        interrupt();
    }

    public AudioSender getAudioSender() {
        return audioSender;
    }

    public void pausePlayer() throws IOException {
        outputStream.write(TrackAlert.PAUSE);
        suspend();
    }

    public void resumePlayer() throws IOException {
        outputStream.write(TrackAlert.RESUME);
        resume();
    }

    public void disconnect() {
        connected = false;
    }

    @Override
    public void run() {
        connected = true;
        File soundFile;

        while (connected) {
            waitForSongs();
            soundFile = dequeFiles.pollFirst();
            if (audioSender != null)
                audioSender.interrupt();
            audioSender = new AudioSender(soundFile, outputStream);
            audioSender.start();
        }
    }

}
