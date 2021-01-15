package org.mustream.client.net;

import org.mustream.common.io.MSOutputStream;
import org.mustream.common.net.MustreamPackage;
import org.mustream.common.net.NeoOutputStream;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class AudioSender {
    private final File audioFile;
    private final MSOutputStream outputStream;
    private static final int BUFF_SIZE = 10240;

    public AudioSender(File audioFile, OutputStream socketStream) {
        this(audioFile, new MSOutputStream(socketStream));
    }

    public AudioSender(File audioFile, MSOutputStream outputStream) {
        this.audioFile = audioFile;
        this.outputStream = outputStream;
    }

    private ByteArrayInputStream openFileStream() throws IOException {
        return new ByteArrayInputStream(Files.readAllBytes(audioFile.toPath()));
    }

    private byte[] readAudioBytes() throws IOException {
        return Files.readAllBytes(audioFile.toPath());
    }

    public void start() {
        final byte[] audioBuffer = new byte[BUFF_SIZE];
        int read;

        try {
            outputStream.writeAudio(readAudioBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
