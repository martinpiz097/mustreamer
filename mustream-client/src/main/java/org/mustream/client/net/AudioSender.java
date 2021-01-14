package org.mustream.client.net;

import org.mustream.common.net.NeoOutputStream;

import javax.sound.sampled.AudioInputStream;
import java.io.IOException;
import java.util.Arrays;

public class AudioSender extends Thread {

    private final AudioInputStream decodedStream;
    private final NeoOutputStream outputStream;

    public AudioSender(AudioInputStream decodedStream, NeoOutputStream outputStream) {
        this.decodedStream = decodedStream;
        this.outputStream = outputStream;
        setName("AudioSender "+decodedStream.getFormat());
    }

    @Override
    public void run() {
        byte[] audioBuffer = new byte[4096];
        int read;
        try {
            while ((read = decodedStream.read(audioBuffer)) != -1) {
                if (read < audioBuffer.length) {
                    outputStream.writePackage(Arrays.copyOf(audioBuffer, read));
                } else {
                    outputStream.writePackage(audioBuffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
