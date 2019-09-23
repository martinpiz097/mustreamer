package org.mustream.server.net;

import org.mustream.common.PackageHeader;
import org.mustream.common.net.NeoInputStream;
import org.mustream.server.data.SoundData;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

public class TClient extends Thread {
    private Socket cliSock;
    private NeoInputStream inputStream;

    private TStreamPlaying streamPlaying;
    private TDataPlayer dataPlayer;

    public TClient(Socket cliSock) throws IOException {
        this.cliSock = cliSock;
        inputStream = new NeoInputStream(cliSock.getInputStream());
        streamPlaying = new TStreamPlaying();
    }

    private void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            streamPlaying.start();
            String header;
            while (true) {
                header = inputStream.readString();
                System.out.println("Header: "+header);
                switch (header) {
                    case PackageHeader.SOUND:
                        dataPlayer = new TDataPlayer(inputStream);
                        dataPlayer.start();
                        break;

                    case PackageHeader.AUDIO_DATA:
                        dataPlayer.saveBytes();
                        break;

                    case PackageHeader.NEXT:
                        if (dataPlayer != null)
                            dataPlayer.finishTrack();
                        break;
                }
                sleep();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
