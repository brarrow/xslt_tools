package main;

import console.Console;
import files.FilesIO;
import webstand.cases.CasesFunctions;

public class Main {
    public static final String version = "0.4.4";
    public static boolean windows = true;

    public static void main(String[] args) {
        if (System.getProperty("os.name").contains("Linux")) {
            windows = false;
        }

        FilesIO.readPathsFromTxt();
        CasesFunctions.updateCaseNames();
        //Docx.readDocx("C:\\Users\\useri\\Documents\\Repos\\SimiDocuments\\Ambulatory\\Adult\\cct = 21973 Gastroenterologist\\doc\\OpenEHR composition specification - Gastroenterologist examination.docx");
        Console.mainCircle();
    }
}
