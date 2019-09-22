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

    public void glueSongFile(File fSound) {
        if (fSound != null)
            dequeFiles.add(fSound);
    }

    public void sendNext() throws IOException {
        outputStream.writeString(PackageHeader.NEXT);
    }

    @Override
    public void run() {
        byte[] soundBytes;
        File fSound;
        while (true) {
            waitForSongs();
            try {
                fSound = dequeFiles.pollFirst();
                outputStream.writeString(PackageHeader.SOUND);
                outputStream.writeString(getSoundFormat(fSound));
                soundBytes = Files.readAllBytes(fSound.toPath());
                outputStream.write(soundBytes);
                //outputStream.writeFile(fSound);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File song = new File("/home/martin/Escritorio/Música/Redrama/Redrama - Just a Day.ogg");
        Client client = new Client();
        client.glueSongFile(song);
        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au3.mp3"));
        client.start();

        try {
            Thread.sleep(30000);
            client.sendNext();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
