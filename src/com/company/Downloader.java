package com.company;

/**
 * Created by Андрей on 14.05.2017.
 */
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class Downloader implements Runnable {
    private String url;
    private long startTimeStamp;
    private long endTimeStamp;
    private long contentSize;
    public Downloader(String url) {
        this.url = url;
    }
    long getDownloadTime() {
        return endTimeStamp - startTimeStamp;
    }
    long getDownloadSize() {
        return contentSize;
    }
    public void run() {
        System.out.println("Download started.");
        try {
            URL url = new URL(this.url);
            contentSize = 0;
            startTimeStamp = System.nanoTime();
            DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(url.openStream()));
            String line;
            while ((line = dataInputStream.readLine()) != null)
                contentSize += line.length();
            endTimeStamp = System.nanoTime();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.printf("Download finished. %d bytes in %.4f seconds.\n", getDownloadSize(), getDownloadTime() / 1000000000d);
    }
}


 class Tutorial002 {
    public static void main(String[] args) throws InterruptedException {
        String [] urls = {
                "http://nibler.ru/uploads/users/2012-02-06/%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D1%81%D0%BE%D0%B2%D1%81%D0%B5%D0%BC-%D0%9C%D0%B8%D0%BB%D1%8B%D0%B5-%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B_1052308865.jpg",
                "http://nibler.ru/uploads/users/2012-02-06/%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D1%81%D0%BE%D0%B2%D1%81%D0%B5%D0%BC-%D0%9C%D0%B8%D0%BB%D1%8B%D0%B5-%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B_9561922673.jpg",
                "http://nibler.ru/uploads/users/2012-02-06/%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D1%81%D0%BE%D0%B2%D1%81%D0%B5%D0%BC-%D0%9C%D0%B8%D0%BB%D1%8B%D0%B5-%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B_828586554.jpg",
                "http://nibler.ru/uploads/users/2012-02-06/%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D1%81%D0%BE%D0%B2%D1%81%D0%B5%D0%BC-%D0%9C%D0%B8%D0%BB%D1%8B%D0%B5-%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B_331998436.jpg",
                "http://nibler.ru/uploads/users/2012-02-06/%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D1%81%D0%BE%D0%B2%D1%81%D0%B5%D0%BC-%D0%9C%D0%B8%D0%BB%D1%8B%D0%B5-%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B-%D0%BA%D0%B0%D1%80%D1%82%D0%B8%D0%BD%D0%BA%D0%B8%20%D0%BA%D0%BE%D0%BC%D0%B8%D0%BA%D1%81%D1%8B_46319360.jpg"
        };
        List<Downloader> downloaders = new ArrayList<Downloader>();
        List<Thread> threads = new ArrayList<Thread>();
        for (String url : urls) {
            Downloader downloader = new Downloader(url);
            downloaders.add(downloader);
            Thread thread = new Thread(downloader);
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads)
            thread.join();
        System.out.println("All downloads are finished. ");
        for (Downloader downloader : downloaders) {
            System.out.printf("%.4f bytes per second.\n", (double)(downloader.getDownloadSize() * 1000000000) / (double)downloader.getDownloadTime());
        }
    }
}
