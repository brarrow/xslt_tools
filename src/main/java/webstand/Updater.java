package webstand;

import main.Main;
import java.lang.String;
import screenform.FilesIO;
import screenform.Functions;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Scanner;

public class Updater {
    public static void updateXslt(Session session) throws Exception {
        updateCaseNames();
        String pathToCase = findPathWithCase(session.getCaseName());

    }
    public static void updateCaseNames() throws Exception {
        File caseNames = new File("cases.txt");
        PrintWriter printWriter = new PrintWriter(caseNames);
        printWriter.close();

        FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes) {
                String filePath = visitedFile.toFile().getPath();
                if (filePath.endsWith(".xslt")) {
                    if (visitedFile.getParent().toFile().getPath().contains("21973")) {
                        return FileVisitResult.CONTINUE;
                    }
                    String caseNow = findCaseInXslt(filePath);
                    try{
                        BufferedWriter writer = new BufferedWriter(new FileWriter(caseNames, true));
                        String toAppend = "";
                        if(Files.readAllLines(caseNames.toPath()).size()!=0){
                            toAppend = "\n";
                        }
                        toAppend += "test_"+caseNow + " ; " + filePath;
                        writer.append(toAppend);
                        writer.close();
                    }
                    catch (Exception e){}
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

    public static String findPathWithCase(String caseName) throws Exception{
        File file = new File("cases.txt");
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(caseName)) {
                    return line.replaceAll(caseName+" ; ","");
                }
            }
        } catch(FileNotFoundException e) {}
        return "";
    }

    public static String findCaseInXslt(String filePath) {
        File file = new File(filePath);
        String caseName="";
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(".xslt_")) {
                    caseName = line.substring(line.indexOf(".xslt_")+6, line.indexOf("{",line.replaceAll(" ","").indexOf(".xslt_")));
                    return caseName;
                }
            }
        } catch(FileNotFoundException e) {}
        return caseName;
    }
}
