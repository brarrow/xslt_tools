package monitoring;

import files.FilesIO;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Collectors;

public class MonitScreen {
    static boolean checkXsltIsActual(String pathPrint, String pathScreen) {
        try {
            String md5InFile = Files.readAllLines(new File(pathScreen).toPath(), Charset.forName("UTF-8")).stream()
                    .filter(str -> str.contains("md5:"))
                    .map(str -> str.replaceAll("<!--md5:", "").replaceAll("-->", ""))
                    .collect(Collectors.joining());
            InputStream is = Files.newInputStream(Paths.get(pathPrint));
            String md5Real = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            return md5InFile.equals(md5Real);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void checkAll() {
        FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes) {
                if (visitedFile.toFile().getPath().endsWith(".xslt") & visitedFile.getParent().toFile().getPath().endsWith("xslt_screen")) {
                    if (visitedFile.getParent().toFile().getPath().contains("84 Emergency call form")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String pathScreen = visitedFile.toFile().getPath();
                    String pathPrint = visitedFile.getParent().toFile().getParent();
                    String screenFileName = visitedFile.getFileName().toString();
                    String printFileName = screenFileName.replaceFirst(".screen.xslt", ".xslt");
                    if (Main.windows) {
                        pathPrint += "\\xslt\\" + printFileName;
                    } else {
                        pathPrint += "/xslt/" + printFileName;
                    }
                    try {
                        if (checkXsltIsActual(pathPrint, pathScreen)) {
                            System.out.println("Good " + pathScreen);
                        } else
                            System.out.println("Bad " + pathScreen);

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("!!!Error! " + FilesIO.path + FilesIO.inFileName + "!!!\n");
                        System.out.flush();
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        };
        FileSystem fileSystem = FileSystems.getDefault();
        Path rootPath = fileSystem.getPath(FilesIO.getPathAll());
        try {
            Files.walkFileTree(rootPath, simpleFileVisitor);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
