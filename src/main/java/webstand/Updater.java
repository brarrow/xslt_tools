package webstand;

import files.FilesIO;
import repository.Git;
import webstand.cases.CasesFunctions;

import java.util.List;

public class Updater {
    public static void updateXslt(Session session) {
        String pathToCase = CasesFunctions.findPathWithCase(session.getCaseName());
        String newXslt = FilesIO.readXslt(pathToCase);
        session.setXsltString(newXslt);
    }

    public static void updateCaseOnStand(String caseName) {
        System.out.println("Updating case " + caseName + "...");
        Session session = new Session(caseName);
        updateXslt(session);
        session.saveCase();
        System.out.println("Case updated on stand!");
    }

    public static void updateCasesOnStand(List<String> changedCases) {
        for (String caseName : changedCases) {
            updateCaseOnStand(caseName);
        }
        System.out.println("All cases updated on stand!");
    }


}
