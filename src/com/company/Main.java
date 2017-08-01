package com.company;


import java.io.*;
import com.company.HttpDownloader.DownloadThread;


public class Main {

    public static void main(String[] args) throws Exception {

        DownloadThread[] threads = null;
        DataInputStream dataIn = null;
        DataOutputStream dataOut = null;
        String buffer = "";
        String fileName = "";
        String targetHost  = "";
        String nameOfDir  = "";
        String pathToWriteDir = new File("").getAbsolutePath();;
        String target = "";
        long startTime=0;
        long endTime=0;
        long contentSize=0;
        int bytesRead = 0;
        int numthreads = 0;
        int filesize = 0;
        int threadNo = 0;
        byte[] bytes = null;


        if (args.length != 4) {
            System.out.printf("ERROR");
            return;
        } else {
            try {
                fileName = String.valueOf(args[0].replaceAll("-", ""));
                nameOfDir = String.valueOf(args[1].replaceAll("-", ""));
                pathToWriteDir = String.valueOf(args[2].replaceAll("-", ""));
                numthreads = Integer.valueOf(args[3].replaceAll("-", ""));
            } catch (Exception exc) {
                System.out.printf("%s", exc.toString());
                return;
            }
        }

        TaskFile taskFile = new TaskFile(fileName);

        taskFile.processFile(args);

        File file = new File(nameOfDir);
        if (!file.exists() || !file.isDirectory()) {
            System.out.printf("Wrong name of directory: %s", nameOfDir);
            return;
        }

        buffer = taskFile.getBuffer(fileName).toString();
        filesize = buffer.length();

        for (Task task : taskFile.getTasks()) {
            targetHost = task.getPath().substring(7, task.getPath().substring(7).indexOf('/')+7);
            target = task.getPath().substring(task.getPath().substring(7).indexOf('/')+7);}
            numthreads = Integer.parseInt(args[3]);

        threads = new DownloadThread[numthreads];
        bytes = new byte[filesize];
        System.out.println("Downloading...\n");
        DownloadThread downloadThread = new DownloadThread(args,targetHost, target, numthreads, threadNo, ((filesize*threadNo)/numthreads), (((filesize*(threadNo+1))/numthreads)-1),startTime,endTime,contentSize);
        for (int i=0; i<numthreads; i++) {
            threads[i] = new DownloadThread(args,targetHost, target, numthreads, i, ((filesize*i)/numthreads), (((filesize*(i+1))/numthreads)-1),startTime,endTime,contentSize);
            startTime = System.nanoTime();
            try {
                threads[i].call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            threads[i].start();
            endTime = System.nanoTime();
            System.out.printf("Downloading by thread number "+(i+1)+" "+"is finished. %d bytes in %.4f seconds.\n", threads[i].getDownloadSize(), threads[i].getDownloadTime()/ 1000000000d );
        }

        for (int i=0; i<numthreads; i++) {
            while (threads[i].isAlive()) {}

         }

        //write thread data to output file
        try {
            dataOut = new DataOutputStream(new FileOutputStream(new File(pathToWriteDir+"\\part.txt")));
            for (int i=0; i<numthreads; i++) {
               dataIn = new DataInputStream(new FileInputStream(new File("PART_"+i)));
                while ( (bytesRead = dataIn.read(bytes, 0, filesize)) != -1 ){
                    dataOut.write(bytes,0 , bytesRead);
                }
            }

        }
        catch (Exception e) {
            System.out.println("Unhandled exception encountered during writing to file .");
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Comleted: 100\n"+
                "Downloaded:"+  " "+taskFile.getTasks().size()+" "+"files"+" "+ downloadThread.call()+" "+"bytes\n"+
                "Time:"+  (endTime-startTime)/1000000000d+" "+"seconds\n"+
                "Average speed:"+ Math.round(downloadThread.call()/((endTime-startTime)/1000000000d))+" "+"bytes/second");
    }

  }

