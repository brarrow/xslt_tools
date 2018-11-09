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

    private static void updateCasesOnStand(List<String> changedCases) {
        for (String caseName : changedCases) {
            System.out.println("Updating case " + caseName + "...");
            Session session = new Session(caseName);
            updateXslt(session);
            session.saveCase();
            System.out.println("Case updated on stand!");
        }
        System.out.println("All cases updated on stand!");
    }

    public static void updateCasesOnStandWithGit() {
        List<String> changedCases = Git.getChangedCasesGit();
        updateCasesOnStand(changedCases);
    }

    public static void updateCasesOnStandWithStand() {
        List<String> changedCases = Stand.getChangedCasesStand();
        updateCasesOnStand(changedCases);
    }


}
