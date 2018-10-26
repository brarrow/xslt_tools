package functionality;

import main.Main;
import screenform.FilesIO;
import screenform.JdomProcessing;
import screenform.Processing;

import java.io.File;

public class Functions {
    public static void getScreenForm(boolean forAll) throws Exception {
        Main.allFiles = forAll;
        FilesIO.readPathsFromTxt();

        if (Main.allFiles) {
            FilesIO.forAllXSLT(true);
        } else {
            if (Main.windows) {
                FilesIO.input = FilesIO.path + FilesIO.inFileName;
            } else {
                FilesIO.input = FilesIO.path + FilesIO.inFileName;
            }
            FilesIO.out = new File(new File(FilesIO.path).getPath() + "_screen").toPath();
            FilesIO.out.toFile().mkdir();
            FilesIO.outFileName = FilesIO.inFileName.replaceFirst(".xslt", ".screen.xslt");
            if (Main.windows) {
                operationsForScreenForm(FilesIO.input, FilesIO.out.toString() + "\\" + FilesIO.outFileName);
            } else {
                operationsForScreenForm(FilesIO.input, FilesIO.out.toString() + "/" + FilesIO.outFileName);
            }
        }
        System.out.println("Done: " + Main.good + "/" + Main.all);

    }

    public static void operationsForScreenForm(String varInput, String varOutput) throws Exception {
        Main.all++;
        FilesIO.init(varInput, varOutput);
        //JdomProcessing.preprocess();
        Processing.processXSLT();
        System.out.println("Processing done!");
        JdomProcessing.processXSLT();
        System.out.println("JDOM processing done!\n");
        Main.good++;
    }

}
