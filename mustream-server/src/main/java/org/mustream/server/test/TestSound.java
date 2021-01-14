package org.mustream.server.test;

import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.muplayer.audio.Track;
import org.muplayer.audio.codec.DecodeManager;
import org.muplayer.audio.formats.MP3Track;
import org.muplayer.audio.info.AudioTag;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSound {
    public static void main(String[] args) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException, UnsupportedAudioFileException {
        Logger.getGlobal().setLevel(Level.OFF);
        File sound = new File("/home/martin/Escritorio/Música/4K Video Downloader/Worry.mp3");
        AudioTag tagInfo = new AudioTag(sound);
        Track track = Track.getTrack(sound);

        AudioHeader header = tagInfo.getHeader();

        System.out.println("SampleRate: "+header.getSampleRate());
        System.out.println("BitRate: "+header.getBitRate());
        int duration = header.getTrackLength();
        System.out.println("TrackLenght: "+ duration);
        System.out.println("EncodingType: "+header.getEncodingType());
        System.out.println("Format: "+header.getFormat());
        System.out.println("Channels: "+header.getChannels());
        System.out.println("VariableBitRate: "+header.isVariableBitRate());

        AudioFormat audioFormat = track.getDecodedStream().getFormat();
        int sampleRate = header.getSampleRateAsNumber();
        int sampleSizeInBits = audioFormat.getSampleSizeInBits();
        int channels = audioFormat.getChannels();
        System.out.println(audioFormat);

        AudioInputStream decodedStream = track.getDecodedStream();

        int read;
        int readed = 0;
        while ((read = decodedStream.read(new byte[4096]))!=-1) {
            readed+=read;
        }


        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound);

        System.out.println(audioInputStream);

        if (!audioInputStream.markSupported()) {
            System.out.println("If not mark supported");
            AudioInputStream decStream = AudioSystem.getAudioInputStream(audioInputStream);
            System.out.println(decStream);
        }

        /*long fileSize = sound.length();
        long audioSize = Math.round(((double)(sampleRate*sampleSizeInBits*duration)*channels)/8);
        System.out.println("TrackFileLenght: "+fileSize);
        System.out.println("AudioSize: "+Math.round(audioSize));
        System.out.println("DecodedSize: "+readed);*/
    }
}
