package org.mustream.client.net;

import org.muplayer.audio.Track;
import org.muplayer.audio.info.AudioTag;
import org.mustream.common.PackageHeader;
import org.mustream.common.audio.AudioFormatUtils;
import org.mustream.common.audio.SoundData;
import org.mustream.common.io.MSOutputStream;
import org.mustream.common.net.MustreamPackage;
import org.mustream.common.net.NeoOutputStream;
import org.mustream.common.sys.SysInfo;
import org.mustream.common.sys.TaskRunner;
import org.mustream.common.sys.TrackAlert;
import org.mustream.client.thread.ThreadUtil;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

public class Client extends Thread{
    private Socket socket;
    private MSOutputStream outputStream;
    private final Deque<File> dequeFiles;
    private AudioSender audioSender;

    public Client() {
        this("localhost");
    }

    public Client(String host) {
        try {
            socket = new Socket(host, SysInfo.MUSTREAMER_PORT);
            outputStream = new MSOutputStream(socket.getOutputStream());
        } catch (IOException e) {
        }
        dequeFiles = new LinkedList<>();
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

    @Override
    public void run() {
        File soundFile;

        while (true) {
            waitForSongs();
            soundFile = dequeFiles.pollFirst();
            audioSender = new AudioSender(soundFile, outputStream);
            audioSender.start();
        }
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Invalid arguments");
        }
        else {
            String host = null;
            Integer port = null;
            File fSound = null;
            if (args[0].equals("-h"))
                host = args[1];
            else if (args[0].equals("-p")){
                try {
                    port = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    port = null;
                }
            }
            else {

            }
        }
    }

}
