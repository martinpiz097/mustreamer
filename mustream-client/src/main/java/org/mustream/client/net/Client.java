package org.mustream.client.net;

import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.muplayer.audio.Track;
import org.muplayer.audio.info.AudioTag;
import org.muplayer.thread.TaskRunner;
import org.mustream.common.PackageHeader;
import org.mustream.common.audio.SoundData;
import org.mustream.common.net.NeoOutputStream;
import org.mustream.common.sys.SysInfo;
import thread.ThreadUtil;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class Client extends Thread{
    private Socket socket;
    private NeoOutputStream outputStream;

    private Deque<File> dequeFiles;

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

    @Override
    public void run() {
        File fSound;

        byte[] soundBuffer = new byte[4096];
        Track track;
        AudioTag audioTag;
        SoundData soundData;

        while (true) {
            waitForSongs();
            try {
                fSound = dequeFiles.pollFirst();
                soundData = new SoundData(fSound);
                audioTag = new AudioTag(fSound);

                outputStream.writeString(PackageHeader.SOUND);
                outputStream.writeObject(soundData);

                SoundData finalSoundData = soundData;
                TaskRunner.execute(() -> {
                    int read;
                    while ((read = finalSoundData.readBytes(soundBuffer)) != -1) {
                        try {
                            outputStream.writeString(PackageHeader.AUDIO_DATA);
                            if (read < soundBuffer.length) {
                                outputStream.write(Arrays.copyOf(soundBuffer, read));
                            }
                            else {
                                outputStream.write(soundBuffer);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ThreadUtil.sleepUntil(audioTag.getDuration()*1000);

            } catch (IOException | CannotReadException | ReadOnlyFileException | TagException | InvalidAudioFrameException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {

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
