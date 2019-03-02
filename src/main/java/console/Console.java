package console;

import main.Main;
import monitoring.MonitScreen;
import screenform.Functions;
import testing.Test;
import webstand.Stand;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Console {
    public static int good = 0;
    public static int all = 0;

    public static void mainCircle() {
        Scanner in = new Scanner(System.in);
        String[] commands;
        String incorComStr = "Incorrect command. Please, use help or turn off your computer.";
        System.out.println("Tools for xslt making. Type \"h\" to get help.");
        System.out.println("Version " + Main.version + ".");
        System.out.println("Made by brarrow.");
        do {
            all = 0;
            good = 0;
            System.out.print("Command: ");
            commands = in.nextLine().split(" ");
            switch (commands[0]) {
                case "s": {
                    switch (commands[1]) {
                        case "-a": {
                            Functions.getScreenForm(true);
                            break;
                        }
                        case "-o": {
                            Functions.getScreenForm(false);
                            break;
                        }
                        default: {
                            System.out.println(incorComStr);
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
                    Test.getHtmlText("/home/brarrow/Рабочий стол/html.html");
                    break;
                }
                case "c": {
                    Functions.changeCurrentFile();
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
                    System.out.println(incorComStr);
                }
            }
        } while (!commands[0].equals("exit"));
    }

    private static void printHelp() {
        System.out.println("Help for " + Main.version + " version.\n" +
                "Available commands: \n" +
                "h - Get help.\n" +
                "c - Change current processing file.\n" +
//                "t - Testing result html" +
                "s -a -o - Get screen form. -a: for all files. -o: for one file. All paths in file paths.txt.\n" +
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
