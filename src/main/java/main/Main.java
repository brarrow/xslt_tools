package main;

import console.Console;
import files.FilesIO;
import webstand.cases.CasesFunctions;

public class Main {
    public static boolean allFiles;
    public static boolean windows = true;
    public static String version = "0.3";

    public static void main(String[] args) throws Exception {
        if(System.getProperty("os.name").contains("Linux")) {
            windows = false;
        }
        FilesIO.readPathsFromTxt();
        CasesFunctions.updateCaseNames();
        Console.mainCircle();
    }
}
