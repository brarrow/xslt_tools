package main;

import console.Console;
import files.FilesIO;
import webstand.cases.CasesFunctions;

public class Main {
    public static final String version = "0.5.1";
    public static boolean windows = true;


    public static void main(String[] args) {
        if (System.getProperty("os.name").contains("Linux")) {
            FilesIO.delim = "/";
            windows = false;
        } else {
            FilesIO.delim = "\\\\";
        }

        FilesIO.readPathsFromTxt();
        CasesFunctions.updateCaseNames();
        //Docx.readDocx("C:\\Users\\useri\\Documents\\Repos\\SimiDocuments\\Ambulatory\\Adult\\cct = 21973 Gastroenterologist\\doc\\OpenEHR composition specification - Gastroenterologist examination.docx");
        Console.mainCircle();
    }
}