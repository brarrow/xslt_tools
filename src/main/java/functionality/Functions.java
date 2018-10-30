package functionality;

import console.Console;
import main.Main;
import screenform.FilesIO;
import screenform.JDOMProcessing;
import screenform.Processing;

import java.io.File;

public class Functions {
    public static void getScreenForm(boolean forAll) throws Exception {
        Main.allFiles = forAll;
        FilesIO.readPathsFromTxt();

        if (Main.allFiles) {
            FilesIO.forAllXSLT();
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
        System.out.println("Done: " + Console.good + "/" + Console.all);

    }

    public static void operationsForScreenForm(String varInput, String varOutput) throws Exception {
        Console.all++;
        FilesIO.init(varInput, varOutput);
        Processing.processXSLT();
        System.out.println("Processing done!");
        JDOMProcessing.processXSLT();
        System.out.println("JDOM processing done!\n");
        Console.good++;
    }

}
