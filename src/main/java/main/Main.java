package main;

import console.Console;
import repository.Git;
import screenform.FilesIO;
import webstand.Session;
import webstand.Updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Main {
    public static boolean allFiles;
    public static boolean windows = true;
    public static String version = "0.2.2";

    public static void main(String[] args) throws Exception {
        //Console.mainCircle();
        FilesIO.readPathsFromTxt();
//        Session ses = new Session("test_249s");
//        ses.loadCase();
//        Updater.updateXslt(ses);
//        ses.setCaseName("test_test");
//        ses.saveCase();
        //        ses.saveCase();
        ProcessBuilder processBuilder = new ProcessBuilder("git","status");
        processBuilder.directory(new File(Git.getRepositoryPath()));

        Process process = processBuilder.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        String result = builder.toString();
        result.length();
        Git.executeCommand("cd \"" + Git.getRepositoryPath()+"\"");
        Console.mainCircle();
    }
}
