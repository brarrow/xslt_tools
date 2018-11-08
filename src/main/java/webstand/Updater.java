package webstand;

import java.lang.String;

import repository.Git;
import screenform.FilesIO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Scanner;

public class Updater {
    public static void updateXslt(Session session) {
        String pathToCase = findPathWithCase(session.getCaseName());
        String newXslt = readFile(pathToCase,Charset.forName("UTF-8"));
        session.setXsltString(newXslt);

    }

    static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }catch (Exception e){}
        return "";
    }

    public static void updateCasesOnStand(){
        List<String> changedCases = Git.getChangedCases();
        for(String caseName: changedCases) {
            System.out.println("Updating case " + caseName + "...");
            Session session = new Session(caseName);
            updateXslt(session);
            session.saveCase();
            System.out.println("Case updated on stand!");
        }
        System.out.println("All cases updated on stand!");
    }

    public static void updateCaseNames() {
        File caseNames = new File("cases.txt");
        try {
            PrintWriter printWriter = new PrintWriter(caseNames);
            printWriter.close();
        }
        catch (Exception e) {}
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
                        toAppend += "test_"+caseNow.replaceAll(" ","") + " ; " + filePath;
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

    public static String findCaseWithPath(String path) {
        File file = new File("cases.txt");
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(path)) {
                    String res = line.substring(0,line.indexOf(" ; ")).replaceAll(" ", "");
                    return res;
                }
            }
        } catch(FileNotFoundException e) {}
        return "";
    }

    public static String findPathWithCase(String caseName) {
        File file = new File("cases.txt");
        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.contains(caseName)&!(line.contains(caseName+"s"))) {
                    return line.substring(line.indexOf(" ; ")+3, line.length());
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
