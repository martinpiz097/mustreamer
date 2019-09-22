package org.mustream.server.data;

import org.muplayer.audio.Track;
import org.mustream.common.net.NeoInputStream;
import org.mustream.common.sys.SysInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Date;

public class SoundData {
    private File fileSound;
    private Track soundTrack;

    public SoundData(NeoInputStream inputStream) throws IOException {
        String format = inputStream.readString();
        //File tmpFile = inputStream.readFile();
        //byte[] data = Files.readAllBytes(tmpFile.toPath());
        //tmpFile.delete();
        byte[] data = inputStream.readAllBytes();
        loadSoundData(data, format);
    }

    public SoundData(byte[] soundBytes, String soundFormat) throws IOException {
        loadSoundData(soundBytes, soundFormat);
    }

    private void loadSoundData(byte[] soundBytes, String soundFormat) throws IOException {
        fileSound = new File(SysInfo.SERVER_FOLDER, new Date().getTime()+"."+soundFormat);
        fileSound.createNewFile();
        Files.write(fileSound.toPath(), soundBytes, StandardOpenOption.TRUNCATE_EXISTING);
        soundTrack = Track.getTrack(fileSound);
    }

    public File getFileSound() {
        return fileSound;
    }

    public Track getSoundTrack() {
        return soundTrack;
    }
}
