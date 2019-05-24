package console;

import files.FilesIO;
import main.Main;
import monitoring.MonitScreen;
import repository.Docx;
import repository.Git;
import screenform.Functions;
import testing.Test;
import webstand.Stand;
import webstand.cases.CasesFunctions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Console {
    public static int good = 0;
    public static int all = 0;
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static String caseStr = null;

    public static void mainCircle() {
        Scanner in = new Scanner(System.in);
        String[] commands;
        System.out.println("Tools for xslt making. Type \"h\" to get help.");
        System.out.println("Version " + Main.version + ".");
        System.out.println("Made by brarrow.");
        do {
            all = 0;
            good = 0;
            try {
                caseStr = CasesFunctions.findCaseWithPath(FilesIO.path);
                System.out.println("\nCurrent case: " + caseStr + ", " + CasesFunctions.getDoctor(caseStr));
                System.out.println(Git.getLastCommitInCase(caseStr));
            } catch (Exception ignored) {
                System.out.println("Can't get case name.");
            }
            System.out.print("Command: ");
            commands = in.nextLine().split(" ");
            switch (commands[0]) {
                case "s": {
                    switch (commands[1]) {
                        case "-o": {
                            Functions.getScreenForm(false);
                            break;
                        }
                        default: {
                            printIncorr();
                        }
                    }
                    break;
                }
                case "ug": {
                    UpdateStand.updateCasesOnStandWithGit();
                    break;
                }
                case "us": {
                    Stand.updateChangedCasesStand();
                    break;
                }
                case "m": {
                    MonitScreen.checkAll();
                    break;
                }
                case "t": {
                    String str = Test.getHtmlText("/home/brarrow/Documents/oxy.html");
                    str.trim();
                    break;
                }
                case "c": {
                    Functions.changeCurrentFile();
                    break;
                }
                case "d": {
                    Docx.openDocWithCase(caseStr);
                    break;
                }
                case "l": {
                    Stand.loadActualXML(caseStr);
                    break;
                }
                case "h": {
                    printHelp();
                    break;
                }
                case "exit": {
                    break;
                }
                default: {
                    printIncorr();
                }
            }
        } while (!commands[0].equals("exit"));
    }

    public static void printMessage(String message, String color) {
        System.out.println(color + message + ANSI_RESET);
    }

    private static void printIncorr() {
        String incorComStr = "Incorrect command.";
        System.out.println(incorComStr);
    }

    private static void printHelp() {
        System.out.println("Help for " + Main.version + " version.\n" +
                "Available commands: \n" +
                "h - Get help.\n" +
                "c - Change current processing file.\n" +
//                "l - Load actual xml from stand.\n" +
                "d - Open documentation for current processing file.\n" +
//                "t - Testing result html" +
                "s -o - Get screen form. -o: for one file. All paths in file paths.txt.\n" +
//                "m - Check actuality of screen forms" +
                "ug - Push updated to git and update stand. (using git changes)\n" +
                "us - Check all stands cases with local files.\n" +
                "exit - Exit from program.\n");
    }

    public static String executeCommand(String[] commands, String directory) {
        ProcessBuilder processBuilder = new ProcessBuilder(commands);

        processBuilder.directory(new File(directory));
        try {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();
        } catch (Exception e) {
            return "Error executing command!";
        }
    }
}
