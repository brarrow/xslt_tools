package files;


import main.Main;
import screenform.Functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FilesIO {
    public static final String tmp = System.getProperty("java.io.tmpdir");
    public static String path;
    public static String inFileName;
    public static String outFileName;
    public static String input;
    public static Path out;
    private static String pathAll;


    public static String getPathAll() {
        return pathAll;
    }

    public static void forAllXSLT() {
        FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes) {
                if (visitedFile.toFile().getPath().endsWith(".xslt") & visitedFile.getParent().toFile().getPath().endsWith("xslt")) {
                    if (visitedFile.getParent().toFile().getPath().contains("84 Emergency call form")) {
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

    public static void readPathsFromTxt() {
        try {
            BufferedReader inp = new BufferedReader(new FileReader("./paths.txt"));
            String line = inp.readLine();
            while (line != null) {
                if (line.startsWith("directory_win=") & Main.windows) {
                    pathAll = line.replace("directory_win=", "").trim();
                }
                if (line.startsWith("directory_lin=") & !Main.windows) {
                    pathAll = line.replace("directory_lin=", "").trim();
                }
                if (line.startsWith("file=")) {
                    line = line.replace("file=", "").trim();
                    int posFileName = line.lastIndexOf(Main.windows ? "\\" : "/") + 1;
                    path = line.substring(0, posFileName);
                    inFileName = line.substring(posFileName);
                }
                line = inp.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(String varInp, String varOut) {
        input = varInp;
        out = Paths.get((varOut));
    }

    public static void writeToFile(List<String> rows) {
        try {
            Files.write(out, rows, Charset.forName("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readXslt(String path) {
        Charset encoding = Charset.forName("UTF-8");
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return (new String(encoded, encoding)).replace("\r", "");
        } catch (Exception ignored) {
        }
        return "";
    }
}
