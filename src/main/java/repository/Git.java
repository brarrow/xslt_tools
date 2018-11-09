package repository;

import files.FilesIO;
import webstand.cases.CasesFunctions;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Git {
    public static String executeCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.directory(new File(Git.getRepositoryPath()));
        try {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            return result;
        }
        catch (Exception e){}
        return "";

    }

    public static List<String> getChangedCasesGit() {
        String[] status = executeCommand("git status").split("\n");
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
        String res = pathAll.substring(0,posSimi+13)+"/";
        return res;
    }
}
