package org.mustream.client.net;

import org.muplayer.audio.Track;
import org.muplayer.audio.info.AudioTag;
import org.mustream.common.audio.AudioFormatUtils;
import org.mustream.common.audio.SoundData;
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
    private NeoOutputStream outputStream;
    private final Deque<File> dequeFiles;
    private AudioSender audioSender;

    public Client() {
        this("localhost");
    }

    public Client(String host) {
        try {
            socket = new Socket(host, SysInfo.MUSTREAMER_PORT);
            outputStream = new NeoOutputStream(socket.getOutputStream());
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

    public Thread getAudioSender() {
        return audioSender;
    }

    public void pausePlayer() throws IOException {
        outputStream.writePackage(TrackAlert.PAUSE);
        suspend();
    }

    public void resumePlayer() throws IOException {
        outputStream.writePackage(TrackAlert.RESUME);
        resume();
    }

    @Override
    public void run() {
        File fSound;
        byte[] soundBuffer = new byte[4096];
        Track track;
        AudioTag audioTag;
        SoundData soundData;
        AtomicReference<AudioInputStream> atomicStream = new AtomicReference<>();

        while (true) {
            waitForSongs();
            try {
                fSound = dequeFiles.pollFirst();
                track = Track.getTrack(fSound);
                audioTag = track.getTagInfo();

                outputStream.writePackage(AudioFormatUtils.getFormatData(track.getAudioFormat()));
                //atomicStream.set(track.getDecodedStream());
                audioSender = new AudioSender(track.getDecodedStream(), outputStream);
                audioSender.start();

                ThreadUtil.sleepUntil(audioTag.getDuration()*1000-100);
                outputStream.writePackage(TrackAlert.TRACK_FINISHED);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
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
            if (args[0].equals("-h")) {
                host = args[1];
            }
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
