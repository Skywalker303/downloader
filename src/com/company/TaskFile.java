package com.company;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Андрей on 08.05.2017.
 */
public class TaskFile {

    private String fileName;
    private ArrayList<Task> tasks;

    private static Map<String, List<String>> filesToDownload = new HashMap<String, List<String>>();
    private static Queue<String> queue = new ConcurrentLinkedQueue<String>();


    public TaskFile(String fileName) {
        this.fileName = fileName;
        this.tasks = new ArrayList<>();
    }

    public char[] getBuffer(String fileName) throws IOException {
        File taskFile = new File(fileName);
        char[] buffer = new char[(int) taskFile.length()];

                FileReader reader = new FileReader(taskFile);
                reader.read(buffer);
                parse(buffer);

                reader.close();

        return buffer;

    }

    public static void processFile(String[] args)  {
        BufferedReader bufferedReader = null;
        try {
            InputStreamReader inputStreamReader = null;
            try {
                inputStreamReader = new InputStreamReader(new FileInputStream(args[0]));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            int counter = 0;
            Map<String, String> fileNames = new HashMap<String,String>();
            try {
                while( (line = bufferedReader.readLine()) != null ){
                    ++counter;
                    final String[] parts = line.split(" ");
                    if( parts.length == 2 ){
                        boolean canAdd = true;
                        if( fileNames.containsKey(parts[1]) ){
                            if( !fileNames.get(parts[1]).equals(parts[0]) ){
                                canAdd = false;
                                System.out.println("Duplicate output file name in line "+counter);
                            }
                        }

                        if( canAdd ){
                            addFile( parts[0], parts[1] );
                            fileNames.put( parts[1], parts[0] );                        }
                    } else {
                        System.out.println("Missing output file name in line "+counter);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addFile(String link, String fn) {
        List<String> l = filesToDownload.get(link);
        if( l == null ){
            l = new ArrayList<String>();
            filesToDownload.put(link, l);
            queue.add(link);
        }
        l.add(fn);
    }
    private void parse(char[] buffer) {
        String fileAsString = new String(buffer);
        String[] words = fileAsString.trim().replaceAll("\r", " ").split(" ");
        for (int i=0;i<words.length-1;i+=2) {
            Task task = new Task(words[i], words[i+1]);
            tasks.add(task);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }


}