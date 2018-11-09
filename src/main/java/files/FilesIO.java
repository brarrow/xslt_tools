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
    public static String path;
    private static String pathAll;
    public static String inFileName;
    public static String outFileName;
    public static String input;
    public static Path out;

    public static String getPath() {
        return path;
    }

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

    public static void readPathsFromTxt() throws Exception {
        BufferedReader inp = new BufferedReader(new FileReader("./paths.txt"));
        String line = inp.readLine();
        while (line != null) {
            if (line.startsWith("directory=")) {
                pathAll = line.replace("directory=", "").trim();
            }
            if (line.startsWith("file=")) {
                line = line.replace("file=", "").trim();
                int posFileName = line.lastIndexOf("\\") + 1;
                path = line.substring(0, posFileName);
                inFileName = line.substring(posFileName);
            }
            line = inp.readLine();
        }
    }

    public static void init(String varInp, String varOut) {
        input = varInp;
        out = Paths.get((varOut));
    }

    public static void writeToFile(List<String> rows) throws Exception {
        Files.write(out, rows, Charset.forName("UTF-8"));
    }

    public static String readXslt(String path) {
        Charset encoding = Charset.forName("UTF-8");
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return (new String(encoded, encoding)).replace("\r", "");
        } catch (Exception e) {
        }
        return "";
    }
}
