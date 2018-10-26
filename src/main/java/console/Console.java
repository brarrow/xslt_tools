package console;

import functionality.Functions;
import main.Main;

import java.util.Scanner;

public class Console {
    public static void mainCircle() throws Exception {
        Scanner in = new Scanner(System.in);
        String[] commands;
        String incorComStr = "Incorrect command. Please, use help or turn off your computer.";
        System.out.println("Tools for xslt making. Type \"h\" to get help.");
        System.out.println("Version " + Main.version + ".");
        System.out.println("Made by brarrow.");
        do {
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
                case "g": {
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

    public static void printHelp() {
        System.out.println("Help for " + Main.version + " version.\n" +
                "Available commands: \n" +
                "h - Get help.\n" +
                "s -a -o - Get screen form. -a: for all files. -o: for one file. All paths in file paths.txt.\n" +
                "g - Load or save information to webstand.\n" +
                "exit - Exit from program.\n");
    }
}
