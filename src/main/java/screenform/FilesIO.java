package screenform;


import functionality.Functions;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

//Решение проблемы с болеет в течение pediatrist 15-17, pString


public class FilesIO {
    public static String path = "C:\\Users\\useri\\Documents\\Repos\\RefactorSimiDocuments\\Ambulatory\\Child\\cct=23958 Neurologist (1-17)\\xslt";
    public static String pathAll = "C:\\Users\\useri\\Documents\\Repos\\RefactorSimiDocuments\\Ambulatory";
    public static String repos = "C:\\Users\\useri\\Documents\\Repos\\SimiDocuments";
    public static String refac = "C:\\Users\\useri\\Documents\\Repos\\RefactorSimiDocuments";
    public static String inFileName = "openEHR-EHR-COMPOSITION.t_neurologist_examination(1-17).xslt";
    public static String outFileName = "test.xslt";
    public static String input;
    public static Path out;


    public static void forAllXSLT(boolean debug) {
        FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes) {
                if (visitedFile.toFile().getPath().endsWith(".xslt") & visitedFile.getParent().toFile().getPath().endsWith("xslt")) {
                    if (visitedFile.getParent().toFile().getPath().contains("21973")) {
                        return FileVisitResult.CONTINUE;
                    }
                    System.out.println(visitedFile.getParent().toFile().getPath());
                    File xslt_screen = new File(visitedFile.getParent().toFile().getPath() + "_screen");
                    xslt_screen.mkdir();
                    if (Main.windows) {
                        path = visitedFile.getParent().toFile().getPath() + "\\";
                    } else {
                        path = visitedFile.getParent().toFile().getPath() + "/";
                    }
                    inFileName = visitedFile.getFileName().toString();
                    outFileName = inFileName.replaceFirst(".xslt", ".screen.xslt");
                    try {
                        if (Main.windows) {
                            Functions.operationsForScreenForm(path + inFileName, xslt_screen.getPath() + "\\" + outFileName);
                        } else {
                            Functions.operationsForScreenForm(path + inFileName, xslt_screen.getPath() + "/" + outFileName);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("!!!Error! " + path + inFileName + "!!!\n");
                        System.out.flush();
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };
        FileSystem fileSystem = FileSystems.getDefault();
        Path rootPath = fileSystem.getPath(pathAll);
        try {
            Files.walkFileTree(rootPath, simpleFileVisitor);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void init(String varInp, String varOut) {
        input = varInp;
        out = Paths.get((varOut));
    }

    public static String getAbsPath(String el) throws Exception {
        if (Main.windows) {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "\\" + el;
        } else {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "/" + el;
        }
    }

    public static void writeToFile() throws Exception {
        Files.write(out, Processing.rows, Charset.forName("UTF-8"));
    }
}
