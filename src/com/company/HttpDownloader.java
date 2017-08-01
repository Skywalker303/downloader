package com.company;

/**
 * Created by Андрей on 08.05.2017.
 */

import java.net.*;
import java.io.*;
import java.util.concurrent.Callable;


public class HttpDownloader  {

        static class DownloadThread extends Thread implements Callable {

        Socket socket = null;
        PrintWriter pwriter = null;
        DataInputStream dataIn = null;
        DataOutputStream dataOut = null;
        String targetHost = "";
        String target = "";
        byte[] rangeData = null;
        int numthreads = 0;
        int threadNo = 0;
        int start = 0;
        int end = 0;
        long startTime=0;
        long endTime=0;
        long contentSize=0;
        int count=0;
        String fileName = "";


        DownloadThread(String[] args, String targetHost, String target, int numthreads, int threadNo, int start, int end,long startTime,long endTime,long contentSize) {
            super();
            this.fileName = args[0];
            this.targetHost = targetHost;
            this.target = target;
            this.rangeData = new byte[(end-start)+1];
            this.startTime=startTime;
            this.endTime= endTime;
            this.contentSize= contentSize;
            this.numthreads = numthreads;
            this.threadNo = threadNo;
            this.start = start;
            this.end = end;

            try {
                this.socket = new Socket(targetHost, 80);
                this.pwriter = new PrintWriter(socket.getOutputStream(), true);
                this.dataIn = new DataInputStream(socket.getInputStream());
                this.dataOut = new DataOutputStream(new FileOutputStream(new File("PART_" + threadNo)));
            }
            catch (Exception e) {
                System.out.println("Unhandled exception");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        TaskFile taskFile = new TaskFile(fileName);

            public long getDownloadTime() {
                return endTime-startTime;
            }

            public long getDownloadSize() {
                return contentSize;
            }


            @Override
            public Long call() throws Exception {
            try {
                taskFile.getBuffer(fileName);
                for (Task task : taskFile.getTasks()){
                target = task.getPath().substring(task.getPath().substring(7).indexOf('/')+7);
                pwriter.println("GET " + target + " HTTP/1.1");
                pwriter.println("Range: bytes=" + start + "-" + end);
                pwriter.println("Host: " + targetHost);
                pwriter.println("");
                while ( (count = dataIn.readLine().length()) != 0 ) {contentSize += count;}
                dataIn.readFully(rangeData);
                dataOut.write(rangeData, 0, rangeData.length);}

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("Unhandled exception");
                e.printStackTrace();
                System.exit(-1);
            }
            return contentSize;
        }

            public long getContentSize( ) {
                return contentSize;
            }

            public long getEndTime() {
                return endTime;
            }

            public long getStartTime() {
                return startTime;
            }


        }
}