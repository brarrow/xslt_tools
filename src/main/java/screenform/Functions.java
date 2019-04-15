package screenform;

import console.Console;
import files.FilesIO;
import main.Main;
import webstand.cases.CasesFunctions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Functions {
    public static void getScreenForm(boolean forAll) {
        FilesIO.readPathsFromTxt();

        if (forAll) {
            FilesIO.forAllXSLT();
        } else {
            FilesIO.input = FilesIO.path + FilesIO.inFileName;
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

    public static void operationsForScreenForm(String varInput, String varOutput) {
        Console.all++;
        FilesIO.init(varInput, varOutput);
        Processing.processXSLT();
        System.out.println("Processing done!");
        JDOMProcessing.processXSLT();
        System.out.println("JDOM processing done!\n");
        Console.good++;
    }

    public static void changeCurrentFile() {
        try {
            Scanner userInp = new Scanner(System.in);
            List<String> lines = Files.readAllLines(Paths.get("cases.txt"), StandardCharsets.UTF_8);
            while (true) {
                System.out.println("Changing current file path. Enter new file:");
                String inpFile = userInp.nextLine();
                List<String> found = lines.stream()
                        .filter(el -> el.contains(inpFile))
                        .collect(Collectors.toList());
                if (found.size() >= 1) {
                    System.out.println("Found more that one file. Please, choose need one:");
                    for (int i = 0; i < found.size(); i++) {
                        System.out.println(String.format("%d. %s : %s",
                                i + 1, found.get(i).substring(0, found.get(i).indexOf(" ;")),
                                CasesFunctions.getDoctorAndCct(found.get(i).substring(0, found.get(i).indexOf(" ;"))))
                        );
                    }
                    String needFile = found.get(Integer.valueOf(userInp.nextLine()) - 1);

                    System.out.println(String.format("Change filepath to %s? [y/n]"
                            , CasesFunctions.getDoctorAndCct(needFile.substring(0, needFile.indexOf(" ;")))));
                    Scanner changeBool = new Scanner(System.in);
                    if (changeBool.nextLine().equalsIgnoreCase("y")) {
                        List<String> paths = Files.readAllLines(Paths.get("paths.txt"), StandardCharsets.UTF_8);
                        FileWriter writerPaths = new FileWriter("paths.txt");
                        paths = paths.stream()
                                .map(el -> {
                                    if (el.startsWith("file=")) {
                                        return el.substring(0, el.indexOf("=")) + "="
                                                + needFile.substring(needFile.indexOf(";") + 1).trim();
                                    } else return el;
                                })
                                .collect(Collectors.toList());
                        for (String line : paths) {
                            writerPaths.append(line).append("\n");
                        }
                        writerPaths.close();
                        FilesIO.readPathsFromTxt();
                        System.out.println("Success!");
                        break;
                    } else {
                        System.out.println("Breaking.");
                        changeBool.close();
                        break;
                    }
                } else {
                    System.out.println("Found nothing. Try again? [y/n]");
                    Scanner changeBool = new Scanner(System.in);
                    if (!changeBool.nextLine().equalsIgnoreCase("y")) {
                        break;
                    }
                }
            }
        } catch (IOException io) {
            System.out.println("File not found!");
        }

    }
}
