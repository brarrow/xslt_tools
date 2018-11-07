package main;

import console.Console;
import screenform.FilesIO;
import webstand.Session;
import webstand.Updater;

public class Main {
    public static boolean allFiles;
    public static boolean windows = true;
    public static String version = "0.2.2";

    public static void main(String[] args) throws Exception {
        //Console.mainCircle();
//        Session ses = new Session("test_test");
//        ses.loadCase();
//        ses.setXsltString("test" + ses.getXsltString());
//        ses.setPlaceholdersString("тесе"+ses.getPlaceholdersString());
//        ses.setPrintparamString("see"+ses.getPrintparamString());
//        ses.setXmlString("lkjljl"+ses.getXmlString());
//        ses.saveCase();
        FilesIO.readPathsFromTxt();
        Updater.updateCaseNames();
        Console.mainCircle();
    }
}
