package org.mustream.client.net;

import org.mustream.common.PackageHeader;
import org.mustream.common.net.NeoOutputStream;
import org.mustream.common.sys.SysInfo;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.*;

import static org.mustream.common.sys.StringUtils.pointSplit;

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

    private String getSoundFormat(File fSound) {
        String[] pointSplit = pointSplit(fSound.getName());
        return pointSplit[pointSplit.length-1].toLowerCase();
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

    public void sendNext() throws IOException {
        outputStream.writeString(PackageHeader.NEXT);
    }

    @Override
    public void run() {
        byte[] soundBytes;
        File fSound;

        FileInputStream fileIn;
        byte[] soundBuffer = new byte[4096];
        int read;

        while (true) {
            waitForSongs();
            try {
                fSound = dequeFiles.pollFirst();
                fileIn = new FileInputStream(fSound);
                outputStream.writeString(PackageHeader.SOUND);
                outputStream.writeString(getSoundFormat(fSound));

                while ((read = fileIn.read(soundBuffer)) != -1) {
                    if (read < soundBuffer.length) {
                        soundBuffer = Arrays.copyOf(soundBuffer, read);
                    }
                    outputStream.writeString(PackageHeader.AUDIO_DATA);
                    outputStream.write(soundBuffer);
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();

        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au1.mp3"));

        Thread.sleep(10000);

        client.sendNext();
        System.out.println("Next Sended!");
        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au2.mp3"));

        client.start();
    }

}
