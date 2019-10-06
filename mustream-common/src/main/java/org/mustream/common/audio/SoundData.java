package org.mustream.common.audio;

import java.io.*;

import static org.mustream.common.sys.StringUtils.pointSplit;

public class SoundData implements Serializable {
    private final transient File fileSound;
    private final String format;
    private final transient FileInputStream soundIn;

    public SoundData(File fileSound) throws FileNotFoundException {
        this.fileSound = fileSound;
        format = getSoundFormat(fileSound);
        soundIn = new FileInputStream(fileSound);
    }

    public static String getSoundFormat(File fSound) {
        String[] pointSplit = pointSplit(fSound.getName());
        return pointSplit[pointSplit.length-1].toLowerCase();
    }

    public int readBytes(byte[] buffer) {
        try {
            return soundIn.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public File getFileSound() {
        return fileSound;
    }

    public String getFormat() {
        return format;
    }

    public FileInputStream getSoundIn() {
        return soundIn;
    }
}
