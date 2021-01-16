package org.mustream.client.net;

import org.mustream.common.PackageHeader;
import org.mustream.common.io.MSOutputStream;
import org.mustream.common.net.MustreamPackage;
import org.mustream.common.net.NeoOutputStream;

import javax.sound.sampled.AudioInputStream;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class AudioSender extends Thread {
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

    private InputStream openFileStream() throws IOException {
        return new FileInputStream(audioFile);
    }

    private RandomAccessFile readAudioFile() throws FileNotFoundException {
        return new RandomAccessFile(audioFile, "r");
    }

    @Override
    public void run() {
        try {
            final byte[] audioBuffer = new byte[BUFF_SIZE];
            int read;
            final RandomAccessFile fileReader = readAudioFile();

            outputStream.writeInt(PackageHeader.SOUND);
            outputStream.writeInt((int) audioFile.length());
            while ((read = fileReader.read(audioBuffer)) != -1 && !isInterrupted()) {
                outputStream.writeAudio(read == BUFF_SIZE
                        ? audioBuffer : Arrays.copyOf(audioBuffer, read));
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
