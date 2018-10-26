package console;

import functionality.Functions;

import java.util.Scanner;

public class Console {
    public static void mainCircle() throws Exception {
        Scanner in = new Scanner(System.in);
        String command = "";
        do {
            command = in.nextLine();
            switch (command) {
                case "s": {
                    Functions.getScreenForm();
                }
                case "g": {

                }
            }
        } while (!command.equals("e"));
    }
}
