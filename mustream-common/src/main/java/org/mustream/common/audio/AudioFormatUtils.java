package org.mustream.common.audio;

import javax.sound.sampled.AudioFormat;
import java.util.HashMap;

public class AudioFormatUtils {
    public static AudioFormat getAudioFormat(FormatData formatData) {
        return new AudioFormat(
                new AudioFormat.Encoding(formatData.getEncoding()),
                formatData.getSampleRate(),
                formatData.getSampleSizeInBytes(),
                formatData.getChannels(),
                formatData.getFrameSize(),
                formatData.getFrameRate(),
                formatData.isBigEndian(),
                formatData.getProperties()
        );
    }

    public static FormatData getFormatData(AudioFormat audioFormat) {
        return new FormatData(
                audioFormat.getEncoding().toString(),
                audioFormat.getSampleRate(),
                audioFormat.getSampleSizeInBits(),
                audioFormat.getChannels(),
                audioFormat.getFrameSize(),
                audioFormat.getFrameRate(),
                audioFormat.isBigEndian(),
                new HashMap<>()
        );
    }
}
