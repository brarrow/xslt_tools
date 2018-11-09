package webstand;

import console.Console;
import files.FilesIO;
import org.openqa.selenium.remote.server.handler.interactions.touch.Up;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static webstand.cases.CasesFunctions.findPathWithCase;
import static webstand.cases.CasesFunctions.getAllCases;

public class Stand {
    public static void updateChangedCasesStand() {
        List<String> allCases = getAllCases();
        System.out.println("Looking for updates...");
        int counter = 1;
        int all = allCases.size();
        for (String caseName : allCases) {
            System.out.print("\r"+counter++ + "/" + all);
            Session session = new Session(caseName);
            String localXslt = FilesIO.readXslt(findPathWithCase(caseName));
            if (!localXslt.equals(session.getXsltString())) {
                System.out.println("\r " + showDiffCommand(session.getXsltString(),localXslt));
                System.out.print("\rUpdate " + caseName + "? [y/n]: ");
                if((new Scanner(System.in)).next().equals("y")) {
                    Updater.updateXslt(session);
                    session.saveCase();
                    System.out.println("Updated!");
                }
            }
        }
        System.out.println();
    }

    public static String showDiffCommand(String first, String second) {
        try {
            File firstFile = new File(FilesIO.tmp+"/first.txt");
            BufferedWriter bufRF = new BufferedWriter(new FileWriter(firstFile));
            bufRF.write(first);
            bufRF.close();

            File secondFile = new File(FilesIO.tmp+"/second.txt");
            BufferedWriter bufRS = new BufferedWriter(new FileWriter(secondFile));
            bufRS.write(second);
            bufRS.close();

            return Console.executeCommand("colordiff first.txt second.txt",FilesIO.tmp);

        }
        catch (Exception e) {
            e.printStackTrace();
            return "Error showing!";
        }
    }
}
