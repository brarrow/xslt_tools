package repository;

import screenform.FilesIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Git {
    public static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static String getRepositoryPath() {
        String pathAll = FilesIO.getPathAll();
        int posSimi = pathAll.indexOf("SimiDocuments");
        String res = pathAll.substring(0,posSimi+13)+"/";
        return res;
    }
}
