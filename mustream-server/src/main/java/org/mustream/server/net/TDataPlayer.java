package org.mustream.server.net;

import org.muplayer.audio.Track;
import org.muplayer.audio.formats.*;
import org.mustream.common.net.NeoInputStream;
import org.mustream.common.sys.SysInfo;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import static java.nio.file.StandardOpenOption.APPEND;

public class TDataPlayer extends Thread {
    private NeoInputStream inputStream;
    private File fileSound;
    private String format;

    private Track track;

    public TDataPlayer(NeoInputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.format = inputStream.readString();
        fileSound = new File(SysInfo.SERVER_FOLDER, new Date().getTime()+"."+format);
        fileSound.createNewFile();
    }

    private void waitForBytes() {
        while (fileSound.length() == 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveBytes() throws IOException {
        Files.write(fileSound.toPath(), inputStream.readAllBytes(), APPEND);
    }

    public void finishTrack() {
        track.finish();
    }

    @Override
    public void run() {
        try {
            System.out.println("Data Player started");
            waitForBytes();
            NeoInputStream soundIn = new NeoInputStream(new FileInputStream(fileSound));

            switch (format) {
                case "mp3":
                    track = new MP3Track(soundIn);
                    break;
                case "ogg":
                    track = new OGGTrack(soundIn);
                    break;

                case "m4a":
                case "aac":
                    track = new M4ATrack(soundIn);
                    break;

                case "flac":
                    track = new FlacTrack(soundIn);
                    break;

                default:
                    track = new PCMTrack(soundIn);
                    break;
            }
            track.start();

            while (!track.isFinished()) {
                sleep(10);
            }
        } catch (LineUnavailableException | IOException | UnsupportedAudioFileException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
