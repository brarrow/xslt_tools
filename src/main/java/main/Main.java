package main;

import console.Console;
import files.FilesIO;
import webstand.cases.CasesFunctions;

public class Main {
    public static boolean allFiles;
    public static boolean windows = true;
    public static String version = "0.2.2";

    public static void main(String[] args) throws Exception {
        FilesIO.readPathsFromTxt();
        CasesFunctions.updateCaseNames();
        Console.mainCircle();
    }
}
