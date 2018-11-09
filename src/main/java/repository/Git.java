package repository;

import console.Console;
import files.FilesIO;
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

    public static String getRepositoryPath() {
        String pathAll = FilesIO.getPathAll();
        int posSimi = pathAll.indexOf("SimiDocuments");
        String res = pathAll.substring(0,posSimi+13);
        return res;
    }
}
