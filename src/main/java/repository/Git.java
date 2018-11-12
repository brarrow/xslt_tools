package repository;

import console.Console;
import console.UpdateStand;
import files.FilesIO;
import webstand.Stand;
import webstand.Updater;
import webstand.cases.CasesFunctions;

import java.util.ArrayList;
import java.util.List;

public class Git {

    public static List<String> getChangedCasesGit() {
        String[] status = Console.executeCommand("git status", Git.getRepositoryPath()).split("\n");
        ArrayList<String> result = new ArrayList<>();
        for(String line : status) {
            if(line.contains("изменено:")){
                result.add(CasesFunctions.findCaseWithPath(line.replaceAll("изменено:      ", "").replaceAll("\t", "")));
            }
        }
        return result;
    }

    public static void commit(String caseName) {
        Console.executeCommand("git add " + CasesFunctions.findPathWithCase(caseName), getRepositoryPath());
        CasesFunctions.getDoctorAndCct(caseName);
        Console.executeCommand("git commit -m " + generateCommitMsg(caseName), getRepositoryPath());
    }

    public static String generateCommitMsg(String caseName) {
        return "#update artifacts " + CasesFunctions.getDoctorAndCct(caseName) + (caseName.endsWith("s")?"(screen ":"(print ") + "form)";
    }

    public static String getRepositoryPath() {
        String pathAll = FilesIO.getPathAll();
        int posSimi = pathAll.indexOf("SimiDocuments");
        String res = pathAll.substring(0,posSimi+13);
        return res;
    }
}
