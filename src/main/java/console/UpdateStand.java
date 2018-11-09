package console;

import repository.Git;
import webstand.Stand;
import webstand.Updater;

import java.util.List;
import java.util.Scanner;

public class UpdateStand {
    public static void updateCasesOnStandWithGit() {
        List<String> changedCases = Git.getChangedCasesGit();
        standardUpdaterCases(changedCases);
    }

    private static void standardUpdaterCases(List<String> changedCases) {
        if (changedCases.size() != 0) {
            System.out.println("Cases to update: " + changedCases + "\nUpdate? [y/n]");
            if((new Scanner(System.in)).next().equals("y")){
                Updater.updateCasesOnStand(changedCases);
            }
            else {
                System.out.println("Canceled.");
            }
        }
        else {
            System.out.println("Nothing to update!");
        }
    }
}
