package repository;

import console.Console;
import files.FilesIO;
import webstand.cases.CasesFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Git {

    public static List<String> getChangedCasesGit() {
        String[] status = Console.executeCommand(new String[]{"git", "status"}, Git.getRepositoryPath()).split("\n");
        ArrayList<String> result = new ArrayList<>();
        for (String line : status) {
            if (!line.contains(".xslt")) {
                continue;
            }
            if (line.contains("изменено:")) {
                result.add(CasesFunctions.findCaseWithPath(line.replaceAll("изменено: {6}", "")
                        .replaceAll("\t", "")));
            }
            if (line.contains("modified:")) {
                result.add(CasesFunctions.findCaseWithPath(line.replaceAll("modified: {3}", "")
                        .replaceAll("\t", "").replaceAll("\r", "").replace("/", "\\")));
            }
        }
        return result;
    }

    public static String printChangesInCase(String caseName) {
        return Console.executeCommand(new String[]{"git", "diff", CasesFunctions.findPathWithCase(caseName)}, Git.getRepositoryPath());
    }

    public static void commit(String caseName) {
        Console.executeCommand(new String[]{"git", "pull"}, getRepositoryPath());
        Console.executeCommand(new String[]{"git", "add", CasesFunctions.findPathWithCase(caseName)}, getRepositoryPath());
        CasesFunctions.getDoctorAndCct(caseName);
        Console.executeCommand(new String[]{"git", "commit", "-m", generateCommitMsg(caseName)}, getRepositoryPath());
        Console.executeCommand(new String[]{"git", "push"}, getRepositoryPath());
        System.out.println("Message to Jira: ");
        System.out.println("Доработано. Готово к тестированию.");
        System.out.println("Сохранено на стенде " + caseName);
        System.out.println("Ревизия: " + getHashLastCommit().substring(0, 12));
    }

    private static String getHashLastCommit() {
        return Console.executeCommand(new String[]{"git", "rev-parse", "HEAD"}, getRepositoryPath());
    }

    private static String generateCommitMsg(String caseName) {
        System.out.println("Enter addition msgs: (all from another line)");
        Scanner input = new Scanner(System.in);
        StringBuilder msg =
                new StringBuilder("#update artifacts " + CasesFunctions.getDoctorAndCct(caseName)
                        + (caseName.endsWith("s") ? " (screen " : " (print ") + "form).");
        String line;
        while (input.hasNextLine()) {
            line = input.nextLine();
            if (line.isEmpty()) break;
            msg.append("\n");
            msg.append(line);
        }
        return msg.toString();
    }

    private static String getRepositoryPath() {
        String pathAll = FilesIO.getPathAll();
        int posSimi = pathAll.indexOf("SimiDocuments");
        return pathAll.substring(0, posSimi + 13);
    }
}
