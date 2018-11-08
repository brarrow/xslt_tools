package main;

import console.Console;
import repository.Git;
import screenform.FilesIO;
import webstand.Session;
import webstand.Updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static boolean allFiles;
    public static boolean windows = true;
    public static String version = "0.2.2";

    public static void main(String[] args) throws Exception {
        FilesIO.readPathsFromTxt();
        Updater.updateCaseNames();
        Console.mainCircle();
    }
}
