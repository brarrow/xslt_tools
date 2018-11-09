package webstand;

import files.FilesIO;

import java.util.ArrayList;
import java.util.List;

import static webstand.cases.CasesFunctions.findPathWithCase;
import static webstand.cases.CasesFunctions.getAllCases;

public class Stand {
    public static List<String> getChangedCasesStand() {
        List<String> allCases = getAllCases();
        List<String> changedCases = new ArrayList<>();
        System.out.println("Updating cases...");
        for (String caseName : allCases) {
            System.out.println();
            Session session = new Session(caseName);
            String localXslt = FilesIO.readXslt(findPathWithCase(caseName));
            if (!localXslt.equals(session.getXsltString())) {
                changedCases.add(caseName);
            }
        }
        return changedCases;
    }
}
