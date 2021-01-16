package org.mustream.client.test;

import org.mustream.client.net.Client;
import org.mustream.client.net.ConnectionManager;

import java.io.File;
import java.io.IOException;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String host = "localhost";
        final Client client = ConnectionManager.connectTo(host);

        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au2.mp3"));
        client.start();


        System.out.println("Before 5 seconds");
        Thread.sleep(5000);
        System.out.println("After 5 seconds");

        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au1.mp3"));

        System.out.println("Before 5 seconds");
        Thread.sleep(5000);
        System.out.println("After 5 seconds");

        client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
                "OrangePlayMusic/muplayer/audio/mp3/au3.mp3"));
        //Thread.sleep(5000);
        //client.pausePlayer();
        //Thread.sleep(3000);
        //client.resumePlayer();

        /*Thread.sleep(10000);

        client.sendNext();
        System.out.println("Next Sended!");
         */
        //client.glueSongFile(new File("/home/martin/Dropbox/Java/Proyectos/IntelliJ/OrangePlayerProject/" +
        //        "OrangePlayMusic/muplayer/audio/mp3/au2.mp3"));
    }
}
