package org.mustream.client.net;

import org.muplayer.audio.Track;
import org.muplayer.audio.info.AudioTag;
import org.mustream.common.audio.AudioFormatUtils;
import org.mustream.common.audio.SoundData;
import org.mustream.common.net.NeoOutputStream;
import org.mustream.common.sys.SysInfo;
import org.mustream.common.sys.TaskRunner;
import org.mustream.common.sys.TrackAlert;
import thread.ThreadUtil;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;

public class Client extends Thread{
    private Socket socket;
    private NeoOutputStream outputStream;

    private Deque<File> dequeFiles;

    private Thread audioSender;

    public Client() throws IOException {
        socket = new Socket("localhost", SysInfo.MUSTREAMER_PORT);
        outputStream = new NeoOutputStream(socket.getOutputStream());
        dequeFiles = new ArrayDeque<>();
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

                atomicStream.set(track.getDecodedStream());

                audioSender = TaskRunner.execute(() -> {
                    int read;
                    try {
                        while ((read = atomicStream.get().read(soundBuffer)) != -1) {
                            if (read < soundBuffer.length) {
                                outputStream.writePackage(Arrays.copyOf(soundBuffer, read));
                            } else {
                                outputStream.writePackage(soundBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                ThreadUtil.sleepUntil(audioTag.getDuration()*1000);
                outputStream.writePackage(TrackAlert.TRACK_FINISHED);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au1.mp3"));

        /*Thread.sleep(10000);

        client.sendNext();
        System.out.println("Next Sended!");
         */
        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au2.mp3"));
        client.start();
    }

}
